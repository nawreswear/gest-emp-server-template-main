package tn.iset.m2glnt.server.model;

import java.util.Optional;
import java.util.List;

/**
 * The Data Access Object interface
 * @param <K> the type of the key used for indexation
 * @param <T> the type of the data object
 */
public interface DAO<K, T> {

    /**
     * Get a given element
     * @param key the indexation key
     * @return the element if found
     */
    Optional<T> get(K key);

    /**
     * Get all the element
     * @return the list of elements
     */
    List<T> getAll();

    /**
     * Create a new entry with a given element
     * @param element the new element
     * @return the identifier of the new element
     */
    K create (T element);

    /**
     * Update a given element
     * @param element the new version of the element
     * @return the new version number
     */
    int update (T element);

    /**
     * Remove an element
     * @param element the element to remove
     */
    void delete (T element);

}
