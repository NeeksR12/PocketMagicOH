package entities;

/**
 * Persistable
 * Description: An object is persistable if it persists through running the software, therefore must be saved into the DB
 * Name: Nico Rotella
 * Date Created: August 25th, 2026
 * Last Edited: August 25th, 2026
 */

// Interface
public interface Persistable {

    String getName();
    Integer getId();
    void setId(Integer id);

}
