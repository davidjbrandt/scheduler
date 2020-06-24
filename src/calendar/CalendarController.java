package calendar;

import customers.Customer;
import database.Database;
import database.User;
import events.Broadcaster;
import events.Subscriber;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import localization.Localizer;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Predicate;

public class CalendarController implements Subscriber {

    private final String AM = "AM";
    private final String PM = "PM";
    
    private final LocalTime BUSINESS_HOURS_START = LocalTime.of(8, 0);
    private final LocalTime BUSINESS_HOURS_END = LocalTime.of(17, 0);
    private final Duration BUSINESS_HOURS_DURATION = Duration.between(BUSINESS_HOURS_START, BUSINESS_HOURS_END);

    @FXML private Pane viewPane;
    @FXML private Label appointmentsLabel;
    @FXML private RadioButton allDatesRadio;
    @FXML private RadioButton monthRadio;
    @FXML private RadioButton weekRadio;
    @FXML private RadioButton allUsersRadio;
    @FXML private RadioButton activeUserRadio;
    @FXML private RadioButton otherUserRadio;
    @FXML private ChoiceBox<User> userChoiceBox;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Customer> customerColumn;
    @FXML private TableColumn<Appointment, String> userColumn;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> locationColumn;
    @FXML private TableColumn<Appointment, String> contactColumn;
    @FXML private TableColumn<Appointment, String> typeColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML private Pane maintenancePane;
    @FXML private Label appointmentLabel;
    @FXML private Label customerLabel;
    @FXML private ChoiceBox<Customer> customerPicker;
    @FXML private Label titleLabel;
    @FXML private TextField titleField;
    @FXML private Label descriptionLabel;
    @FXML private TextField descriptionField;
    @FXML private Label locationLabel;
    @FXML private TextField locationField;
    @FXML private Label contactLabel;
    @FXML private TextField contactField;
    @FXML private Label typeLabel;
    @FXML private TextField typeField;
    @FXML private Label urlLabel;
    @FXML private TextField urlField;
    @FXML private Label startLabel;
    @FXML private ChoiceBox<Integer> startHour;
    @FXML private ChoiceBox<Integer> startMinute;
    @FXML private ChoiceBox<String> startAMPM;
    @FXML private DatePicker startDatePicker;
    @FXML private Label endLabel;
    @FXML private ChoiceBox<Integer> endHour;
    @FXML private ChoiceBox<Integer> endMinute;
    @FXML private ChoiceBox<String> endAMPM;
    @FXML private DatePicker endDatePicker;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private User activeUser;
    private Appointment activeAppointment;
    private ObservableList<Appointment> allAppointments;
    private Duration activeDuration = Duration.ofHours(1);
    
    private Predicate<Appointment> dateFilter = appointment -> true;
    private Predicate<Appointment> userFilter = appointment -> true;

    private final AppointmentsFactory factory;
    private final Broadcaster broadcaster;
    private final Localizer localizer;

    public CalendarController(AppointmentsFactory factory, Broadcaster broadcaster, Localizer localizer) {
        this.factory = factory;
        this.broadcaster = broadcaster;
        this.localizer = localizer;
    }

    @FXML
    private void initialize() {
        localize();
        setupTable();
        setupForm();
    }

    private void localize() {
        localizer.translate(appointmentsLabel);
        localizer.translate(allDatesRadio);
        localizer.translate(monthRadio);
        localizer.translate(weekRadio);
        localizer.translate(allUsersRadio);
        localizer.translate(activeUserRadio);
        localizer.translate(addButton);
        localizer.translate(editButton);
        localizer.translate(deleteButton);
        localizer.translate(appointmentTable);
        localizer.translate(appointmentLabel);
        localizer.translate(customerLabel);
        localizer.translate(titleLabel);
        localizer.translate(descriptionLabel);
        localizer.translate(locationLabel);
        localizer.translate(contactLabel);
        localizer.translate(typeLabel);
        localizer.translate(urlLabel);
        localizer.translate(startLabel);
        localizer.translate(endLabel);
        localizer.translate(saveButton);
        localizer.translate(cancelButton);
    }

