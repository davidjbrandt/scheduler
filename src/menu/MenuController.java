package menu;

import events.Broadcaster;
import events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import localization.Localizer;

public class MenuController implements Subscriber {
    
    @FXML private Button calendarButton;
    @FXML private Button customersButton;
    @FXML private Button reportsButton;
    
    private final Broadcaster broadcaster;
    private final Localizer localizer;
    
    public MenuController(Broadcaster broadcaster, Localizer localizer) {
        this.broadcaster = broadcaster;
        this.localizer = localizer;
    }
    
    @FXML
    private void initialize() {
        localize();
    }
    
    private void localize() {
        localizer.translate(calendarButton);
        localizer.translate(customersButton);
        localizer.translate(reportsButton);
    }
    
    @FXML
    private void calendarClick() {
        broadcaster.calendarClicked();
    }
    
    @FXML
    private void customersClick() {
        broadcaster.customersClicked();
    }
    
    @FXML
    private void reportsClick() {
        broadcaster.reportsClicked();
    }
}
