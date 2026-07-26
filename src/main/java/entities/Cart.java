package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Cart
 * Description: Cart object to hold a customers shopping cart with their purchases
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 26th, 2026
 */

// Class
public class Cart {

    // Attributes
    private final Map<Card, Integer> cart = new HashMap<Card, Integer>(); // card -> quantity
    private int totalCost;


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


    /**
     * Description: Determines the total cost of the cart
     * Pre-Condition: This object is declared and initialized, price of card and desired quantity in maps are not null
     * Post-Condition: Total cost of the cart is set
     */
    private void determineCost() {
        totalCost = 0;

        for (var entry : cart.entrySet()) {
            totalCost += (entry.getKey().getPrice() * entry.getValue());
        }
    }

}
