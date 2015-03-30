package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import java.util.List;

/**
 * Generic Data Access Object interface
 * @author zannis <zannis.kal@gmail.com>
 */
public interface Dao {
    void persist(E entity);
    void remove(E entity);
    E findById(K id);
    List<E> findAll();
}
