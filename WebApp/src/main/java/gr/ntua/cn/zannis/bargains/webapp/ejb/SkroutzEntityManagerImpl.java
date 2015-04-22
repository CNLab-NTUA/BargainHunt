package gr.ntua.cn.zannis.bargains.webapp.ejb;

import gr.ntua.cn.zannis.bargains.webapp.components.Notifier;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@Stateless
@LocalBean
public class SkroutzEntityManagerImpl implements SkroutzEntityManager {

    private static final Logger log = LoggerFactory.getLogger(SkroutzEntityManagerImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public <T extends SkroutzEntity> void persist(T object) throws RuntimeException {
        try {
            em.persist(object);
            log.info("Object " + object.toString() + " persisted successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο persist στο " + object.toString(), e);
        }
    }

    @Override
    public void persistChild(Object child, Object parent) {}

    @Override
    public <T extends SkroutzEntity> T merge(T object) throws RuntimeException {
        try {
            em.merge(object);
            log.info("Object " + object.toString() + " merged successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο merge στο " + object.toString(), e);
        }
        return object;
    }

    @Override
    public <T extends SkroutzEntity> void remove(T object) throws RuntimeException {
        try {
            em.remove(object);
            log.info("Object " + object + " removed successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο remove στο " + object.toString(), e);
        }
    }

    @Override
    public void removeChild(Object child, Object parent) {}

    @Override
    public <T extends SkroutzEntity> T find(Class<T> tClass, Object skroutzId) throws RuntimeException {
        T result;
        // finds an object by its skroutzId
        TypedQuery<T> q = em.createNamedQuery(tClass.getSimpleName() + ".findBySkroutzId", tClass);
        q.setParameter("skroutzId", skroutzId);
        try {
            result = q.getSingleResult();
        } catch (NoResultException e) {
            // swallow and return null
            result = null;
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο find" + tClass.getSimpleName() + " με skroutzId " + skroutzId, e);
            result = null;
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity> List<T> findAll(Class<T> tClass) {
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

    @Override
    public <T extends SkroutzEntity> TypedQuery<T> createNamedQuery(String namedQuery, Class<T> tClass) {
        return em.createNamedQuery(namedQuery, tClass);
    }
}
