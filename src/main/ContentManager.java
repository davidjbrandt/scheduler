package main;

import javafx.scene.Node;

public class ContentManager {
    
    private final Node menu;
    private final Node calendar;
    private final Node customers;
    private final Node reports;
    
    public ContentManager(Node menu, Node calendar, Node customers, Node reports) {
        this.menu = menu;
        this.calendar = calendar;
        this.customers = customers;
        this.reports = reports;
    }
    
    public Node getMenu() {
        return menu;
    }
    
    public Node getCalendar() {
        return calendar;
    }
    
    public Node getCustomers() {
        return customers;
    }
    
    public Node getReports() {
        return reports;
    }
}
