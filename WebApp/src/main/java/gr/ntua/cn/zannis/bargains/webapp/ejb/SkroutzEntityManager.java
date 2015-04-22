package gr.ntua.cn.zannis.bargains.webapp.ejb;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public interface SkroutzEntityManager {

    <T extends SkroutzEntity> void persist(T object) throws RuntimeException;

    void persistChild(Object child, Object parent);

    <T extends SkroutzEntity> T merge(T object) throws RuntimeException;

    <T extends SkroutzEntity> void remove(T object) throws RuntimeException;

    void removeChild(Object child, Object parent);

    <T extends SkroutzEntity> T find(Class<T> tClass, Object id) throws RuntimeException;

    <T extends SkroutzEntity> List<T> findAll(Class<T> tClass);

    <T extends SkroutzEntity> TypedQuery<T> createNamedQuery(String namedQuery, Class<T> tClass);
}
