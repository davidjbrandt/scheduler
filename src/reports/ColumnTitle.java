package reports;

public enum ColumnTitle {
    APPOINTMENTS ("appointments"),
    CUSTOMER ("customer"),
    TYPES("types"),
    MONTH ("month");
    
    private final String value;
    
    ColumnTitle(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
