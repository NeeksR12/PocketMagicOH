package entities;


/**
 * Customer
 * Description: A customer object which can hold a cart to purchase items
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 28th, 2026
 */

// Class
public class Customer {

    // Attributes
    private final String name;
    private final Cart cart = new Cart();

    // Constructor
    public Customer(String n) {
        name = n;
    }

    /**
     * Description: Gives the string value of the customer, this is what is to be displayed in Customers
     * Pre-Condition: This Customer is initialized
     * Post-Condition: String is returned
     * @return the string value of the customer
     */
    @Override
    public String toString() {
        return name + cart;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Cart getCart() {
        return cart;
    }
}
