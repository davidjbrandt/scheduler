package customers;

import database.Database;
import events.Broadcaster;
import events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import localization.Localizer;

public class CustomersController implements Subscriber {
    
    @FXML private Pane viewPane;
    @FXML private Label customersLabel;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> address1Column;
    @FXML private TableColumn<Customer, String> address2Column;
    @FXML private TableColumn<Customer, String> cityColumn;
    @FXML private TableColumn<Customer, String> zipColumn;
    @FXML private TableColumn<Customer, String> countryColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    
    @FXML private Pane maintenancePane;
    @FXML private Label customerLabel;
    @FXML private Label nameLabel;
    @FXML private TextField nameField;
    @FXML private Label address1Label;
    @FXML private TextField address1Field;
    @FXML private Label address2Label;
    @FXML private TextField address2Field;
    @FXML private Label cityLabel;
    @FXML private TextField cityField;
    @FXML private Label zipLabel;
    @FXML private TextField zipField;
    @FXML private Label countryLabel;
    @FXML private TextField countryField;
    @FXML private Label phoneLabel;
    @FXML private TextField phoneField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Customer activeCustomer;

    private final CustomerFactory factory;
    private final Broadcaster broadcaster;
    private final Localizer localizer;
    
    public CustomersController(CustomerFactory factory, Broadcaster broadcaster, Localizer localizer) {
        this.factory = factory;
        this.broadcaster = broadcaster;
        this.localizer = localizer;
    }
    
    @FXML
    private void initialize() {
        localize();
        setupTable();
        broadcaster.subscribe(this);
    }
    
    private void localize() {
        localizer.translate(customersLabel);
        localizer.translate(addButton);
        localizer.translate(editButton);
        localizer.translate(deleteButton);
        localizer.translate(customerTable);
        localizer.translate(customerLabel);
        localizer.translate(nameLabel);
        localizer.translate(address1Label);
        localizer.translate(address2Label);
        localizer.translate(cityLabel);
        localizer.translate(zipLabel);
        localizer.translate(countryLabel);
        localizer.translate(phoneLabel);
        localizer.translate(saveButton);
        localizer.translate(cancelButton);
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        address1Column.setCellValueFactory(new PropertyValueFactory<>("address"));
        address2Column.setCellValueFactory(new PropertyValueFactory<>("address2"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        zipColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    @FXML
    private void addClick() {
        showMaintenance(factory.newCustomer());
    }

    @FXML
    private void editClick() {
        Customer selectedItem = getSelectedItem();
        if (selectedItem != null) {
            showMaintenance(selectedItem);
        }
    }

    @FXML
    private void deleteClick() {
        Customer selectedItem = getSelectedItem();
        if (selectedItem != null) {
            broadcaster.deleteClicked(selectedItem);
        }
    }

    private Customer getSelectedItem() {
        return customerTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void saveClick() {
        if (formIsValid()) {
            hideMaintenance();
            saveActiveCustomer();
            broadcaster.saveClicked(activeCustomer);
        }
    }

    private boolean formIsValid() {
        boolean isValid = hasData(nameField, nameLabel);
        isValid = isValid && hasData(address1Field, address1Label);
        isValid = isValid && hasData(cityField, cityLabel);
        isValid = isValid && hasData(zipField, zipLabel);
        isValid = isValid && hasData(countryField, countryLabel);
        isValid = isValid && hasData(phoneField, phoneLabel);
        return isValid;
    }
    
    private boolean hasData(TextField field, Label label) {
        boolean isEmpty = field.getText().isEmpty();
        if (isEmpty) {
            field.requestFocus();
            broadcaster.missingRequiredField(label.getText());
        }
        return !isEmpty;
    }

    @FXML
    private void cancelClick() {
        hideMaintenance();
    }

    @Override
    public void loginSuccessful(Database database) {
        customerTable.setItems(database.getCustomers());
    }

    @Override
    public void backClicked() {
        hideMaintenance();
    }
    
    @Override
    public void logout() {
        hideMaintenance();
    }
    
    private void loadActiveCustomer() {
        nameField.setText(activeCustomer.getName());
        address1Field.setText(activeCustomer.getAddress());
        address2Field.setText(activeCustomer.getAddress2());
        cityField.setText(activeCustomer.getCity());
        zipField.setText(activeCustomer.getPostalCode());
        countryField.setText(activeCustomer.getCountry());
        phoneField.setText(activeCustomer.getPhone());
    }

    private void saveActiveCustomer() {
        activeCustomer.setName(nameField.getText());
        activeCustomer.setAddress(address1Field.getText());
        activeCustomer.setAddress2(address2Field.getText());
        activeCustomer.setCity(cityField.getText());
        activeCustomer.setPostalCode(zipField.getText());
        activeCustomer.setCountry(countryField.getText());
        activeCustomer.setPhone(phoneField.getText());
    }
    
    private void showMaintenance(Customer activeCustomer) {
        this.activeCustomer = activeCustomer;
        loadActiveCustomer();
        maintenancePane.setVisible(true);
        viewPane.setVisible(false);
    }
    
    private void hideMaintenance() {
        customerTable.refresh();
        viewPane.setVisible(true);
        maintenancePane.setVisible(false);
    }
}
