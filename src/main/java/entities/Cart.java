package entities;

import entities.products.Product;

import java.util.HashMap;
import java.util.Map;

/**
 * Cart
 * Description: Cart object to hold a customers shopping cart with their purchases
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 28th, 2026
 */

// Class
public class Cart {

    // Attributes
    private final Map<Product, Integer> items = new HashMap<Product, Integer>(); // card -> quantity



    // Constructors
    public Cart() {

    }

    // Add cart
    public void addToCart(String name, Integer quantity) {
        /*
        Two pressing issues, the command takes in a quantity then a product. Right now this is just cards, not packs
        or decks. Eventually needs to be able to accommodate other types of products.
        Second, the customer inputs just the name of the product wanted, would be best to cross-reference with the
        inventory? Need to potentially have inventory access, add products by name

        // This logic should be in cartCMD, keep entity simple
         */
    }


    // Remove cart
    public void removeFromCart(String name, Integer quantity) {

    }

    /**
     * Description: Checks if a product is in the cart and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the product
     * @return The boolean if found
     */
    public boolean hasProduct(String name) {
        for (Product p : items.keySet()) {
            if (p.getName().equals(name))
                return true;
        }
        return false;
    }


    /**
     * Description: Determines the total cost of the cart
     * Pre-Condition: This object is declared and initialized, price of product and desired quantity in maps are not null
     * Post-Condition: Total cost of the cart is set
     * @return The total cost of the cart
     */
    public int price() {
        int price = 0;
        for (var entry : items.entrySet()) {
            price += (entry.getKey().getPrice() * entry.getValue());
        }
        return price;
    }

    /**
     * Description: Gives the quantity of a desired product by its name
     * Pre-Condition: Product with the given name should be in the cart
     * Post-Condition: Quantity of product in cart is returned
     * @param name The name of the product
     * @return The quantity of the product in the cart
     * @throws IllegalArgumentException if there is no product with that name in the cart
     */
    public Integer quantityOf(String name) throws IllegalArgumentException {
        for (var entry : items.entrySet()) {
            if (entry.getKey().getName().equals(name))
                return entry.getValue();
        }
        throw new IllegalArgumentException(String.format("Error, %s is not a product in cart.", name));
    }



    // Needs to string

    // Getters
    public Map<Product, Integer> getItems() {
        return items;
    }

}
