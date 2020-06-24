package database;

import calendar.Appointment;
import calendar.AppointmentsFactory;
import customers.Customer;
import customers.CustomerFactory;
import reports.ColumnTitle;
import reports.ReportColumn;
import reports.ReportFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseFactory {
    
    private final AppointmentsFactory appointmentsFactory;
    private final CustomerFactory customerFactory;
    private final ReportFactory reportFactory;
    
    public DatabaseFactory(AppointmentsFactory appointmentsFactory, CustomerFactory customerFactory,
            ReportFactory reportFactory) {
        this.appointmentsFactory = appointmentsFactory;
        this.customerFactory = customerFactory;
        this.reportFactory = reportFactory;
    }
    
    public Appointment existingAppointment(ResultSet resultSet, Customer customer, User user) throws SQLException {
        return appointmentsFactory.existingAppointment(resultSet, customer, user);
    }
    
    public Customer existingCustomer(ResultSet resultSet) throws SQLException {
        return customerFactory.existingCustomer(resultSet);
    }
    
    public User existingUser(ResultSet resultSet) throws SQLException {
        return new User(resultSet.getInt("userId"), resultSet.getString("userName"));
    }
    
    public ReportColumn newReportColumn(ColumnTitle title) {
        return reportFactory.newReportColumn(title);
    }
}
