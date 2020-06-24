package calendar;

import customers.Customer;
import database.Database;
import database.User;
import events.Subscriber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AppointmentsFactory implements Subscriber {
    
    private User activeUser;
    
    private final TimeConverter timeConverter;

    public AppointmentsFactory(TimeConverter timeConverter) {
        this.timeConverter = timeConverter;
    }

    @Override
    public void loginSuccessful(Database database) {
        activeUser = database.getActiveUser();
    }

    public Appointment newAppointment() {
        return new Appointment(null, null, activeUser, "", "", "", "",
                "", "", defaultStartTime(), defaultEndTime());
    }
    
    public Appointment existingAppointment(ResultSet resultSet, Customer customer, User user) throws SQLException {
        Appointment appointment = newAppointment();
        appointment.setId(resultSet.getInt("appointmentId"));
        appointment.setCustomer(customer);
        appointment.setUser(user);
        appointment.setTitle(resultSet.getString("title"));
        appointment.setDescription(resultSet.getString("description"));
        appointment.setLocation(resultSet.getString("location"));
        appointment.setContact(resultSet.getString("contact"));
        appointment.setType(resultSet.getString("type"));
        appointment.setUrl(resultSet.getString("url"));
        appointment.setStart(timeConverter.utcToLocal(resultSet.getTimestamp("start")));
        appointment.setEnd(timeConverter.utcToLocal(resultSet.getTimestamp("end")));
        return appointment;
    }

    public LocalDateTime defaultStartTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(1).minusMinutes(now.getMinute());
    }

    public LocalDateTime defaultEndTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(2).minusMinutes(now.getMinute());
    }
}
