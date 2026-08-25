package databases;

import entities.Persistable;
import java.util.*;

/**
 * TrackedCollection
 * Description: A collection of a persistable objects that must be tracked throughout software runs
 * Name: Nico Rotella
 * Date Created: August 25th, 2026
 * Last Edited: August 25th, 2026
 * @param <T> The persistable being tracked
 */
public class TrackedCollection<T extends Persistable> {

    // Attributes
    private final Map<String, T> persistables = new LinkedHashMap<>(); // name -> persistable
    private final Set<T> dirty = new LinkedHashSet<>();
    private final Set<T> deleted = new LinkedHashSet<>();

    /**
     * Description: Checks if a persistable is in the collection and returns a boolean
     * Pre-Condition: This is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the persistable
     * @return The boolean if found
     */
    public boolean has(String name) {
        return persistables.containsKey(name);
    }

    /**
     * Description: Looks for a persistable in the collection by its name
     * Pre-Condition: Collection is initialized
     * Post-Condition: Persistable is returned or exception is thrown
     * @param name The name of the persistable being searched for
     * @return The persistable if found
     * @throws NoSuchElementException if not found
     */
    public T getByName(String name) throws NoSuchElementException {
        T t = persistables.get(name);

        if (t == null)
            throw new NoSuchElementException("Error, not found: " + name); // Not found
        else
            return t; // Found
    }

    /**
     * Description: Adds a persistable to collection
     * Pre-Condition: Collection and persistable are initialized and persistable is not already in the collection
     * Post-Condition: Persistable is added or exception is thrown
     * @param persistable The persistable
     * @throws IllegalArgumentException if the card is already in the collection
     */
    public void add(T persistable) throws IllegalArgumentException {
        // Checking if the persistable is already in the collection
        if (has(persistable.getName()))
            throw new IllegalArgumentException("Error, already exists: " + persistable.getName()); // it was

        // It needs to be added
        persistables.put(persistable.getName(), persistable);
        markDirty(persistable);
    }

    /**
     * Description: Takes a persistable and marks it as dirty for the DB to worry about
     * Pre-Condition: Should only mark a persistable dirty if being created or updated
     * Post_Condition: The persistable is marked dirty
     * @param persistable The persistable
     */
    public void markDirty(T persistable) {
        dirty.add(persistable);
    }

    /**
     * Description: Takes a persistable and marks it as a deleted persistable for the DB to worry about
     * Pre-Condition: Param is a persistable
     * Post-Condition: The persistable is ready to be deleted from the DB and is removed from inv
     * @param persistable The persistable
     */
    public void markDeleted(T persistable) {
        dirty.remove(persistable);
        if (persistable.getId() != null) { // Checking if the persistable has been in the DB before
            deleted.add(persistable); // It has, .'. needs to be deleted
        }
        persistables.remove(persistable.getName());
    }

    /**
     * Description: Clears the dirty and deleted sets
     * Pre-Condition: None
     * Post-Condition: Dirty and deleted sets are cleared
     */
    public void clearDirtyTracking() {
        dirty.clear();
        deleted.clear();
    }

    /**
     * Description: Gives the values of the tracked persistables in the map
     * Pre-Condition: None
     * Post-Condition: The values being tracked are returned
     * @return The values being tracked
     */
    public Collection<T> values() {
        return persistables.values();
    }

    /**
     * Description: Checks if there are any persistables being tracked
     * Pre-Condition: None
     * Post-Condition: A boolean is returned true if there are persistables being tracked anf false if not
     * @return The boolean if there are persistables being tracked
     */
    public boolean isEmpty() {
        return persistables.isEmpty();
    }

    // Getters
    public Set<T> getDirty() { 
        return dirty; 
    }
    
    public Set<T> getDeleted() { 
        return deleted; 
    }
    
}
