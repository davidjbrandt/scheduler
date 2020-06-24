package localization;

public enum Bundle {
    ALERTS ("Alerts"),
    CALENDAR ("Calendar"),
    CUSTOMERS ("Customers"),
    LOGIN ("Login"),
    MAIN ("Main"),
    MENU ("Menu"),
    REPORTS ("Reports");
    
    private final String PATH = "localization.resources.";
    private final String name;
    
    Bundle(String name) {
        this.name = PATH + name;
    }
    
    public String getName() {
        return name;
    }
}
