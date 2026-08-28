package entities;


import databases.TrackedCollection;
import entities.products.Deck;
import entities.products.Product;
import entities.products.items.Card;

import java.util.*;

/**
 * Customer
 * Description: A customer object which can hold carts and decks
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: August 28th, 2026
 */

// Class
public class Customer implements Persistable {

    // Attributes
    private final String name;
    private TrackedCollection<Cart> carts = new TrackedCollection<>();
    private TrackedCollection<Deck> decks = new TrackedCollection<>();
    private Integer id;


    // Constructor
    public Customer(String n) {
        name = n;
    }


    // Tracked cart methods
    /**
     * Description: Checks if a customer has a cart and returns a boolean
     * Pre-Condition: Customer is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the cart
     * @return The boolean if found
     */
    public boolean hasCart(String name) {
        return carts.has(name);
    }

    /**
     * Description: Adds a cart to this customer's carts
     * Pre-Condition: Customer and cart are already initialized and the customer does not already have this cart
     * Post-Condition: The cart is added to this customer's carts
     * @param c The cart being added
     * @throws IllegalArgumentException if the customer already has this cart
     */
    public void addCart(Cart c) throws IllegalArgumentException{
        try {
            carts.add(c);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error, this customer already has this cart therefore it cannot be added.");
        }
    }

    /**
     * Description: Looks for a cart in the customer by its name
     * Pre-Condition: Customer is initialized
     * Post-Condition: cart is returned or exception is thrown
     * @param name The name of the cart being searched for
     * @return The cart if found
     * @throws NoSuchElementException if not found
     */
    public Cart getCartByName(String name) throws NoSuchElementException {
        try {
            return carts.getByName(name);
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("Error, this customer does not have this cart.");
        }
    }

    /**
     * Description: Deletes a cart by name
     * Pre-Condition: Customer is initialized
     * Post-Condition: The cart is removed from this customer
     * @param name The name of the cart
     * @throws IllegalArgumentException if this is not a cart that the customer has
     */
    public void deleteCartByName(String name) throws IllegalArgumentException {
        try {
            carts.deleteByName(name);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException(String.format("Error, %s does not have a cart named %s therefore it " +
                    "cannot be deleted.", this.getName(), name));
        }
    }

    /**
     * Description: Takes a cart and marks it as dirty for the DB to worry about
     * Pre-Condition: Should only mark a cart dirty if its contents have been updated
     * Post_Condition: The cart is marked dirty
     * @param c The cart
     */
    public void markCartDirty(Cart c) {
        carts.markDirty(c);
    }

    /**
     * Description: Clears the dirty and deleted sets
     * Pre-Condition: None
     * Post-Condition: Dirty and deleted sets are cleared
     */
    public void clearCartDirtyTracking() {
        carts.clearDirtyTracking();
    }


    // Tracked deck methods
    /**
     * Description: Checks if a customer has a deck and returns a boolean
     * Pre-Condition: Customer is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the deck
     * @return The boolean if found
     */
    public boolean hasDeck(String name) {
        return decks.has(name);
    }

    /**
     * Description: Adds a deck to this customer's decks
     * Pre-Condition: Customer and deck are already initialized and the customer does not already have this deck
     * Post-Condition: The deck is added to this customer's decks
     * @param d The deck being added
     * @throws IllegalArgumentException if the customer already has this deck
     */
    public void addDeck(Deck d) throws IllegalArgumentException {
        try {
            decks.add(d);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error, this customer already has this deck therefore it cannot be added.");
        }
    }

    /**
     * Description: Looks for a deck in the customer by its name
     * Pre-Condition: Customer is initialized
     * Post-Condition: Deck is returned or exception is thrown
     * @param name The name of the deck being searched for
     * @return The deck if found
     * @throws NoSuchElementException if not found
     */
    public Deck getDeckByName(String name) throws NoSuchElementException {
        try {
            return decks.getByName(name);
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("Error, this customer does not have this deck.");
        }
    }


    /**
     * Description: Deletes a deck by name
     * Pre-Condition: Customer is initialized
     * Post-Condition: The deck is removed from this customer
     * @param name The name of the deck
     * @throws IllegalArgumentException if this is not a deck that the customer has
     */
    public void deleteDeckByName(String name) throws IllegalArgumentException {
        try {
            decks.deleteByName(name);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException(String.format("Error, %s does not have a deck named %s therefore it " +
                    "cannot be deleted.", this.getName(), name));
        }
    }

    /**
     * Description: Takes a deck and marks it as dirty for the DB to worry about
     * Pre-Condition: Should only mark a deck dirty if its contents have been updated
     * Post_Condition: The deck is marked dirty
     * @param d The deck
     */
    public void markDeckDirty(Deck d) {
        decks.markDirty(d);
    }

    /**
     * Description: Clears the dirty and deleted sets
     * Pre-Condition: None
     * Post-Condition: Dirty and deleted sets are cleared
     */
    public void clearDeckDirtyTracking() {
        decks.clearDirtyTracking();
    }


    // General class operations
    /**
     * Description: Checks if this customer contains a product in either a cart or deck it has
     * Pre-Condition: None
     * Post-Condition: A boolean is returned if the product is in the customer
     * @param name The name of the product
     * @return Boolean if the customer is
     */
    public boolean containsProduct(String name) {
        // Carts
        for (Cart c : carts.values()) {
            if (c.hasProduct(name))
                return true;
        }
        // Decks
        for (Deck d : decks.values()) {
            if (d.hasProduct(name))
                return true;
        }
        return false; // Does not contain
    }

    /**
     * Description: Deletes a product from within the carts and decks of a customer
     * Pre-Condition: Param is a product
     * Post-Condition: Product is deleted from this customer entirely
     * @param p The product being deleted
     */
    public void deleteProduct(Product p) {
        // Carts
        for (Cart cart : carts.values())
            cart.delete(p);
        // Decks
        if (p instanceof Card c) {
            for (Deck deck : decks.values())
                deck.delete(c);
        }
    }

    /**
     * Description: Gives the string value of the customer, this is what is to be displayed in Customers
     * Pre-Condition: This Customer is initialized
     * Post-Condition: String is returned
     * @return the string value of the customer
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("CUSTOMER - %s", name));

        for (Cart c : carts.values())
            sb.append(c.toString());

        return sb.toString();
    }

    // Getters
    @Override
    public String getName() {
        return name;
    }


    @Override
    public Integer getId() {
        return id;
    }

    public TrackedCollection<Cart> getCarts() {
        return carts;
    }

    public TrackedCollection<Deck> getDecks () {
        return decks;
    }

    public Set<Cart> getDirtyCarts() {
        return carts.getDirty();
    }

    public Set<Deck> getDirtyDecks () {
        return decks.getDirty();
    }

    public Set<Cart> getDeletedCarts() {
        return carts.getDeleted();
    }

    public Set<Deck> getDeletedDecks() {
        return decks.getDeleted();
    }

    // Setter
    @Override
    public void setId(Integer i) {
        id = i;
    }

    public void setCarts(TrackedCollection<Cart> c) {
        carts = c;
    }

    public void setDecks(TrackedCollection<Deck> d) {
        decks = d;
    }

}
