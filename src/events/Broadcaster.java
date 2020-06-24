package events;

import calendar.Appointment;
import customers.Customer;
import database.Database;

import java.time.LocalTime;
import java.util.ArrayList;

public class Broadcaster {
    
    ArrayList<Subscriber> subscribers = new ArrayList<>();
    
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
    
    public void loginClicked(String username, String password) {
        for (Subscriber subscriber : subscribers) {
            subscriber.loginClicked(username, password);
        }
    }
    
    public void loginSuccessful(Database database) {
        for (Subscriber subscriber : subscribers) {
            subscriber.loginSuccessful(database);
        }
    }
    
    public void loginFailed() {
        for (Subscriber subscriber : subscribers) {
            subscriber.loginFailed();
        }
    }
    
    public void logout() {
        for (Subscriber subscriber : subscribers) {
            subscriber.logout();
        }
    }
    
    public void backClicked() {
        for (Subscriber subscriber : subscribers) {
            subscriber.backClicked();
        }
    }
    
    public void exitClicked() {
        for (Subscriber subscriber : subscribers) {
            subscriber.exitClicked();
        }
    }
    
    public void calendarClicked() {
        for (Subscriber subscriber : subscribers) {
            subscriber.calendarClicked();
        }
    }
    
    public void customersClicked() {
        for (Subscriber subscriber : subscribers) {
            subscriber.customersClicked();
        }
    }
    
    public void reportsClicked() {
        for (Subscriber subscriber : subscribers) {
            subscriber.reportsClicked();
        }
    }

    public void saveClicked(Customer customer) {
        for (Subscriber subscriber : subscribers) {
            subscriber.saveClicked(customer);
        }
    }

    public void deleteClicked(Customer customer) {
        for (Subscriber subscriber : subscribers) {
            subscriber.deleteClicked(customer);
        }
    }

    public void deleteConfirmed(Customer customer) {
        for (Subscriber subscriber : subscribers) {
            subscriber.deleteConfirmed(customer);
        }
    }

    public void saveClicked(Appointment appointment) {
        for (Subscriber subscriber : subscribers) {
            subscriber.saveClicked(appointment);
        }
    }

    public void deleteClicked(Appointment appointment) {
        for (Subscriber subscriber : subscribers) {
            subscriber.deleteClicked(appointment);
        }
    }

    public void deleteConfirmed(Appointment appointment) {
        for (Subscriber subscriber : subscribers) {
            subscriber.deleteConfirmed(appointment);
        }
    }

    public void missingRequiredField(String field) {
        for (Subscriber subscriber : subscribers) {
            subscriber.missingRequiredField(field);
        }
    }
    
    public void appointmentOutsideBusinessHours(LocalTime start, LocalTime end) {
        for (Subscriber subscriber : subscribers) {
            subscriber.appointmentOutsideBusinessHours(start, end);
        }
    }
    
    public void appointmentTimeNotAvailable() {
        for (Subscriber subscriber : subscribers) {
            subscriber.appointmentTimeNotAvailable();
        }
    }
}
