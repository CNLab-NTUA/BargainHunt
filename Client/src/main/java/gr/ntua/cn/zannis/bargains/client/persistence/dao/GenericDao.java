package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Generic Data Access Object interface
 * @author zannis <zannis.kal@gmail.com>
 */
public interface GenericDao <T, PK extends Serializable> {

    /** Persist the newInstance object into database */
    T persist(T newInstance);

    /** Retrieve an object that was previously persisted to the database using
     *   the indicated id as primary key
     */
    T find(PK id);

    /**
     * Retrieve all the objects from the given type.
     * @return A list containing all the results.
     */
    List<T> findAll();

    /** Save changes made to a persistent object.  */
    T update(T transientObject);

    /** Remove an object from persistent storage in the database */
    void remove(T persistentObject);
}