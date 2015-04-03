package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import java.util.List;

/**
 * Generic Data Access Object interface
 * @author zannis <zannis.kal@gmail.com>
 */
public interface GenericDao <T> {

    /** Persist a transient entity object into database */
    T persist(T transientEntity);

    /** Retrieve a persistent object from the database using its id */
    T find(long id);

    /** Retrieve a persistent object from the database using its skroutzId */
    T findBySkroutzId(long id);

    /**
     * Retrieve all the objects from the given type.
     * @return A list containing all the results.
     */
    List<T> findAll();

    /** Save changes made to a persistent object.  */
    T update(T transientEntity);

    /** Remove an object from persistent storage in the database */
    void remove(T persistentEntity);
}