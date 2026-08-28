package databases;


import entities.Cart;
import entities.products.Bundle;
import entities.products.ProductGroup;
import entities.products.items.Card;
import entities.products.Product;
import entities.products.items.Item;

import java.util.*;

/**
 * Inventory
 * Description: Contains the stores inventory of cards and related methods to manipulate it
 * Name: Nico Rotella
 * Date Created: May 25th, 2026
 * Last Edited: August 28th, 2026
 */
public class Inventory {

    // Attributes
    private final TrackedCollection<Product> products = new TrackedCollection<>();


    // Constructor
    public Inventory() {}


    // Tracked collection methods
    /**
     * Description: Checks if a product is in the inventory and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the product
     * @return The boolean if found
     */
    public boolean hasProduct(String name) {
        return products.has(name);
    }

    /**
     * Description: Looks for a product in the inventory by its name
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Product is returned or exception is thrown
     * @param name The name of the product being searched for
     * @return The product if found
     * @throws NoSuchElementException if not found
     */
    public Product getProductByName(String name) throws NoSuchElementException {
        try {
            return products.getByName(name);
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException("Error, this product is not in the inventory.");
        }
    }

    /**
     * Description: Adds a product to inventory
     * Pre-Condition: Inventory and Product are initialized and product is not already in the inventory
     * Post-Condition: Product is added or exception is thrown
     * @param p The product
     * @throws IllegalArgumentException if the card is already in the inventory
     */
    public void addProduct(Product p) {
        try {
            products.add(p);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error, this product is already in the inventory therefore cannot be added.");
        }
    }

    /**
     * Description: Takes a product and marks it as dirty for the DB to worry about
     * Pre-Condition: Should only mark a product dirty if being created or updated
     * Post_Condition: The product is marked dirty
     * @param p The product
     */
    public void markDirty(Product p) {
        products.markDirty(p);
    }

    /**
     * Description: Clears the dirty and deleted sets
     * Pre-Condition: None
     * Post-Condition: Dirty and deleted sets are cleared
     */
    public void clearDirtyTracking() {
        products.clearDirtyTracking();
    }


    // General Product Operations
    /**
     * Description: Returns a product by its id
     * Pre-Condition: Param is an int
     * Post-Condition: The product is returned
     * @param id The product id
     * @return The product
     * @throws NoSuchElementException if not found
     */
    public Product getProductById(int id) {
        for (Product p : products.values()) {
            if (p.getId() != null && p.getId() == id)
                return p;
        }
        throw new NoSuchElementException(String.format("Error, product id %d not found.", id));
    }

    /**
     * Description: Removes a product from the inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: The product is removed from the inventory
     * @param name The name of the product being removed
     * @throws IllegalArgumentException if the product is not in the inventory
     */
    public void deleteProductByName(String name) throws IllegalArgumentException {
        try {
            products.deleteByName(name); // This also marks deleted
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Error, this product is not in the inventory therefore cannot be removed");
        }

        // Removing the product from any other product that may contain it
        for (Product p: products.values()) {
            if (p instanceof ProductGroup<?, ?> pc && pc.hasProduct(name))
                pc.deleteByName(name);
        }
    }


    // Card Specific Operations
    /**
     * Description: Checks if a card is in the inventory and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the card
     * @return The boolean if found
     */
    public Boolean hasCard(String name) {
        for (Product p : products.values()) {
            if (p instanceof Card c) { // NOTE: This is called pattern notation, no need to declare and instantiate later
                if (c.getName().equals(name))
                    return true;
            }
        }
        return false;
    }

    /**
     * Description: Looks for a card in the inventory by its name
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Card is returned or exception is thrown
     * @param name The name of the card being searched for
     * @return The card if found
     * @throws NoSuchElementException if not found
     */
    public Card getCardByName(String name) throws NoSuchElementException{
        for (Product p : products.values()) {
            if (p instanceof Card c) {
                if (c.getName().equals(name))
                    return c; // Found
            }
        }
        throw new NoSuchElementException("Error, this card is not in the inventory."); // Not found
    }

