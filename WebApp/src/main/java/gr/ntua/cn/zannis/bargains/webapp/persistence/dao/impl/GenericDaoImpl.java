package gr.ntua.cn.zannis.bargains.webapp.persistence.dao.impl;

import gr.ntua.cn.zannis.bargains.webapp.persistence.dao.GenericDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.PERSISTENCE_UNIT;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class GenericDaoImpl<T> implements GenericDao<T> {

    protected static final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    protected static final EntityManager em = emf.createEntityManager();
    protected final Class<T> entityClass;
    protected static final Logger log = LoggerFactory.getLogger(GenericSkroutzDaoImpl.class.getCanonicalName());


    public GenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T persist(T entity) {
        log.info("Persisting entity " + entity);
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            log.error("Exception while persisting entity " + entity, e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        return entity;
    }

    @Override
    public void remove(T entity) {
        log.info("Removing entity " + entity);
        try {
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            log.error("Exception while removing entity " + entity, e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T find(long id) {
        return em.find(entityClass, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        TypedQuery<T> query = em.createNamedQuery(entityClass.getSimpleName() + ".findAll", entityClass);
        return query.getResultList();
    }

    @Override
    public T update(T transientObject) {
        log.info("Updating object " + transientObject);
        T persistentObject = null;
        try {
            em.getTransaction().begin();
            persistentObject = em.merge(transientObject);
            em.getTransaction().commit();
        } catch (Exception e) {
            log.error("Exception while updating " + transientObject);
            em.getTransaction().rollback();
        }
        return persistentObject;
    }
}
