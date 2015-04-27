package gr.ntua.cn.zannis.bargains.webapp.ejb.impl;

import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@Stateless
@LocalBean
public class CustomEntityManager {

    public static final Logger log = LoggerFactory.getLogger(CustomEntityManager.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    public <T> void persist(T object) throws RuntimeException {
        try {
            em.persist(object);
            log.info("Object " + object.toString() + " persisted successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο persist στο " + object.toString(), e);
        }
    }

    public <T> T merge(T object) throws RuntimeException {
        try {
            em.merge(object);
            log.info("Object " + object.toString() + " merged successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο merge στο " + object.toString(), e);
        }
        return object;
    }

    public <T> void remove(T object) throws RuntimeException {
        try {
            em.remove(object);
            log.info("Object " + object + " removed successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο remove στο " + object.toString(), e);
        }
    }

    public <T> T find(Class<T> tClass, Object id) throws RuntimeException {
        T result;
        try {
            result = em.find(tClass, id);
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο find" + tClass.getSimpleName() + " με skroutzId " + id, e);
            result = null;
        }
        return result;
    }

    public <T> List<T> findAll(Class<T> tClass) throws RuntimeException {
        List<T> result;
        TypedQuery<T> q = createNamedQuery(tClass.getSimpleName() + ".findAll", tClass);
        try {
            result = q.getResultList();
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο findAll" + tClass.getSimpleName(), e);
            result = new ArrayList<>();
        }
        return result;
    }

    public <T> TypedQuery<T> createNamedQuery(String namedQuery, Class<T> tClass) {
        return em.createNamedQuery(namedQuery, tClass);
    }
}
