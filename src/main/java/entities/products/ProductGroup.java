package entities.products;

import entities.products.items.Item;

import java.util.Map;

/**
 * Expandable
 * Description: An object is expandable if it holds products and needs to be able to flatten those products into items
 * Name: Nico Rotella
 * Date Created: August 5th, 2026
 * Last Edited: August 8th, 2026
 */

// Interface
public interface ProductGroup {

    void add(Product product, Integer quantity);
    void remove(Product product, Integer quantity);
    void delete(Product product);
    void empty();
    boolean hasProduct(String name);
    Integer quantityOf(String name) throws IllegalArgumentException;
    boolean isEmpty();
    Map<Item, Integer> toItems();

}
