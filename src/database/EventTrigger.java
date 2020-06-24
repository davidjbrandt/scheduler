package database;

import calendar.Appointment;
import customers.Customer;
import events.Broadcaster;
import events.Subscriber;

public class EventTrigger implements Subscriber {
    
    private final Database database;
    private final Broadcaster broadcaster;
    
    public EventTrigger(Database database, Broadcaster broadcaster) {
        this.database = database;
        this.broadcaster = broadcaster;
    }
    
    @Override
    public void loginClicked(String username, String password) {
        if (database.login(username, password)) {
            broadcaster.loginSuccessful(database);
        } else {
            broadcaster.loginFailed();
        }
    }

    @Override
    public void saveClicked(Customer customer) {
        database.save(customer);
    }

    @Override
    public void deleteConfirmed(Customer customer) {
        database.delete(customer);
    }

    @Override
    public void saveClicked(Appointment appointment) {
        database.save(appointment);
    }

    @Override
    public void deleteConfirmed(Appointment appointment) {
        database.delete(appointment);
    }
}
