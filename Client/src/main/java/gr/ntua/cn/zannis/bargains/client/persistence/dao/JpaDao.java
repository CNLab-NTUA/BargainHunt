package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class JpaDao implements Dao {
    protected Class entityClass;

    @PersistenceContext
    EntityManager em;

    public JpaDao() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class) genericSuperclass.getActualTypeArguments()[1];
    }

    @Override
    public void persist(E entity) {
        em.persist(entity);
    }

    @Override
    public void remove(E entity) {
        em.remove(entity);
    }

    @Override
    public E findById(K id) {
        em.find(entityClass, id);
    }

    @Override
    public List findAll() {
        Query query = em.createNamedQuery("findAll");
        return query.getResultList();
    }
}
