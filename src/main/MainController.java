package main;

import database.Database;
import events.Broadcaster;
import events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import localization.Bundle;
import localization.Localizer;

public class MainController implements Subscriber {
    
    private final Bundle BUNDLE = Bundle.MAIN;
    
    @FXML private Button backButton;
    @FXML private Button logoutButton;
    @FXML private Button exitButton;
    
    @FXML private Label welcomeLabel;
    @FXML private Label usernameLabel;
    
    @FXML private Pane contentPane;
    
    private final ContentManager contentManager;
    private final Broadcaster broadcaster;
    private final Localizer localizer;
    
    public MainController(ContentManager contentManager, Broadcaster broadcaster, Localizer localizer) {
        this.contentManager = contentManager;
        this.broadcaster = broadcaster;
        this.localizer = localizer;
    }
    
    @FXML
    private void initialize() {
        localize();
        reset();
    }
    
    private void localize() {
        localizer.translate(backButton);
        localizer.translate(logoutButton);
        localizer.translate(welcomeLabel);
        localizer.translate(exitButton);
    }
    
    private void reset() {
        setContent(contentManager.getMenu());
        backButton.setVisible(false);
    }
    
    @Override
    public void loginSuccessful(Database database) {
        usernameLabel.setText(database.getActiveUsername());
    }
    
    @FXML
    private void backClick() {
        reset();
        broadcaster.backClicked();
    }
    
    @FXML
    private void logoutClick() {
        broadcaster.logout();
        reset();
    }
    
    @FXML
    private void exitClick() {
        broadcaster.exitClicked();
    }
    
    private void setContent(Node content) {
        contentPane.getChildren().setAll(content);
        backButton.setVisible(true);
    }
    
    @Override
    public void calendarClicked() {
        setContent(contentManager.getCalendar());
    }
    
    @Override
    public void customersClicked() {
        setContent(contentManager.getCustomers());
    }
    
    @Override
    public void reportsClicked() {
        setContent(contentManager.getReports());
    }
}