    /**
     * Description: Returns a card by its id
     * Pre-Condition: Param is an int
     * Post-Condition: The card is returned
     * @param id The card id
     * @return The card
     * @throws NoSuchElementException if not found
     */
    public Card getCardById(int id) {
        for (Product p : products.values()) {
            if (p instanceof Card c && c.getId() != null && c.getId() == id)
                return c;
        }
        throw new NoSuchElementException(String.format("Error, card id %d not found.", id));
    }

    // Bundle specific operations
    /**
     * Description: Checks if a bundle is in the inventory and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the bundle
     * @return The boolean if found
     */
    public Boolean hasBundle(String name) {
        for (Product p : products.values()) {
            if (p instanceof Bundle b) {
                if (b.getName().equals(name))
                    return true;
            }
        }
        return false;
    }

    /**
     * Description: Looks for a bundle in the inventory by its name
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Bundle is returned or exception is thrown
     * @param name The name of the bundle being searched for
     * @return The bundle if found
     * @throws NoSuchElementException if not found
     */
    public Bundle getBundleByName(String name) throws NoSuchElementException{
        for (Product p : products.values()) {
            if (p instanceof Bundle b) {
                if (b.getName().equals(name))
                    return b; // Found
            }
        }
        throw new NoSuchElementException("Error, this bundle is not in the inventory."); // Not found
    }

    // General Class Operations
    /**
     * Description: Checks if there is stock in the inventory
     * Pre-Condition: The inventory object is initialized
     * Post-Condition: A boolean is returned true if there is stock and false if not
     * @return The boolean if there is stock
     */
    public boolean hasStock() {
        return !products.isEmpty();
    }

    /**
     * Description: Removes the stock from the inventory from the items in the given cart
     * Pre-Condition: Desired quantities in cart should be greater than the stock of the product and cart is not null
     * Post-Condition: Stock is removed from inventory or exception is thrown
     * @param c The cart
     * @throws IllegalArgumentException if the cart desired more of a product than it is stocked
     */
    public void purchaseCart(Cart c) throws IllegalArgumentException {

        // Checking that the cart is not null
        if (c == null)
            throw new IllegalArgumentException("Error, this cart is null.");

        // Checking stock availability
        for (var entry : c.toItems().entrySet()) {
            Item item = entry.getKey();
            Integer quantity = entry.getValue();

            // Checking if there is less stock than desired
            if (item.getStock() < quantity) {
                throw new IllegalArgumentException("Error, checkout could not be completed due to stock.");
            }
        }

        // Sufficient stock, removing stock from inventory
        for (var entry : c.toItems().entrySet()) {
            entry.getKey().removeStock(entry.getValue()); // Shouldn't throw since checked above
        }
    }

    /**
     * Description: Checks if a product is in a product group in the inventory
     * Pre-Condition: None
     * Post-Condition: Boolean is returned if product is in a product group
     * @param name The name of the product being checked
     * @return Boolean if in a product group
     */
    public boolean isProductInProductGroup(String name) {
        for (Product p : products.values()) {
            if (p instanceof ProductGroup<?, ?> pg) {
                if (pg.hasProduct(name))
                    return true; // Is in a product group
            }
        }
        return false;
    }

    /**
     * Description: Gives the string value of the inventory, this is what should be in the text file
     * Pre-Condition: This inventory is initialized
     * Post-Condition: String is returned
     * @return The string value of the inventory
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Product p : products.values()) {
            sb.append(p.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    // Getters
    public TrackedCollection<Product> getProducts() {
        return products;
    }

    public Set<Product> getDirtyProducts() {
        return products.getDirty();
    }

    public Set<Product> getDeletedProducts() {
        return products.getDeleted();
    }

}
