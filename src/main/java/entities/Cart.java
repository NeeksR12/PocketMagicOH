package entities;

import entities.products.Product;

import java.util.HashMap;
import java.util.Map;

/**
 * Cart
 * Description: Cart object to hold a customers shopping cart with their purchases
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 29th, 2026
 */

// Class
public class Cart {

    // Attributes
    private final Map<Product, Integer> items = new HashMap<Product, Integer>(); // card -> quantity


    // Constructors
    public Cart() {}

    /**
     * Description: Adds or updates a cart item
     * Pre-Condition: Params are of correct types
     * Post-Condition: The cart is updated to reflect the new desired items
     * @param product The product being added to the cart
     * @param quantity The quantity of the product being added to the cart
     */
    public void addToCart(Product product, Integer quantity) {
        // This product is already in the cart
        items.computeIfPresent(product, (k, v) -> v + quantity);
        // This product was not in the cart
        items.putIfAbsent(product, quantity);
    }


    /**
     * Description: Removes and/or deletes a cart item
     * Pre-Condition: Params are of correct types
     * Post-Condition: The cart is updated to reflect the removed items
     * @param product The product being removed from the cart
     * @param quantity The quantity of the product being removed from the cart
     */
    public void removeFromCart(Product product, Integer quantity) {
        // Taking that item out of their cart
        items.computeIfPresent(product, (k, v) -> v - quantity);
        // If the item has no more quantity, removing it from their cart
        if (items.get(product) <= 0)
            items.remove(product);
    }

    /**
     * Description: Empties the cart
     * Pre-Condition: None
     * Post-Condition: The cart is cleared and has zero items
     */
    public void emptyCart() {
        items.clear();
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


    /**
     * Description: Gives the string value of the cart, this is what is to be displayed in each customer. NOTE: starts with \n
     * Pre-Condition: This cart is initialized
     * Post-Condition: String is returned
     * @return The string value of the cart
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (var entry : items.entrySet())
            sb.append(String.format("\n%s - %d", entry.getKey().getName(), entry.getValue()));

        return sb.toString();
    }

    // Getters
    public Map<Product, Integer> getItems() {
        return items;
    }

}
