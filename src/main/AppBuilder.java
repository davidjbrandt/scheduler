package main;

import alerts.AlertFactory;
import calendar.AppointmentsFactory;
import calendar.CalendarController;
import calendar.TimeConverter;
import customers.CustomerFactory;
import customers.CustomersController;
import database.Database;
import database.DatabaseFactory;
import database.InMemoryDB;
import database.MySQL;
import events.Broadcaster;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import localization.Bundle;
import localization.Localizer;
import login.LoginController;
import login.tracking.Logger;
import menu.MenuController;
import reports.Report;
import reports.ReportFactory;
import reports.ReportsController;
import scenes.Switcher;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class AppBuilder {
    
    private final String LOGIN_FORM_PATH = "../login/login.fxml";
    private final String MAIN_FORM_PATH = "../main/main.fxml";
    private final String MENU_FORM_PATH = "../menu/menu.fxml";
    private final String CALENDAR_FORM_PATH = "../calendar/calendar.fxml";
    private final String CUSTOMERS_FORM_PATH = "../customers/customers.fxml";
    private final String REPORTS_FORM_PATH = "../reports/reports.fxml";
    
    public void setupApp(Stage primaryStage) throws Exception {
        Broadcaster broadcaster = new Broadcaster();
        TimeConverter timeConverter = new TimeConverter();
        broadcaster.subscribe(new Logger(timeConverter));
        AppointmentsFactory appointmentsFactory = new AppointmentsFactory(timeConverter);
        broadcaster.subscribe(appointmentsFactory);
        CustomerFactory customerFactory = new CustomerFactory();
        Localizer reportsLocalizer = makeLocalizer(Bundle.REPORTS);
        ReportFactory reportFactory = new ReportFactory(reportsLocalizer);
        DatabaseFactory databaseFactory = new DatabaseFactory(appointmentsFactory, customerFactory, reportFactory);
        Database database = new InMemoryDB(databaseFactory);
        
        broadcaster.subscribe(new alerts.EventTrigger(new AlertFactory(makeLocalizer(Bundle.ALERTS)), broadcaster));
        
        broadcaster.subscribe(new database.EventTrigger(database, broadcaster));
        LoginController loginController = new LoginController(broadcaster,makeLocalizer(Bundle.LOGIN));
        broadcaster.subscribe(loginController);
        Scene loginScene = new Scene(loadFxmlFile(LOGIN_FORM_PATH, loginController));
        MenuController menuController = new MenuController(broadcaster, makeLocalizer(Bundle.MENU));
        broadcaster.subscribe(menuController);
        Node menu = loadFxmlFile(MENU_FORM_PATH, menuController);
        CalendarController calendarController = new CalendarController(appointmentsFactory, broadcaster,
                makeLocalizer(Bundle.CALENDAR));
        broadcaster.subscribe(calendarController);
        Node calendar = loadFxmlFile(CALENDAR_FORM_PATH, calendarController);
        CustomersController customersController = new CustomersController(customerFactory, broadcaster,
                makeLocalizer(Bundle.CUSTOMERS));
        broadcaster.subscribe(customersController);
        Node customers = loadFxmlFile(CUSTOMERS_FORM_PATH, customersController);
        ArrayList<Report> reportsList = setupReportsList(database, reportsLocalizer);
        ReportsController reportsController = new ReportsController(reportsList, reportsLocalizer);
        Node reports = loadFxmlFile(REPORTS_FORM_PATH, reportsController);
        ContentManager contentManager = new ContentManager(menu, calendar, customers, reports);
        Localizer mainLocalizer = makeLocalizer(Bundle.MAIN);
        MainController mainController = new MainController(contentManager, broadcaster, mainLocalizer);
        broadcaster.subscribe(mainController);
        Scene mainScene = new Scene(loadFxmlFile(MAIN_FORM_PATH, mainController));
        broadcaster.subscribe(new Switcher(primaryStage, loginScene, mainScene));
        
        showUI(primaryStage, mainLocalizer);
    }
    
    private void showUI(Stage primaryStage, Localizer mainLocalizer) {
        primaryStage.setTitle(mainLocalizer.translate("title"));
        primaryStage.show();
    }
    
    private <T> T loadFxmlFile(String fxmlFilePath, Object controller) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
        loader.setController(controller);
        return loader.load();
    }
    
    private ArrayList<Report> setupReportsList(Database database, Localizer localizer) {
        ArrayList<Report> reports = new ArrayList<>();
        /* Passing method references as a Supplier gives the Report class the flexibility to call different database
         *  methods without making a new subclass for each individual report. */
        reports.add(new Report("appointmentTypesByMonth", database::getAppointmentTypesByMonth, localizer));
        reports.add(new Report("appointmentsByCustomer", database::getAppointmentsByCustomer, localizer));
        return reports;
    }
    
    private Localizer makeLocalizer(Bundle bundle) {
        return new Localizer(ResourceBundle.getBundle(bundle.getName(), Locale.getDefault()));
    }
}
