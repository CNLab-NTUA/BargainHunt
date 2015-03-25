package gr.ntua.cn.zannis.bargains.client;

import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;

/**
 * The RESTful Client interface that targets an API that can provide
 * products, shops, categories, skus and their metadata.
 * @author zannis <zannis.kal@gmail.com
 */
public interface RestClient {

    <T extends SkroutzEntity> Page<T> nextPage(Page<T> page);

    <T extends SkroutzEntity> Page<T> getAll(Class<T> tClass);

    <T extends SkroutzEntity> Page<T> searchByName(Class<T> tClass, String query);

    <T extends SkroutzEntity> T getById(Class<T> tClass, Integer id);

    <T extends SkroutzEntity> T getByEntity(Class<T> tClass, T entity);

}
