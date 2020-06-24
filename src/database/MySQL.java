package database;

import calendar.Appointment;
import calendar.TimeConverter;
import customers.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import reports.ColumnTitle;
import reports.ReportColumn;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class MySQL implements Database {
    
    private final String DB_URL = "jdbc:mysql://url-goes-here"; //redacted for security purposes
    private final String DB_USERNAME = ""; //redacted for security purposes
    private final String DB_PASSWORD = ""; //redacted for security purposes
    
    private final Duration REFRESH_FREQUENCY = Duration.ofMinutes(5);
    
    private User activeUser;
    
    private LocalDateTime lastRefresh = LocalDateTime.now().minus(REFRESH_FREQUENCY);
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    
    private final DatabaseFactory factory;
    private final TimeConverter timeConverter;
    
    public MySQL(DatabaseFactory factory, TimeConverter timeConverter) {
        this.factory = factory;
        this.timeConverter = timeConverter;
    }
    
    private void refreshData(boolean force) {
        LocalDateTime now = LocalDateTime.now();
        if (force || lastRefresh.isBefore(now.minus(REFRESH_FREQUENCY))) {
            lastRefresh = now;
            refreshCustomers();
            refreshUsers();
            refreshAppointments();
        }
    }
    
    private void refreshAppointments() {
        appointments.clear();
        try (Connection connection = getConnection()) {
            ResultSet resultSet = executeSelectStatement(connection, "SELECT * FROM appointment");
            while (resultSet.next()) {
                Customer customer = getCustomerById(resultSet.getInt("customerId"));
                User user = getUserById(resultSet.getInt("userId"));
                appointments.add(factory.existingAppointment(resultSet, customer, user));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void refreshCustomers() {
        customers.clear();
        try (Connection connection = getConnection()) {
            ResultSet resultSet = executeSelectStatement(connection,
                    "SELECT cu.customerId customerId, cu.customerName customerName, a.address address, " +
                    "a.address2 address2, ci.city city, a.postalCode postalCode, co.country country, a.phone phone\n" +
                    "FROM customer AS cu\n" +
                    "INNER JOIN address AS a ON cu.addressId = a.addressId\n" +
                    "INNER JOIN city AS ci ON a.cityId = ci.cityId\n" +
                    "INNER JOIN country AS co ON ci.countryId = co.countryId");
            while (resultSet.next()) {
                customers.add(factory.existingCustomer(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void refreshUsers() {
        try (Connection connection = getConnection()) {
            ResultSet resultSet = executeSelectStatement(connection, "SELECT * FROM user");
            while (resultSet.next()) {
                if (getUserById(resultSet.getInt("userId")) == null) {
                    users.add(factory.existingUser(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private Customer getCustomerById(int id) {
        Customer foundCustomer = null;
        for (Customer customer : customers) {
            if (customer.getCustomerId() == id) {
                foundCustomer = customer;
                break;
            }
        }
        return foundCustomer;
    }
    
    private User getUserById(int id) {
        User foundUser = null;
        for (User user : users) {
            if (user.getId() == id) {
                foundUser = user;
                break;
            }
        }
        return foundUser;
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
    
    private Statement executeStatement(Connection connection, String sqlStatement) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(sqlStatement);
        return statement;
    }
    
    private ResultSet executeSelectStatement(Connection connection, String sqlStatement) throws SQLException {
        Statement statement = executeStatement(connection, sqlStatement);
        return statement.getResultSet();
    }
    
    private int executeUpdateStatement(Connection connection, String sqlStatement) throws SQLException {
        Statement statement = executeStatement(connection, sqlStatement);
        return statement.getUpdateCount();
    }
    
    @Override
    public boolean login(String username, String password) {
        boolean successful = false;
        try (Connection connection = getConnection()) {
            ResultSet resultSet = executeSelectStatement(connection,
                    "SELECT * FROM user WHERE username = '" + username + "'");
            successful = resultSet.next() && password.equals(resultSet.getString("password"));
            if (successful) {
                refreshData(true);
                activeUser = getUserById(resultSet.getInt("userId"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return successful;
    }
    
    @Override
    public ObservableList<Appointment> getAppointments() {
        refreshData(false);
        return appointments;
    }
    
    @Override
    public ObservableList<Customer> getCustomers() {
        refreshData(false);
        return customers;
    }
    
    @Override
    public ObservableList<User> getUsers() {
        refreshData(false);
        return users;
    }
    
    @Override
    public User getActiveUser() {
        return activeUser;
    }
    
    @Override
    public String getActiveUsername() {
        return activeUser.getName();
    }
    
    @Override
    public void save(Customer customer) {
        if (customer.getCustomerId() == null) {
            insert(customer);
            refreshData(true);
        } else {
            update(customer);
            refreshData(false);
        }
    }
    
    private void insert(Customer customer) {
        try (Connection connection = getConnection()) {
            int addressId = lookupOrInsertAddressId(connection, customer);
            executeUpdateStatement(connection,
                    "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, " +
                            "lastUpdate, lastUpdateBy)\n" +
                            "VALUES (" +
                            "'" + customer.getName() + "', " +
                            addressId + ", " +
                            1 + ", " +
                            createRecordValues() + ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void update(Customer customer) {
        try (Connection connection = getConnection()) {
            int addressId = lookupOrInsertAddressId(connection, customer);
            executeUpdateStatement(connection,
                    "UPDATE customer\n" +
                            "SET customerName = '" + customer.getName() + "',\n" +
                            "addressId = " + addressId + ",\n" +
                            "lastUpdate = '" + timeConverter.nowInUTC() + "',\n" +
                            "lastUpdateBy = '" + activeUser.getName() + "'\n" +
                            "WHERE customerId = " + customer.getCustomerId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private int lookupOrInsertAddressId(Connection connection, Customer customer) throws SQLException {
        int countryId = lookupOrInsertCountryId(connection, customer.getCountry());
        int cityId = lookupOrInsertCityId(connection, customer.getCity(), countryId);
        Integer addressId = lookupAddressId(connection, customer, cityId);
        if (addressId == null) {
            insertAddress(connection, customer, cityId);
            addressId = lookupAddressId(connection, customer, cityId);
        }
        return addressId;
    }
    
    private Integer lookupAddressId(Connection connection, Customer customer, int cityId) throws SQLException {
        ResultSet resultSet = executeSelectStatement(connection,
                "SELECT * FROM address\n" +
                        "WHERE address = '" + customer.getAddress() + "'\n" +
                        "AND address2 = '" + customer.getAddress2() + "'\n" +
                        "AND cityId = " + cityId + "\n" +
                        "AND postalCode = '" + customer.getPostalCode() + "'\n" +
                        "AND phone = '" + customer.getPhone() + "'");
        Integer addressId = null;
        if (resultSet.next()) {
            addressId = resultSet.getInt("addressId");
        }
        return addressId;
    }
    
    private void insertAddress(Connection connection, Customer customer, int cityId) throws SQLException {
        executeUpdateStatement(connection,
                "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, " +
                        "createdBy, lastUpdate, lastUpdateBy)\n" +
                        "VALUES (" +
                        "'" + customer.getAddress() + "', " +
                        "'" + customer.getAddress2() + "', " +
                        cityId + ", " +
                        "'" + customer.getPostalCode() + "', " +
                        "'" + customer.getPhone() + "', " +
                        createRecordValues() + ")");
    }
    
    private int lookupOrInsertCityId(Connection connection, String city, int countryId) throws SQLException {
        Integer cityId = lookupCityId(connection, city, countryId);
        if (cityId == null) {
            insertCity(connection, city, countryId);
            cityId = lookupCityId(connection, city, countryId);
        }
        return cityId;
    }
    
    private Integer lookupCityId(Connection connection, String city, int countryId) throws SQLException {
        ResultSet resultSet = executeSelectStatement(connection,
                "SELECT *\n" +
                        "FROM city\n" +
                        "WHERE city = '" + city + "'\n" +
                        "AND countryId = " + countryId);
        Integer cityId = null;
        if (resultSet.next()) {
            cityId = resultSet.getInt("cityId");
        }
        return cityId;
    }
    
    private void insertCity(Connection connection, String city, int countryId) throws SQLException {
        executeUpdateStatement(connection,
                "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy)\n" +
                        "VALUES (" +
                        "'" + city + "', " +
                        countryId + ", " +
                        createRecordValues() + ")");
    }
    
    private int lookupOrInsertCountryId(Connection connection, String country) throws SQLException {
        Integer countryId = lookupCountryId(connection, country);
        if (countryId == null) {
            insertCountry(connection, country);
            countryId = lookupCountryId(connection, country);
        }
        return countryId;
    }
    
    private Integer lookupCountryId(Connection connection, String country) throws SQLException {
        ResultSet resultSet = executeSelectStatement(connection,
                "SELECT * FROM country WHERE country = '" + country + "'");
        Integer countryId = null;
        if (resultSet.next()) {
            countryId = resultSet.getInt("countryId");
        }
        return countryId;
    }
    
    private void insertCountry(Connection connection, String country) throws SQLException {
        executeUpdateStatement(connection,
                "INSERT INTO country (country, createDate, createdBy, lastUpdate, lastUpdateBy)\n" +
                        "VALUES (" +
                        "'" + country + "', " +
                        createRecordValues() + ")");
    }
    
    @Override
    public void delete(Customer customer) {
        try (Connection connection = getConnection()) {
            int deleted = executeUpdateStatement(connection,
                    "DELETE FROM customer WHERE customerId = " + customer.getCustomerId());
            if (deleted == 1) {
                customers.remove(customer);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        refreshData(false);
    }
    
    @Override
    public void save(Appointment appointment) {
        if (appointment.getId() == null) {
            insert(appointment);
            refreshData(true);
        } else {
            update(appointment);
            refreshData(false);
        }
    }
    
    private void insert(Appointment appointment) {
        try (Connection connection = getConnection()) {
            executeUpdateStatement(connection,
                    "INSERT INTO appointment (customerId, userId, title, description, location, contact, " +
                    "type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)\n" +
                    "VALUES (" +
                            appointment.getCustomer().getCustomerId() + ", " +
                            activeUser.getId() + ", " +
                            "'" + appointment.getTitle() + "', " +
                            "'" + appointment.getDescription() + "', " +
                            "'" + appointment.getLocation() + "', " +
                            "'" + appointment.getContact() + "', " +
                            "'" + appointment.getType() + "', " +
                            "'" + appointment.getUrl() + "', " +
                            "'" + timeConverter.localToUTC(appointment.getStart()) + "', " +
                            "'" + timeConverter.localToUTC(appointment.getEnd()) + "', " +
                            createRecordValues() + ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void update(Appointment appointment) {
        try (Connection connection = getConnection()) {
            executeUpdateStatement(connection,
                    "UPDATE appointment\n" +
                    "SET customerId = " + appointment.getCustomer().getCustomerId() + ",\n" +
                    "title = '" + appointment.getTitle() + "',\n" +
                    "description = '" + appointment.getDescription() + "',\n" +
                    "location = '" + appointment.getLocation() + "',\n" +
                    "contact = '" + appointment.getContact() + "',\n" +
                    "type = '" + appointment.getType() + "',\n" +
                    "url = '" + appointment.getUrl() + "',\n" +
                    "start = '" + timeConverter.localToUTC(appointment.getStart()) + "',\n" +
                    "end = '" + timeConverter.localToUTC(appointment.getEnd()) + "',\n" +
                    "lastUpdate = '" + timeConverter.nowInUTC() + "',\n" +
                    "lastUpdateBy = '" + activeUser.getName() + "'\n" +
                    "WHERE appointmentId = " + appointment.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void delete(Appointment appointment) {
        try (Connection connection = getConnection()) {
            int deleted = executeUpdateStatement(connection,
                    "DELETE FROM appointment WHERE appointmentId = " + appointment.getId());
            if (deleted == 1) {
                appointments.remove(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        refreshData(false);
    }
    
    private String createRecordValues() {
        String now = timeConverter.nowInUTC();
        String username = activeUser.getName();
        return "'" + now + "', " +
                "'" + username + "', " +
                "'" + now + "', " +
                "'" + username + "'";
    }
    
    @Override
    public ArrayList<ReportColumn> getAppointmentsByCustomer() {
        ReportColumn customerColumn = factory.newReportColumn(ColumnTitle.CUSTOMER);
        ReportColumn appointmentsColumn = factory.newReportColumn(ColumnTitle.APPOINTMENTS);
        try (Connection connection = getConnection()) {
            ResultSet resultSet = executeSelectStatement(connection,
                    "SELECT c.customerName customerName, COUNT(*) appointments\n" +
                            "FROM customer AS c\n" +
                            "INNER JOIN appointment AS a ON c.customerId = a.customerId\n" +
                            "GROUP BY customerName");
            while (resultSet.next()) {
                customerColumn.addResult(resultSet.getString("customerName"));
                appointmentsColumn.addResult("" + resultSet.getInt("appointments"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        ArrayList<ReportColumn> columns = new ArrayList<>();
        columns.add(customerColumn);
        columns.add(appointmentsColumn);
        return columns;
    }
    
    @Override
    public ArrayList<ReportColumn> getAppointmentTypesByMonth() {
        ReportColumn monthColumn = factory.newReportColumn(ColumnTitle.MONTH);
        ReportColumn typesColumn = factory.newReportColumn(ColumnTitle.TYPES);
        try (Connection connection = getConnection()) {
            ResultSet resultSet = executeSelectStatement(connection,
                    "SELECT MONTH(start) startMonth, COUNT(DISTINCT type) types\n" +
                            "FROM appointment\n" +
                            "GROUP BY startMonth");
            while (resultSet.next()) {
                int monthNumber = resultSet.getInt("startMonth");
                String monthName = Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.getDefault());
                monthColumn.addResult(monthName);
                typesColumn.addResult("" + resultSet.getInt("types"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        ArrayList<ReportColumn> columns = new ArrayList<>();
        columns.add(monthColumn);
        columns.add(typesColumn);
        return columns;
    }
}
