package entities;


/**
 * Customer
 * Description: A customer object which can hold a cart to purchase items
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: August 17th, 2026
 */

// Class
public class Customer {

    // Attributes
    private final String name;
    private Cart cart = new Cart();

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
        return name + cart + "\n";
    }

    // Getters
    public String getName() {
        return name;
    }

    public Cart getCart() {
        return cart;
    }

    // Setter
    public void setCart(Cart c) {
        cart = c;
    }
}
