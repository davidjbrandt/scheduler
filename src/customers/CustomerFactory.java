package customers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerFactory {

    public Customer newCustomer() {
        return new Customer(null, "", "", "", "", "", "", "");
    }
    
    public Customer existingCustomer(ResultSet resultSet) throws SQLException {
        Customer customer = newCustomer();
        customer.setCustomerId(resultSet.getInt("customerId"));
        customer.setName(resultSet.getString("customerName"));
        customer.setAddress(resultSet.getString("address"));
        customer.setAddress2(resultSet.getString("address2"));
        customer.setCity(resultSet.getString("city"));
        customer.setPostalCode(resultSet.getString("postalCode"));
        customer.setCountry(resultSet.getString("country"));
        customer.setPhone(resultSet.getString("phone"));
        return customer;
    }
}