    private void setupTable() {
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        userChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> otherUserChanged());
    }

    private void setupForm() {
        setupTimePicker(startHour, startMinute, startAMPM);
        setupTimePicker(endHour, endMinute, endAMPM);
        selectStart(factory.defaultStartTime());
        selectEnd(factory.defaultEndTime());
        //Passing lambda expressions to this dedicated setup function reduces code duplication
        setupDateTimeChangeListener(startHour, startMinute, startAMPM, startDatePicker, this::startChanged);
        setupDateTimeChangeListener(endHour, endMinute, endAMPM, endDatePicker, this::endChanged);
    }
    
    private void setupTimePicker(ChoiceBox<Integer> hourPicker, ChoiceBox<Integer> minutePicker,
            ChoiceBox<String> ampmPicker) {

        for (int i = 1; i <= 12; i++) {
            hourPicker.getItems().add(i);
        }
        for (int i = 0; i <= 45; i += 15) {
            minutePicker.getItems().add(i);
        }
        ampmPicker.getItems().add(AM);
        ampmPicker.getItems().add(PM);
    }
    
    private void setupDateTimeChangeListener(ChoiceBox<Integer> hourPicker, ChoiceBox<Integer> minutePicker,
            ChoiceBox<String> ampmPicker, DatePicker datePicker, Runnable onChange) {
        
        hourPicker.valueProperty().addListener(simpleChangeListener(onChange));
        minutePicker.valueProperty().addListener(simpleChangeListener(onChange));
        ampmPicker.valueProperty().addListener(simpleChangeListener(onChange));
        datePicker.valueProperty().addListener(simpleChangeListener(onChange));
    }
    
    private <T> ChangeListener<T> simpleChangeListener(Runnable onChange) {
        // I decided this was easier to read than an anonymous functional Object creation
        return (observable, oldValue, newValue) -> onChange.run();
    }
    
    private void startChanged() {
        selectEnd(getSelectedStart().plus(activeDuration));
    }
    
    private void endChanged() {
        if (getSelectedEnd().isBefore(getSelectedStart().plusMinutes(15))) {
            selectEnd(getSelectedStart().plus(activeDuration));
        } else {
            activeDuration = Duration.between(getSelectedStart(), getSelectedEnd());
        }
    }
    
    private void applyViewFilters() {
        //Using Predicate lambda expressions here allows the filtered list to stay updated with new entries
        appointmentTable.setItems(allAppointments.filtered(dateFilter).filtered(userFilter));
    }
    
    @FXML
    private void viewAllDates() {
        dateFilter = appointment -> true;
        applyViewFilters();
    }
    
    @FXML
    private void viewMonth() {
        dateFilter = this::isThisMonth;
        applyViewFilters();
    }
    
    private boolean isThisMonth(Appointment appointment) {
        LocalDateTime now = LocalDateTime.now();
        return appointment.getStart().getMonth()
                .equals(now.getMonth());
    }
    
    @FXML
    private void viewWeek() {
        dateFilter = this::isThisWeek;
        applyViewFilters();
    }
    
    private boolean isThisWeek(Appointment appointment) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = LocalDateTime.of(now.toLocalDate().minusDays(now.getDayOfWeek().getValue() % 7),
                LocalTime.of(0, 0));
        LocalDateTime weekEnd = weekStart.plusWeeks(1);
        LocalDateTime appointmentStart = appointment.getStart();
        return appointmentStart.isAfter(weekStart) && appointmentStart.isBefore(weekEnd);
    }
    
    @FXML
    private void viewAllUsers() {
        userFilter = appointment -> true;
        applyViewFilters();
    }
    
    @FXML
    private void viewActiveUser() {
        userFilter = appointment -> appointment.getUser().equals(activeUser);
        applyViewFilters();
    }
    
    @FXML
    private void viewOtherUser() {
        userFilter = appointment -> appointment.getUser().equals(userChoiceBox.getValue());
        applyViewFilters();
    }
    
    @FXML
    private void otherUserChanged() {
        applyViewFilters();
    }

    @FXML
    private void addClick() {
        showMaintenance(factory.newAppointment());
    }

    @FXML
    private void editClick() {
        Appointment selectedItem = getSelectedItem();
        if (selectedItem != null) {
            showMaintenance(selectedItem);
        }
    }

    @FXML
    private void deleteClick() {
        Appointment selectedItem = getSelectedItem();
        if (selectedItem != null) {
            broadcaster.deleteClicked(selectedItem);
        }
    }

    private Appointment getSelectedItem() {
        return appointmentTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void saveClick() {
        if (formIsValid()) {
            hideMaintenance();
            saveActiveAppointment();
            broadcaster.saveClicked(activeAppointment);
        }
    }

    private boolean formIsValid() {
        boolean isValid = customerIsSelected();
        isValid = isValid && isWithinBusinessHours();
        isValid = isValid && timeIsAvailable();
        return isValid;
    }
    
    private boolean customerIsSelected() {
        boolean isSelected = customerPicker.getValue() != null;
        if (!isSelected) {
            broadcaster.missingRequiredField(customerLabel.getText());
        }
        return isSelected;
    }
    
    private boolean isWithinBusinessHours() {
        boolean result = activeDuration.compareTo(BUSINESS_HOURS_DURATION) <= 0;
        result = result && getSelectedStart().toLocalTime().compareTo(BUSINESS_HOURS_START) >= 0;
        result = result && getSelectedEnd().toLocalTime().compareTo(BUSINESS_HOURS_END) <= 0;
        result = result && isBusinessDay(getSelectedStart().getDayOfWeek());
        if (!result) {
            broadcaster.appointmentOutsideBusinessHours(BUSINESS_HOURS_START, BUSINESS_HOURS_END);
        }
        return result;
    }
    
    private boolean isBusinessDay(DayOfWeek dayOfWeek) {
        return !dayOfWeek.equals(DayOfWeek.SUNDAY) && !dayOfWeek.equals(DayOfWeek.SATURDAY);
    }
    
    private boolean timeIsAvailable() {
        //Because anyMatch short-circuits, a parallel stream might improve performance if there are many appointments
        /* I also made an assumption here that users can schedule appointments overlapping other users' appointments
        *  but not their own, since the users can each meet with clients separately and independently. */
        boolean isNotAvailable = allAppointments.parallelStream()
                .filter(appointment -> appointment.getUser().equals(activeAppointment.getUser()))
                .filter(appointment -> !appointment.equals(activeAppointment))
                .anyMatch(this::isOverlapping);
        if (isNotAvailable) {
            broadcaster.appointmentTimeNotAvailable();
        }
        return !isNotAvailable;
    }
    
    private boolean isOverlapping(Appointment appointment) {
        LocalDateTime selectedStart = getSelectedStart();
        LocalDateTime selectedEnd = getSelectedEnd();
        boolean result = isBetween(selectedStart, appointment.getStart(), appointment.getEnd());
        result = result || isBetween(selectedEnd, appointment.getStart(), appointment.getEnd());
        result = result || isBetween(appointment.getStart(), selectedStart, selectedEnd);
        result = result || isBetween(appointment.getEnd(), selectedStart, selectedEnd);
        result = result || selectedStart.equals(appointment.getStart());
        return result;
    }
    
    private boolean isBetween(LocalDateTime dateTime, LocalDateTime betweenStart, LocalDateTime betweenEnd) {
        return dateTime.isAfter(betweenStart) && dateTime.isBefore(betweenEnd);
    }

    @FXML
    private void cancelClick() {
        hideMaintenance();
    }

    @Override
    public void loginSuccessful(Database database) {
        activeUser = database.getActiveUser();
        allAppointments = database.getAppointments();
        userChoiceBox.setItems(database.getUsers());
        userChoiceBox.setValue(activeUser);
        applyViewFilters();
        customerPicker.setItems(database.getCustomers());
    }

    @Override
    public void backClicked() {
        hideMaintenance();
    }
    
    @Override
    public void logout() {
        hideMaintenance();
    }
    
    private void loadActiveAppointment() {
        customerPicker.setValue(activeAppointment.getCustomer());
        titleField.setText(activeAppointment.getTitle());
        descriptionField.setText(activeAppointment.getDescription());
        locationField.setText(activeAppointment.getLocation());
        contactField.setText(activeAppointment.getContact());
        typeField.setText(activeAppointment.getType());
        urlField.setText(activeAppointment.getUrl());
        selectStart(activeAppointment.getStart());
        selectEnd(activeAppointment.getEnd());
        activeDuration = Duration.between(getSelectedStart(), getSelectedEnd());
    }
    
    private void selectStart(LocalDateTime dateTime) {
        selectDateTime(dateTime, startHour, startMinute, startAMPM, startDatePicker);
    }
    
    private void selectEnd(LocalDateTime dateTime) {
        selectDateTime(dateTime, endHour, endMinute, endAMPM, endDatePicker);
    }
    
    private void selectDateTime(LocalDateTime dateTime, ChoiceBox<Integer> hourPicker, ChoiceBox<Integer> minutePicker,
            ChoiceBox<String> ampmPicker, DatePicker datePicker) {
        
        selectHour(hourPicker, dateTime.getHour());
        minutePicker.setValue(dateTime.getMinute());
        selectAMPM(ampmPicker, dateTime.getHour());
        datePicker.setValue(dateTime.toLocalDate());
    }

    private void selectHour(ChoiceBox<Integer> hourPicker, int hour) {
        int selection = hour % 12;
        if (selection == 0) {
            selection = 12;
        }
        hourPicker.setValue(selection);
    }

    private void selectAMPM(ChoiceBox<String> ampmPicker, int hour) {
        if (hour < 12) {
            ampmPicker.setValue(AM);
        } else {
            ampmPicker.setValue(PM);
        }
    }

    private void saveActiveAppointment() {
        activeAppointment.setCustomer(customerPicker.getValue());
        activeAppointment.setTitle(titleField.getText());
        activeAppointment.setDescription(descriptionField.getText());
        activeAppointment.setLocation(locationField.getText());
        activeAppointment.setContact(contactField.getText());
        activeAppointment.setType(typeField.getText());
        activeAppointment.setUrl(urlField.getText());
        activeAppointment.setStart(getSelectedStart());
        activeAppointment.setEnd(getSelectedEnd());
    }
    
    private LocalDateTime getSelectedStart() {
        return getSelection(startHour, startMinute, startAMPM, startDatePicker);
    }
    
    private LocalDateTime getSelectedEnd() {
        return getSelection(endHour, endMinute, endAMPM, endDatePicker);
    }

    private LocalDateTime getSelection(ChoiceBox<Integer> hourPicker, ChoiceBox<Integer> minutePicker,
            ChoiceBox<String> ampmPicker, DatePicker datePicker) {

        int hour = hourPicker.getValue();
        if (hour == 12) {
            hour = 0;
        }
        if (ampmPicker.getValue().equals(PM)) {
            hour += 12;
        }
        return LocalDateTime.of(datePicker.getValue(), LocalTime.of(hour, minutePicker.getValue()));
    }

    private void showMaintenance(Appointment activeAppointment) {
        this.activeAppointment = activeAppointment;
        loadActiveAppointment();
        maintenancePane.setVisible(true);
        viewPane.setVisible(false);
    }

    private void hideMaintenance() {
        appointmentTable.refresh();
        viewPane.setVisible(true);
        maintenancePane.setVisible(false);
    }
}
