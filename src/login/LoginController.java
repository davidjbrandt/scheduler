package login;

import events.Broadcaster;
import events.Subscriber;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import localization.Localizer;

public class LoginController implements Subscriber {
    
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    
    private final Broadcaster broadcaster;
    private final Localizer localizer;
    
    public LoginController(Broadcaster broadcaster, Localizer localizer) {
        this.broadcaster = broadcaster;
        this.localizer = localizer;
    }
    
    @FXML
    private void initialize() {
        localize();
        setKeyboardShortcuts();
    }
    
    private void localize() {
        localizer.translate(usernameLabel);
        localizer.translate(passwordLabel);
        localizer.translate(loginButton);
    }
    
    private void setKeyboardShortcuts() {
        EventHandler<KeyEvent> keyHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        };
        usernameField.setOnKeyPressed(keyHandler);
        passwordField.setOnKeyPressed(keyHandler);
        loginButton.setOnKeyPressed(keyHandler);
    }
    
    @FXML
    private void loginClick() {
        broadcaster.loginClicked(usernameField.getText(), passwordField.getText());
        usernameField.requestFocus();
    }
    
    @Override
    public void logout() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
