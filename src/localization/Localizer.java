package localization;

import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ResourceBundle;

public class Localizer {
    
    private final ResourceBundle bundle;
    
    public Localizer(ResourceBundle bundle) {
        this.bundle = bundle;
    }
    
    public String translate(String key) {
        return bundle.getString(key);
    }
    
    public void translate(Labeled labeled) {
        labeled.setText(translate(labeled.getText()));
    }
    
    public <S> void translate(TableView<S> tableView) {
        for (TableColumn<S, ?> column : tableView.getColumns()) {
            column.setText(translate(column.getText()));
        }
    }
}
