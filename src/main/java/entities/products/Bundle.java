package entities.products;

import entities.products.items.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Bundle
 * Description: A collection of other products that are sold together in a pre-determined group
 * Name: Nico Rotella
 * Date Created: August 5th, 2026
 * Last Edited: August 20th, 2026
 */

// Class
public class Bundle implements Product, ProductGroup<Product, Item> {
    
    // Attributes
    private final Map<Product, Integer> products = new HashMap<Product, Integer>();
    private final String name;
    private Integer id;
    
    
    // Constructors
    public Bundle(String n) {
        name = n;
    }


    /**
     * Description: Adds or updates a bundle product
     * Pre-Condition: Params are of correct types
     * Post-Condition: The bundle is updated to reflect the new desired products
     * @param product The product being added to the bundle
     * @param quantity The quantity of the product being added to the bundle
     */
    @Override
    public void add(Product product, Integer quantity) {
        products.merge(product, quantity, Integer::sum);
    }

    /**
     * Description: Removes and/or deletes a bundle product
     * Pre-Condition: Params are of correct types
     * Post-Condition: The bundle is updated to reflect the removed products
     * @param product The product being removed from the bundle
     * @param quantity The quantity of the product being removed from the bundle
     */
    @Override
    public void remove(Product product, Integer quantity) {
        // Taking that product out of their bundle
        products.computeIfPresent(product, (k, v) -> v - quantity);
        // If the product has no more quantity, removing it from their bundle
        if (products.get(product) <= 0)
            products.remove(product);
    }

    /**
     * Description: Deletes a product from the bundle entirely
     * Pre-Condition: Param is a product
     * Post-Condition: The bundle has this product deleted from it
     * @param product The product being deleted
     */
    @Override
    public void delete(Product product) {
        products.remove(product);
    }

    /**
     * Description: Deletes a product from the bundle entirely, by name
     * Pre-Condition: Param is a string
     * Post-Condition: The bundle has this product deleted from it
     * @param name The name of the product being deleted
     */
    @Override
    public void deleteByName(String name) {
        for (Product p : products.keySet()) {
            if (p.getName().equals(name))
                products.remove(p);
        }
    }

    /**
     * Description: Empties the bundle
     * Pre-Condition: None
     * Post-Condition: The bundle is cleared and has zero products
     */
    @Override
    public void empty() {
        products.clear();
    }

    /**
     * Description: Checks if a product is in the bundle and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the product
     * @return The boolean if found
     */
    @Override
    public boolean hasProduct(String name) {
        for (Product p : products.keySet()) {
            if (p.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Description: Gives the quantity of a desired product by its name
     * Pre-Condition: Product with the given name should be in the bundle
     * Post-Condition: Quantity of product in bundle is returned
     * @param name The name of the product
     * @return The quantity of the product in the bundle
     * @throws IllegalArgumentException if there is no product with that name in the bundle
     */
    @Override
    public Integer quantityOf(String name) throws IllegalArgumentException {
        for (var entry : products.entrySet()) {
            if (entry.getKey().getName().equals(name))
                return entry.getValue();
        }
        throw new IllegalArgumentException(String.format("Error, %s is not a product in bundle.", name));
    }

    /**
     * Definition: Checks if a bundle is empty
     * Pre-Condition: products is initialized
     * Post-Condition: A boolean is returned
     * @return The boolean if the bundle is empty
     */
    @Override
    public boolean isEmpty() {
        return products.isEmpty();
    }


    /**
     * Description: Determines the total cost of this bundle
     * Pre-Condition: This object is declared and initialized, price of product and desired quantity in maps are not null
     * Post-Condition: Total cost of the bundle is returned
     * @return The total cost of the bundle
     */
    @Override
    public int getPrice() {
        int price = 0;
        for (var entry : products.entrySet()) {
            price += (entry.getKey().getPrice() * entry.getValue());
        }
        return price;
    }

    /**
     * Description: Takes a bundle and returns it as a map of purely items to quantities
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
            else if (product instanceof ProductGroup<?, ?> productGroup) { // <?> product group of unknown types
                for (var subEntry : productGroup.toItems().entrySet()) {
                    items.merge(subEntry.getKey(), subEntry.getValue() * quantity, Integer::sum);
                }
            }
        }
        return items;
    }

    /**
     * Description: Gives the string value of the bundle
     * Pre-Condition: This bundle is initialized
     * Post-Condition: String is returned
     * @return The string value of the bundle
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("BUNDLE");
        sb.append(String.format("\nName: %s", name));

        for (var entry : products.entrySet())
            sb.append(String.format("\n%s - %d", entry.getKey().getName(), entry.getValue()));

        return sb.toString();
    }

    // Getters
    @Override
    public String getName() {
        return name;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getType() {
        return "BUNDLE";
    }

    // Setters
    @Override
    public void setId(Integer i) {
        id = i;
    }

}
