package database;

import calendar.Appointment;
import customers.Customer;
import javafx.collections.ObservableList;
import reports.ReportColumn;

import java.util.ArrayList;

public interface Database {
    boolean login(String username, String password);
    ObservableList<Appointment> getAppointments();
    ObservableList<Customer> getCustomers();
    ObservableList<User> getUsers();
    User getActiveUser();
    String getActiveUsername();
    void save(Customer customer);
    void delete(Customer customer);
    void save(Appointment appointment);
    void delete(Appointment appointment);
    ArrayList<ReportColumn> getAppointmentsByCustomer();
    ArrayList<ReportColumn> getAppointmentTypesByMonth();
}
