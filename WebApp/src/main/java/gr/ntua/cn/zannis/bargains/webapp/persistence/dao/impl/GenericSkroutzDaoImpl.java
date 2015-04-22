package gr.ntua.cn.zannis.bargains.webapp.persistence.dao.impl;

import gr.ntua.cn.zannis.bargains.webapp.persistence.dao.GenericSkroutzDao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Implementation of GenericSkroutzDao interface.
 * @author zannis <zannis.kal@gmail.com>
 */
public class GenericSkroutzDaoImpl<T> extends gr.ntua.cn.zannis.bargains.webapp.persistence.dao.impl.GenericDaoImpl<T> implements GenericSkroutzDao<T> {

    public GenericSkroutzDaoImpl(Class<T> type) {
        super(type);
    }

    @Override
    public T findBySkroutzId(long id) {
        T entity = null;
        try {
            em.getTransaction().begin();
            TypedQuery<T> q = em.createNamedQuery(entityClass.getSimpleName() + ".findBySkroutzId", entityClass);
            q.setParameter("skroutzId", id);
            entity = q.getSingleResult();
        } catch (NoResultException e) {
            //swallow and return null
            log.warn("Δε βρέθηκε κανένα αποτέλεσμα.");
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        return entity;
    }

    @Override
    public List<Long> findAllSkroutzIds() {
        TypedQuery<Long> query = em.createNamedQuery(entityClass.getSimpleName() + "findAllSkroutzIds", Long.class);
        return query.getResultList();
    }
}
