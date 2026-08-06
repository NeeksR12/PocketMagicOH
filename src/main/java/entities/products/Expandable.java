package entities.products;

import entities.products.items.Item;

import java.util.Map;

/**
 * Expandable
 * Description: An object is expandable if it holds products and needs to be able to flatten those products into items
 * Name: Nico Rotella
 * Date Created: August 5th, 2026
 * Last Edited: August 5th, 2026
 */

// Interface
public interface Expandable {

    Map<Item, Integer> toItems();

}
