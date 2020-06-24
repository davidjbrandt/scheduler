package customers;

public class Customer {

    private Integer customerId;
    private String name;
    private String address;
    private String address2;
    private String city;
    private String postalCode;
    private String country;
    private String phone;

    public Customer(Integer customerId, String name, String address, String address2, String city,
            String postalCode, String country, String phone) {
        
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.phone = phone;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name;
    }
}
