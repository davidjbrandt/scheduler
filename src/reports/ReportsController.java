package reports;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import localization.Localizer;

import java.util.Collection;

public class ReportsController {
    
    @FXML private Label reportsLabel;
    @FXML private ChoiceBox<Report> reportChoiceBox;
    @FXML private Button refreshButton;
    @FXML private TextArea reportArea;
    
    private final ObservableList<Report> reports;
    private final Localizer localizer;
    
    public ReportsController(Collection<Report> reports, Localizer localizer) {
        this.reports = FXCollections.observableArrayList(reports);
        this.localizer = localizer;
    }
    
    @FXML
    private void initialize() {
        localize();
        setup();
    }
    
    private void localize() {
        localizer.translate(reportsLabel);
        localizer.translate(refreshButton);
    }
    
    private void setup() {
        reportChoiceBox.setItems(FXCollections.observableArrayList(reports));
        //Using a lambda expression here saves an import statement
        reportChoiceBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> reportArea.setText(newValue.getResults()));
    }
    
    @FXML
    private void refreshClick() {
        Report report = reportChoiceBox.getValue();
        if (report != null) {
            reportArea.setText(report.getResults());
        }
    }
}
