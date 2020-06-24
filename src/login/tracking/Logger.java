package login.tracking;

import calendar.TimeConverter;
import database.Database;
import events.Subscriber;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger implements Subscriber {
    
    private final TimeConverter timeConverter;
    
    public Logger(TimeConverter timeConverter) {
        this.timeConverter = timeConverter;
    }
    
    @Override
    public void loginSuccessful(Database database) {
        String logText = database.getActiveUsername() + " logged in at " + timeConverter.nowInUTC() + "(UTC)";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Login Log.txt", true));
            writer.write(logText);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
