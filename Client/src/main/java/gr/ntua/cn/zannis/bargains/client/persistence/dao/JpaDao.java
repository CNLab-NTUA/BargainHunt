package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class GenericDaoHibernateImpl <T, PK extends Serializable>
        implements GenericDao<T, PK>, FinderExecutor {


    public PK create(T o) {
        return (PK) getSession().save(o);
    }

    public T read(PK id) {
        return (T) getSession().get(type, id);
    }

    public void update(T o) {
        getSession().update(o);
    }

    public void delete(T o) {
        getSession().delete(o);
    }


public class JpaDao<T, PK extends Serializable> implements GenericDao<T, PK> {

}
    protected Class entityClass;

    @PersistenceContext
    EntityManager em;

    public JpaDao(Class<T> type) {
        this.entityClass = type;
    }

    @Override
    public PK persist(T entity) {
        try {
            em.persist(entity);
        }
    }

    @Override
    public void remove(E entity) {
        em.remove(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E findById(int id) {
        return (E) em.find(entityClass, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> findAll() {
        TypedQuery<E> query = em.createNamedQuery("findAll", entityClass);
        return query.getResultList();
    }
}
