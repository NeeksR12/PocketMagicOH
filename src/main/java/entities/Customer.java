package entities;


/**
 * Customer
 * Description: A customer object which can hold a cart to purchase items
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 26th, 2026
 */

// Class
public class Customer {

    // Attributes
    private final String name;
    private Cart cart;
    // Needs way to have cart, one for now do multiple later

    // Constructor
    public Customer(String n) {
        name = n;
    }



    // Getters
    public String getName() {
        return name;
    }
}
