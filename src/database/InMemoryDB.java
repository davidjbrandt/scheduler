package database;

import calendar.Appointment;
import customers.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import reports.ColumnTitle;
import reports.ReportColumn;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class InMemoryDB implements Database {
    
    private static int nextUserID = 1;
    
    private User activeUser;

    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    
    private final DatabaseFactory factory;
    
    public InMemoryDB(DatabaseFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public boolean login(String username, String password) {
        activeUser = getUserByName(username);
        return username.equals(password);
    }
    
    private User getUserByName(String name) {
        User foundUser = null;
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                foundUser = user;
                break;
            }
        }
        if (foundUser == null) {
            foundUser = new User(nextUserID++, name);
            users.add(foundUser);
        }
        return foundUser;
    }
    
    @Override
    public ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public ObservableList<Customer> getCustomers() {
        return customers;
    }
    
    @Override
    public ObservableList<User> getUsers() {
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
        if (!customers.contains(customer)) {
            customers.add(customer);
        }
    }

    @Override
    public void delete(Customer customer) {
        customers.remove(customer);
    }

    @Override
    public void save(Appointment appointment) {
        if (!appointments.contains(appointment)) {
            appointments.add(appointment);
        }
    }

    @Override
    public void delete(Appointment appointment) {
        appointments.remove(appointment);
    }
    
    @Override
    public ArrayList<ReportColumn> getAppointmentsByCustomer() {
        ReportColumn customerColumn = factory.newReportColumn(ColumnTitle.CUSTOMER);
        ReportColumn appointmentsColumn = factory.newReportColumn(ColumnTitle.APPOINTMENTS);
        for (Customer customer : customers) {
            customerColumn.addResult(customer.getName());
            appointmentsColumn.addResult(Long.toString(appointments.stream()
                    .filter(appointment -> appointment.getCustomer().equals(customer))
                    .count()));
        }
        ArrayList<ReportColumn> reportColumns = new ArrayList<>();
        reportColumns.add(customerColumn);
        reportColumns.add(appointmentsColumn);
        return reportColumns;
    }
    
    @Override
    public ArrayList<ReportColumn> getAppointmentTypesByMonth() {
        ReportColumn monthColumn = factory.newReportColumn(ColumnTitle.MONTH);
        ReportColumn typesColumn = factory.newReportColumn(ColumnTitle.TYPES);
        appointments.stream()
                .map(appointment -> appointment.getStart().getMonth())
                .distinct()
                .forEach(month -> {
                    monthColumn.addResult(month.getDisplayName(TextStyle.FULL,Locale.getDefault()));
                    typesColumn.addResult(Long.toString(appointments.stream()
                            .filter(appointment -> appointment.getStart().getMonth().equals(month))
                            .map(Appointment::getType)
                            .distinct()
                            .count()));
        });
        ArrayList<ReportColumn> reportColumns = new ArrayList<>();
        reportColumns.add(monthColumn);
        reportColumns.add(typesColumn);
        return reportColumns;
    }
}
