package databases;

import entities.Customer;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Customers
 * Description: Database showing all the customers in the system
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 26th, 2026
 */

// Class
public class Customers {

    // Attributes
    private ArrayList<Customer> shoppers = new ArrayList<Customer>();

    // Constructors
    /**
     * Default constructor
     */
    public Customers() {}


    /**
     * Description: Checks if a customer is a shopper by name and returns a boolean
     * Pre-Condition: Customers is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the customer
     * @return The boolean if found
     */
    public boolean isCustomerAShopper(String name) {
        for (Customer c : shoppers) {
            if (c.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Description: Looks for a customer who is shopping by name and returns them if found
     * Pre-Condition: Customers is initialized
     * Post-Condition: Customer is returned or exception is thrown
     * @param name The name of the customer
     * @return The customer if found
     * @throws NoSuchElementException if not found
     */
    public Customer getCustomerByName(String name) throws NoSuchElementException {
        for (Customer c : shoppers) {
            if (c.getName().equals(name))
                return c;
        }
        throw new NoSuchElementException("Error, this customer is not a shopper.");
    }

    /*
    If needed, add more search through shoppers command, I'm sure checking what is in customers carts would be valuable
    Must add a toString() still
    NAME
    cart.toString()
     */

}
