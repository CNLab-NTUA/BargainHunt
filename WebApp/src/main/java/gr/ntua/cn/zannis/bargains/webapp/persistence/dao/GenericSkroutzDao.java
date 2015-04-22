package gr.ntua.cn.zannis.bargains.webapp.persistence.dao;

import java.util.List;

/**
 * An interface made specifically for managing Skroutz entities. That is, entities
 * that contain a <code>skroutzId</code>.
 * @author zannis <zannis.kal@gmail.com>
 */
public interface GenericSkroutzDao<T> extends gr.ntua.cn.zannis.bargains.webapp.persistence.dao.GenericDao<T> {

    /** Retrieve a persistent object from the database using its skroutzId */
    T findBySkroutzId(long id);

    /**
     * Retrieve all the skroutzIds from a given entity to quickly check
     * if they exist in the database.
     *
     * @return A list containing all the skroutzIds for the given entity.
     */
    List<Long> findAllSkroutzIds();
}
