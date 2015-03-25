package gr.ntua.cn.zannis.bargains.client;

import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.persistence.PersistentEntity;

/**
 * The RESTful Client interface that targets an API that can provide
 * products, shops, categories, skus and their metadata.
 * @author zannis <zannis.kal@gmail.com
 */
public interface RestClient {

    <T extends PersistentEntity> Page<T> nextPage(Page<T> page);

    <T extends PersistentEntity> Page<T> getAll(Class<T> tClass);

    <T extends PersistentEntity> Page<T> searchByName(Class<T> tClass, String query);

    <T extends PersistentEntity> T getById(Class<T> tClass, Integer id);

    <T extends PersistentEntity> T getByEntity(Class<T> tClass, T entity);

}
