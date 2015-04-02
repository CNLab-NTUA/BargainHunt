package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.PERSISTENCE_UNIT;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class JpaDao<T, PK extends Serializable> implements GenericDao<T, PK> {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    static EntityManager em = emf.createEntityManager();
    protected Class entityClass;
    Logger log = LoggerFactory.getLogger(getClass().getCanonicalName());

    public JpaDao(Class<T> type) {
        this.entityClass = type;
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
            em.getTransaction().rollback();
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
            em.getTransaction().rollback();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T find(PK id) {
        return (T) em.find(entityClass, id);
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
