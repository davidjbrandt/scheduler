package alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import localization.Localizer;

public class AlertFactory {
    
    private final Localizer localizer;
    
    public AlertFactory(Localizer localizer) {
        this.localizer = localizer;
    }
    
    private String localize(Message message) {
        return localizer.translate(message.getName());
    }
    
    private ButtonType display(Alert.AlertType alertType, String prompt) {
        Alert alert = new Alert(alertType, prompt);
        alert.showAndWait();
        return alert.getResult();
    }
    
    private boolean confirm(String prompt) {
        return (ButtonType.OK == display(Alert.AlertType.CONFIRMATION, prompt));
    }
    
    private void inform(String prompt) {
        display(Alert.AlertType.INFORMATION, prompt);
    }
    
    private void warn(String prompt) {
        display(Alert.AlertType.WARNING, prompt);
    }
    
    /*package-private*/ void informLoginFailed() {
        inform(localize(Message.LOGIN));
    }
    
    /*package-private*/ void informMeetingSoon(String customer, String time) {
        String prompt = localize(Message.MEETING_SOON);
        prompt = prompt.replaceAll("CUSTOMER", customer);
        prompt = prompt.replaceAll("TIME", time);
        inform(prompt);
    }

    /*package-private*/ boolean confirmDeleteCustomer(String customer) {
        String prompt = localize(Message.DELETE_CUSTOMER);
        prompt = prompt.replaceAll("CUSTOMER", customer);
        return confirm(prompt);
    }

    /*package-private*/ boolean confirmDeleteAppointment(String customer, String date, String time) {
        String prompt = localize(Message.DELETE_APPOINTMENT);
        prompt = prompt.replaceAll("CUSTOMER", customer);
        prompt = prompt.replaceAll("DATE", date);
        prompt = prompt.replaceAll("TIME", time);
        return confirm(prompt);
    }

    /*package-private*/ void informRequiredField(String field) {
        String prompt = localize(Message.REQUIRED_FIELD);
        prompt = prompt.replaceAll("FIELD", field);
        inform(prompt);
    }
    
    /*package-private*/ void informAppointmentOutsideBusinessHours(String start, String end) {
        String prompt = localize(Message.APPOINTMENT_OUTSIDE_BUSINESS_HOURS);
        prompt = prompt.replaceAll("START", start);
        prompt = prompt.replaceAll("END", end);
        inform(prompt);
    }
    
    /*package-private*/ void informAppointmentTimeNotAvailable() {
        inform(localize(Message.APPOINTMENT_TIME_NOT_AVAILABLE));
    }
}
