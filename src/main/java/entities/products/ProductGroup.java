package entities.products;

import entities.products.items.Item;

import java.util.Map;

/**
 * ProductGroup
 * Description: An object is a product group if it holds products and needs to be able to flatten those products into items
 * Name: Nico Rotella
 * Date Created: August 5th, 2026
 * Last Edited: August 20th, 2026
 */

// Interface
public interface ProductGroup<T extends Product, U extends Item> {

    void add(T product, Integer quantity);
    void remove(T product, Integer quantity);
    void delete(T product);
    void empty();
    boolean hasProduct(String name);
    Integer quantityOf(String name) throws IllegalArgumentException;
    boolean isEmpty();
    Map<U, Integer> toItems();

}
