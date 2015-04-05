package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.PERSISTENCE_UNIT;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class GenericDaoImpl<T> implements GenericDao<T> {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    static EntityManager em = emf.createEntityManager();
    protected Class<T> entityClass;
    Logger log = LoggerFactory.getLogger(getClass().getCanonicalName());

    public GenericDaoImpl(Class<T> type) {
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
    public T findBySkroutzId(long id) {
        T entity = null;
        try {
            em.getTransaction().begin();
            TypedQuery<T> q = em.createNamedQuery(entityClass.getSimpleName() + ".findBySkroutzId", entityClass);
            q.setParameter("skroutzId", id);
            entity = q.getSingleResult();
        } catch (NoResultException e) {
            //swallow and return null
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        TypedQuery<T> query = em.createNamedQuery(entityClass.getSimpleName() + ".findAll", entityClass);
        return query.getResultList();
    }

    @Override
    public List<Long> findAllSkroutzIds() {
        TypedQuery<Long> query = em.createNamedQuery(entityClass.getSimpleName() + "findAllSkroutzIds", Long.class);
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
