package databases;

import entities.Customer;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Customers
 * Description: Database showing all the customers in the system
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 29th, 2026
 */

// Class
public class Customers {

    // Attributes
    private final ArrayList<Customer> shoppers = new ArrayList<Customer>();

    // Constructor
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

    /**
     * Description: Adds a customer to shoppers
     * Pre-Condition: Customers and Customer are initialized and the customer is not already a shopper
     * Post-Condition: Customer is added or exception is thrown
     * @param c The customer
     * @throws IllegalArgumentException if the customer is already a shopper
     */
    public void addShopper(Customer c) {
        if (isCustomerAShopper(c.getName()))
            shoppers.add(c);
        else
            throw new IllegalArgumentException(String.format("Error, %s is already a shopper therefore cannot be added."
                    , c.getName()));
    }

    /**
     * Description: Removes a shopper by name
     * Pre-Condition: Customers is initialized
     * Post-Condition: The customer is removed from shoppers
     * @param name The name of the customer
     * @throws IllegalArgumentException if the customer is not a shopper
     */
    public void removeShopperByName(String name) throws IllegalArgumentException {
        if (isCustomerAShopper(name))
            shoppers.remove(getCustomerByName(name));
        else
            throw new IllegalArgumentException(String.format("Error, %s is not a shopper therefore cannot be removed.",
                    name));
    }

    /**
     * Description: Gives the string value of the customers, this is what should be in the text file
     * Pre-Condition: This Customers is initialized
     * Post-Condition: String is returned
     * @return The string value of the customers
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Customer c : shoppers) {
            sb.append(c.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

}
