package databases;

import entities.Customer;
import entities.products.Product;

import java.util.*;

/**
 * Customers
 * Description: Database showing all the customers in the system
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: August 28th, 2026
 */

// Class
public class Customers {

    // Attributes
    private final TrackedCollection<Customer> shoppers = new TrackedCollection<>();


    // Constructor
    public Customers() {}


    // Tracked collection methods
    /**
     * Description: Checks if a customer is a shopper by name and returns a boolean
     * Pre-Condition: Customers is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the customer
     * @return The boolean if found
     */
    public boolean hasCustomer(String name) {
        return shoppers.has(name);
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
        try {
            return shoppers.getByName(name);
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("Error, this customer is not a shopper.");
        }
    }

    /**
     * Description: Adds a customer to shoppers
     * Pre-Condition: Customers and Customer are initialized and the customer is not already a shopper
     * Post-Condition: Customer is added or exception is thrown
     * @param c The customer
     * @throws IllegalArgumentException if the customer is already a shopper
     */
    public void addCustomer(Customer c) {
        try {
            shoppers.add(c);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error, %s is already a shopper therefore cannot be added."
                    , c.getName()));
        }
    }

    /**
     * Description: Takes a customer and marks it as dirty for the DB to worry about
     * Pre-Condition: Should only mark a customer dirty if being created or updated (including its carts)
     * Post_Condition: The customer is marked dirty
     * @param c The customer
     */
    public void markDirty(Customer c) {
        shoppers.markDirty(c);
    }

    /**
     * Description: Clears the dirty and deleted sets of the shoppers
     * Pre-Condition: None
     * Post-Condition: Dirty and deleted sets are cleared
     */
    public void clearDirtyTracking() {
        // Clearing the dirty tracking within each dirty customer
        for (Customer c : shoppers.getDirty()) {
            c.clearCartDirtyTracking();
            c.clearDeckDirtyTracking();
        }

        // Clearing dirty shoppers
        shoppers.clearDirtyTracking();
    }


    // General customer operations
    /**
     * Description: Deletes a shopper by name
     * Pre-Condition: Customers is initialized
     * Post-Condition: The customer is removed from shoppers
     * @param name The name of the customer
     * @throws IllegalArgumentException if the customer is not a shopper
     */
    public void deleteCustomerByName(String name) throws IllegalArgumentException {
        try {
            shoppers.deleteByName(name);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException(String.format("Error, %s is not a shopper therefore cannot be removed.",
                    name));
        }
    }

    /**
     * Description: Deletes a product from all customers. Only to be used if the product itself is being deleted. Failsafe.
     * Pre-Condition: Param must be a product and customers is initialized
     * Post-Condition: The product is deleted from every customer
     * @param product The product
     */
    public void deleteProductFromAllCustomers(Product product) {
        for (Customer c : shoppers.values()) {
            c.deleteProduct(product);
        }
    }

    /**
     * Description: Checks if a product is in a product group in the inventory
     * Pre-Condition: None
     * Post-Condition: Boolean is returned if product is in a product group
     * @param name The name of the product being checked
     * @return Boolean if in a product group
     */
    public boolean isProductInACustomer(String name) {
        for (Customer c : shoppers.values()) {
            if (c.containsProduct(name))
                return true;
        }
        return false;
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

        for (Customer c : shoppers.values()) {
            sb.append(c.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    // Getters
    public Set<Customer> getDirtyShoppers() {
        return shoppers.getDirty();
    }

    public Set<Customer> getDeletedShoppers() {
        return shoppers.getDeleted();
    }

}
