package gr.ntua.cn.zannis.bargains.webapp.rest;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.*;
import gr.ntua.cn.zannis.bargains.webapp.rest.requests.filters.Filter;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * The RESTful Client interface that targets an API that can provide
 * products, shops, categories, skus and their metadata.
 * @author zannis <zannis.kal@gmail.com>
 */
public interface RestClient {

    /**
     * Generic method to retrieve a single result from the given category.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param skroutzId The <code>skroutzId</code> of the item we are looking for.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @return An object of type T.
     */
    <T extends SkroutzEntity> T get(Class<T> tClass, Integer skroutzId) throws RuntimeException;

    /**
     * Generic method to retrieve a single result from the given category.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param skroutzId The <code>skroutzId</code> of the item we are looking for.
     * @param etag The object's entity tag.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @return An object of type T.
     */
    <T extends SkroutzEntity> T get(Class<T> tClass, Integer skroutzId, String etag) throws RuntimeException;

    /**
     * Generic method to retrieve all results from the given type.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param <T> A type between {@link Category}, {@link Manufacturer}
     * @return The first page of the results.
     */
    <T extends SkroutzEntity> Page<T> get(Class<T> tClass);

    /**
     * Generic method to retrieve all results from the given type.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param etag The object's entity tag.
     * @param <T> A type between {@link Category}, {@link Manufacturer}
     * @return The first page of the results.
     */
    <T extends SkroutzEntity> Page<T> get(Class<T> tClass, String etag);

    /**
     * Generic method to retrieve all results from the given type using the specified filters.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param filters A list of filters to apply to the query.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @return The first page of the results.
     */
    <T extends SkroutzEntity> Page<T> get(Class<T> tClass, Filter... filters);

    /**
     * Generic method to retrieve all results from the given type using the specified filters.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param etag The object's entity tag.
     * @param filters A list of filters to apply to the query.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @return The first page of the results.
     */
    <T extends SkroutzEntity> Page<T> get(Class<T> tClass, String etag, Filter... filters);

    /**
     * Generic method to retrieve the given entity from the category <code>T</code>.
     * @param entity The entity to retrieve.
     * @param <T> The entity's class type.
     * @return The updated entity or the original one if there was no update.
     */
    <T extends SkroutzEntity> T get(T entity);

    /**
     * Generic method to retrieve nested results from the given type {@link U} which belongs to the type {@link T}.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param <U> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param parentEntity A different {@link SkroutzEntity} object which owns <code>childClass</code>.
     * @param childClass A {@link SkroutzEntity} class type.
     * @return The first page of the results.
     */
    <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass);

    /**
     * Generic method to retrieve nested results from the given type {@link U} which belongs to the type {@link T}.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param <U> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param parentEntity A different {@link SkroutzEntity} object which owns <code>childClass</code>.
     * @param childClass A {@link SkroutzEntity} class type.
     * @param etag The object's entity tag.
     * @return The first page of the results.
     */
    <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, String etag);

    /**
     * Generic method to retrieve nested results from the given type {@link U} which belongs to the type {@link T} filtered
     * by the specified filters.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param <U> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param parentEntity A different {@link SkroutzEntity} object which owns <code>childClass</code>.
     * @param childClass A {@link SkroutzEntity} class type.
     * @param filters A list of filters to apply to the query.
     * @return The first page of the results.
     */
    <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, Filter... filters);

    /**
     * Generic method to retrieve nested results from the given type {@link U} which belongs to the type {@link T} filtered
     * by the specified filters.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param <U> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param parentEntity A different {@link SkroutzEntity} object which owns <code>childClass</code>.
     * @param childClass A {@link SkroutzEntity} class type.
     * @param etag The object's entity tag.
     * @param filters A list of filters to apply to the query.
     * @return The first page of the results.
     */
    <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, String etag, Filter... filters);

    /**
     * Method to retrieve all results from the given type in a list. It internally bypasses pagination, as it requests
     * all the pages one by one and returns the result.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @return A {@link List<T>} containing all the result set.
     */
    <T extends SkroutzEntity> List<T> getAll(Class<T> tClass);

    /**
     * Method to retrieve all results from the given type in a list filtered by an array of filters.
     * It internally bypasses pagination, as it requests all the pages one by one and returns the result.
     * @param tClass A {@link SkroutzEntity} class type.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @return A {@link List<T>} containing all the result set or an empty list if nothing was found.
     */
    <T extends SkroutzEntity> List<T> getAll(Class<T> tClass, Filter... filters);

    /**
     * Method to execute a nested get query and return all the results (bypassing pagination) as a list.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param <U> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param parentEntity A different {@link SkroutzEntity} object which owns <code>childClass</code>.
     * @param childClass A {@link SkroutzEntity} class type.
     * @return A list containing the results.
     */
    <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getAllNested(U parentEntity, Class<T> childClass);

    /**
     * Method to execute a nested get query using the specified filters and return all the results (bypassing pagination) as a list.
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param <U> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @param parentEntity A different {@link SkroutzEntity} class type which owns <code>childEntity</code>.
     * @param childClass A {@link SkroutzEntity} class type.
     * @param filters A list of filters to apply to the query.
     * @return A list containing the results.
     */
    <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getAllNested(U parentEntity, Class<T> childClass, Filter... filters);

    /**
     * Method to retrieve the next page from a paginated result.
     * @param page The previous {@link Page<T>}
     * @param <T> A type between {@link Category}, {@link Manufacturer}, {@link Sku}, {@link Product}, {@link Shop}
     * @return The next {@link Page<T>}
     */
    <T extends SkroutzEntity> Page<T> getNextPage(Page<T> page);

    /**
     * Search request using a string query. It typically returns a list of categories that contain
     * SKUs with the given string and maybe  a list of {@link gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Meta.Alternative}
     * or {@link gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Meta.StrongMatches}.
     * @param query The string we are looking for.
     * @return A {@link SearchResults} object. Practically a {@link Page<Category>} with some extra features.
     */
    SearchResults search(String query) throws UnsupportedEncodingException;

}
