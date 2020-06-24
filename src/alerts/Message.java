package alerts;

public enum Message {
    APPOINTMENT_OUTSIDE_BUSINESS_HOURS ("appointmentOutsideBusinessHours"),
    APPOINTMENT_TIME_NOT_AVAILABLE ("appointmentTimeNotAvailable"),
    DELETE_APPOINTMENT ("deleteAppointment"),
    DELETE_CUSTOMER ("deleteCustomer"),
    MEETING_SOON ("meetingSoon"),
    LOGIN ("login"),
    REQUIRED_FIELD ("requiredField");
    
    private final String name;
    
    Message(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
