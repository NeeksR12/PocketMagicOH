package entities;

import entities.products.ProductGroup;
import entities.products.Product;
import entities.products.items.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Cart
 * Description: Cart object to hold a customers shopping cart with their purchases
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: August 19th, 2026
 */

// Class
public class Cart implements ProductGroup<Product, Item> {

    // Attributes
    private final Map<Product, Integer> products = new HashMap<Product, Integer>();
    private Integer id;


    // Constructors
    public Cart() {}

    /**
     * Description: Adds or updates a cart product
     * Pre-Condition: Params are of correct types
     * Post-Condition: The cart is updated to reflect the new desired products
     * @param product The product being added to the cart
     * @param quantity The quantity of the product being added to the cart
     */
    public void add(Product product, Integer quantity) {
        products.merge(product, quantity, Integer::sum);
    }
    
    /**
     * Description: Removes and/or deletes a cart product
     * Pre-Condition: Params are of correct types
     * Post-Condition: The cart is updated to reflect the removed products
     * @param product The product being removed from the cart
     * @param quantity The quantity of the product being removed from the cart
     */
    public void remove(Product product, Integer quantity) {
        // Taking that product out of their cart
        products.computeIfPresent(product, (k, v) -> v - quantity);
        // If the product has no more quantity, removing it from their cart
        if (products.get(product) <= 0)
            products.remove(product);
    }

    /**
     * Description: Deletes a product from the cart entirely
     * Pre-Condition: Param is a product
     * Post-Condition: The cart has this product deleted from it
     * @param product The product being deleted
     */
    public void delete(Product product) {
        products.remove(product);
    }

    /**
     * Description: Empties the cart
     * Pre-Condition: None
     * Post-Condition: The cart is cleared and has zero products
     */
    public void empty() {
        products.clear();
    }

    /**
     * Description: Checks if a product is in the cart and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the product
     * @return The boolean if found
     */
    public boolean hasProduct(String name) {
        for (Product p : products.keySet()) {
            if (p.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Description: Determines the total cost of the cart
     * Pre-Condition: This object is declared and initialized, price of product and desired quantity in maps are not null
     * Post-Condition: Total cost of the cart is returned
     * @return The total cost of the cart
     */
    public int price() {
        int price = 0;
        for (var entry : products.entrySet()) {
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
        for (var entry : products.entrySet()) {
            if (entry.getKey().getName().equals(name))
                return entry.getValue();
        }
        throw new IllegalArgumentException(String.format("Error, %s is not a product in cart.", name));
    }

    /**
     * Definition: Checks if a cart is empty
     * Pre-Condition: products is initialized
     * Post-Condition: A boolean is returned
     * @return The boolean if the cart is empty
     */
    public boolean isEmpty() {
        return products.isEmpty();
    }

    /**
     * Description: Takes a cart and returns it as a map of purely items to quantities
     * Pre-Condition: This object is declared and initialized
     * Post-Condition: The map of items to quantities is returned
     * @return The map of items to quantities (HashMap)
     */
    @Override
    public Map<Item, Integer> toItems() {
        Map<Item, Integer> items = new HashMap<Item, Integer>();

        // Going through each product
        for (var entry : products.entrySet()) {

            Product product = entry.getKey();
            Integer quantity = entry.getValue();

            // Seeing if the product is already an item
            if (product instanceof Item item) {
                items.merge(item, quantity, Integer::sum);
            }
            // Collapsing the product into its items
            else if (product instanceof ProductGroup<?, ?> productGroup) {
                for (var subEntry : productGroup.toItems().entrySet()) {
                    items.merge(subEntry.getKey(), subEntry.getValue() * quantity, Integer::sum);
                }
            }
        }
        return items;
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

        for (var entry : products.entrySet())
            sb.append(String.format("\n%s - %d", entry.getKey().getName(), entry.getValue()));

        return sb.toString();
    }
    
    // Getters
    public Map<Product, Integer> getProducts() {
        return products;
    }

    public Integer getId() {
        return id;
    }

    // Setters
    public void setId(Integer i) {
        id = i;
    }

}
