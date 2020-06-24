package events;

import calendar.Appointment;
import customers.Customer;
import database.Database;

import java.time.LocalTime;

public interface Subscriber {
    default void loginClicked(String username, String password) {}
    default void loginSuccessful(Database database) {}
    default void loginFailed() {}
    default void logout() {}
    default void backClicked() {}
    default void exitClicked() {}
    default void calendarClicked() {}
    default void customersClicked() {}
    default void reportsClicked() {}
    default void saveClicked(Customer customer) {}
    default void deleteClicked(Customer customer) {}
    default void deleteConfirmed(Customer customer) {}
    default void saveClicked(Appointment appointment) {}
    default void deleteClicked(Appointment appointment) {}
    default void deleteConfirmed(Appointment appointment) {}
    default void missingRequiredField(String field) {}
    default void appointmentOutsideBusinessHours(LocalTime start, LocalTime end) {}
    default void appointmentTimeNotAvailable() {}
}
