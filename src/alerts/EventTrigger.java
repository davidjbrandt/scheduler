package alerts;

import calendar.Appointment;
import customers.Customer;
import database.Database;
import events.Broadcaster;
import events.Subscriber;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventTrigger implements Subscriber {
    
    private final AlertFactory factory;
    private final Broadcaster broadcaster;
    
    public EventTrigger(AlertFactory factory, Broadcaster broadcaster) {
        this.factory = factory;
        this.broadcaster = broadcaster;
    }
    
    @Override
    public void loginSuccessful(Database database) {
        database.getAppointments()
                .stream()
                .filter(appointment -> appointment.getStart().isBefore(LocalDateTime.now().plusMinutes(15)))
                .filter(appointment -> appointment.getStart().isAfter(LocalDateTime.now()))
                .filter(appointment -> appointment.getUser().equals(database.getActiveUser()))
                .forEach(appointment ->
                        factory.informMeetingSoon(appointment.getCustomer().getName(),
                                appointment.getStart().toLocalTime().toString()));
    }
    
    @Override
    public void loginFailed() {
        factory.informLoginFailed();
    }

    @Override
    public void deleteClicked(Customer customer) {
        if (factory.confirmDeleteCustomer(customer.getName())) {
            broadcaster.deleteConfirmed(customer);
        }
    }

    @Override
    public void deleteClicked(Appointment appointment) {
        String customerName = appointment.getCustomer().getName();
        String appointmentDate = appointment.getStart().toLocalDate().toString();
        String appointmentTime = appointment.getStart().toLocalTime().toString();
        if (factory.confirmDeleteAppointment(customerName, appointmentDate, appointmentTime)) {
            broadcaster.deleteConfirmed(appointment);
        }
    }

    @Override
    public void missingRequiredField(String field) {
        factory.informRequiredField(field);
    }
    
    @Override
    public void appointmentOutsideBusinessHours(LocalTime start, LocalTime end) {
        factory.informAppointmentOutsideBusinessHours(start.toString(), end.toString());
    }
    
    @Override
    public void appointmentTimeNotAvailable() {
        factory.informAppointmentTimeNotAvailable();
    }
}
