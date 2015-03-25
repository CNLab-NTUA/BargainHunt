package gr.ntua.cn.zannis.bargains.client.impl;

import gr.ntua.cn.zannis.bargains.client.dto.RestResponse;
import gr.ntua.cn.zannis.bargains.client.dto.impl.*;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.*;

/**
 * Base class implementing useful methods for actual RestClient. SkroutzRestClient is supposed
 * to extend this.
 * @author zannis <zannis.kal@gmail.com>
 */
@SuppressWarnings("unused")
public abstract class RestClientImpl {

    protected static final Logger log = LoggerFactory.getLogger(SkroutzRestClient.class);

    protected final String target;
    protected final String token;
    protected final ClientConfig config = new ClientConfig();
    protected int remainingRequests;

    public RestClientImpl(String targetURi, String token) {
        this.target = targetURi;
        this.token = token;
    }

    /**
     * Method to get the next page in a multi-page result scenario.
     * @param tClass The target class type.
     * @param page The previous {@link Page<T>}
     * @param <T> The class entity that extends {@link SkroutzEntity}
     * @return The next page if it exists
     */
    protected <T extends SkroutzEntity> Page<T> nextPage(Class<T> tClass, Page<T> page) {
        if (page.hasNext()) {
            URI nextUri;
            // could be the last page
            nextUri = page.getNext() != null ? page.getNext().getUri() : page.getLast().getUri();
            Class<? extends RestResponse<T>> responseClass = getMatchingResponse(tClass);
            Response response = sendUnconditionalGetRequest(nextUri);
            return getFirstPage(responseClass, response);
        } else {
            return null;
        }
    }

    /**
     * Creates an unconditional GET HTTP request to the Skroutz API. All unconditional
     * public GET methods should use this since its preconfigured using our custom
     * configuration. This method follows redirects, accepts JSON entities and contains
     * the required authorization headers.
     * @param requestUri The target URI.
     * @return A {@link javax.ws.rs.core.Response} containing one or more entities.
     */
    private Response sendUnconditionalGetRequest(URI requestUri) {
        return ClientBuilder.newClient(config).target(requestUri)
                .property(ClientProperties.FOLLOW_REDIRECTS, true)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + token)
                .accept("application/vnd.skroutz+json; version=3")
                .get();
    }

    /**
     * Creates a conditional GET HTTP request to the Skroutz API. All conditional
     * public GET methods should use this since its preconfigured using our custom
     * configuration. This method follows redirects, accepts JSON entities and contains
     * the required authorization headers. It uses an If-None-Match condition
     * with the provided Etag argument.
     * @param requestUri The target URI.
     * @return A {@link javax.ws.rs.core.Response} containing one or more entities.
     */
    private Response sendConditionalGetRequest(URI requestUri, String eTag) {
        return ClientBuilder.newClient(config).target(requestUri)
                .property(ClientProperties.FOLLOW_REDIRECTS, true)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + token)
                .header("If-None-Match", eTag)
                .accept("application/vnd.skroutz+json; version=3")
                .get();
    }

    /**
     * Helper function to extract Link headers from a HTTP response.
     * @param response The response to extract headers from.
     * @return A map of Link urls, empty if no {@link Link} is found.
     */
    private Map<String, Link> getLinks(Response response) {
        Map<String, Link> map = new HashMap<>();
        if (response.hasLink("next")) {
            map.put("next", response.getLink("next"));
        }
        if (response.hasLink("prev")) {
            map.put("prev", response.getLink("prev"));
        }
        if (response.hasLink("last")) {
            map.put("last", response.getLink("last"));
        }
        return map;
    }

    /**
     * Method to get all entities from a given {@link T} entity.
     * @param tClass The class type to retrieve.
     * @param <T> A class type that represents a {@link SkroutzEntity}
     * @return The first page of the list.
     */
    protected <T extends SkroutzEntity> Page<T> getAll(Class<T> tClass) {
        URI uri = getMatchingUri(tClass, null);
        Class<? extends RestResponse<T>> responseClass = getMatchingResponse(tClass);
        Response response = sendUnconditionalGetRequest(uri);
        return getFirstPage(responseClass, response);
    }

    /**
     * Helper method to wrap a result as a {@link Page}
     * @param responseClass The {@link RestResponseImpl<T>} class used to deserialize the response.
     * @param response The response we got from the server.
     * @param <T> A class type that extends {@link SkroutzEntity}
     * @return The first {@link Page<T>} from the result list.
     */
    private <T extends SkroutzEntity> Page<T> getFirstPage(Class<? extends RestResponse<T>> responseClass, Response response) {
        // check response status first
        if (response.getStatus() != 200) {
            log.error(response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            // parse useful headers
            Map<String, Link> links = getLinks(response);
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                Page<T> page = response.readEntity(responseClass).getPage();
                page.setPrev(links.get("prev"));
                page.setNext(links.get("next"));
                page.setLast(links.get("last"));
                for (T item : page.getItems()) {
                    item.setInsertedAt(new Date());
                }
                return page;
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    /**
     * Method used to fetch a {@link T} object from the server using its id.
     * @param tClass The class type to fetch.
     * @param id The object's id on the server.
     * @param <T> A class type that extends {@link SkroutzEntity}
     * @return A {@link T} object or null if it wasn't found.
     */
    protected <T extends SkroutzEntity> T getById(Class<T> tClass, Integer id) {
        URI uri = getMatchingUri(tClass, ID, id);
        Class<? extends RestResponse<T>> responseClass = getMatchingResponse(tClass);
        if (uri != null && responseClass != null) {
            Response response = sendUnconditionalGetRequest(uri);
            return getEntity(responseClass, response);
        } else {
            return null;
        }
    }

    /**
     * Method to fetch paginated results that use query parameters.
     * @param <T> The class that extends {@link SkroutzEntity}
     * @return A {@link Page<T>} object.
     */
    protected <T extends SkroutzEntity> Page<T> getPageByCustomUri(Class<T> tClass, URI uri) {
        Class<? extends RestResponse<T>> responseClass = getMatchingResponse(tClass);
        if (uri != null && responseClass != null) {
            Response response = sendUnconditionalGetRequest(uri);
            return getFirstPage(responseClass, response);
        } else {
            return null;
        }
    }

    /**
     * Method to fetch entity results that use query parameters.
     * @param <T> The class that extends {@link SkroutzEntity}
     * @return A {@link T} object.
     */
    protected <T extends SkroutzEntity> T getEntityByCustomUri(Class<T> tClass, URI uri) {
        Class<? extends RestResponse<T>> responseClass = getMatchingResponse(tClass);
        if (uri != null && responseClass != null) {
            Response response = sendUnconditionalGetRequest(uri);
            return getEntity(responseClass, response);
        } else {
            return null;
        }
    }

    /**
     * Method that matches a {@link SkroutzEntity} to its corresponding {@link RestResponseImpl}
     * @param tClass The {@link SkroutzEntity} class type.
     * @param <T>    The actual {@link SkroutzEntity}.
     * @return The matching {@link RestResponseImpl}.
     */
    @SuppressWarnings("unchecked")
    private <T extends SkroutzEntity> Class<? extends RestResponse<T>> getMatchingResponse(Class<T> tClass) {
        if (tClass.equals(Product.class)) {
            return (Class<? extends RestResponse<T>>) ProductResponse.class;
        } else if (tClass.equals(Shop.class)) {
            return (Class<? extends RestResponse<T>>) ShopResponse.class;
        } else if (tClass.equals(Category.class)) {
            return (Class<? extends RestResponse<T>>) CategoryResponse.class;
        } else if (tClass.equals(Sku.class)) {
            return (Class<? extends RestResponse<T>>) SkuResponse.class;
        } else if (tClass.equals(Manufacturer.class)) {
            return (Class<? extends RestResponse<T>>) ManufacturerResponse.class;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends SkroutzEntity> Class<? extends RestResponse<T>> getMatchingResponse(T entity) {
        if (entity instanceof Product) {
            return (Class<? extends RestResponse<T>>) ProductResponse.class;
        } else if (entity instanceof Shop) {
            return (Class<? extends RestResponse<T>>) ShopResponse.class;
        } else if (entity instanceof Category) {
            return (Class<? extends RestResponse<T>>) CategoryResponse.class;
        } else if (entity instanceof Sku) {
            return (Class<? extends RestResponse<T>>) SkuResponse.class;
        } else if (entity instanceof Manufacturer) {
            return (Class<? extends RestResponse<T>>) ManufacturerResponse.class;
        } else {
            return null;
        }
    }

    /**
     * Method that gets a {@link SkroutzEntity} from a received {@link Response}.
     * @param responseClass The {@link RestResponseImpl} class to use for deserialization.
     * @param response The {@link Response} we got from the API.
     * @param <T> The {@link SkroutzEntity} class we want.
     * @return An entity of type {@link T}
     */
    protected <T extends SkroutzEntity> T getEntity(Class<? extends RestResponse<T>> responseClass, Response response) {
        // check response status first
        if (response.getStatus() != 200) {
            log.error(response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            // parse useful headers
            String eTag = response.getHeaderString("ETag");
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                RestResponse<T> wrapper = response.readEntity(responseClass);
                wrapper.getItem().setEtag(eTag);
                wrapper.getItem().setInsertedAt(new Date());
                wrapper.getItem().setCheckedAt(new Date());
                return wrapper.getItem();
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    /**
     * Method used to check for updates to a persistent entity.
     * @param entity The persistent entity.
     * @return The {@link SkroutzEntity} entity
     * with its possibly updated fields or null if there was an error.
     */
    protected <T extends SkroutzEntity> T getByEntity(T entity) {
        URI uri = getMatchingUri(entity.getClass(), ID, entity.getSkroutzId());
        Class<? extends RestResponse<T>> responseClass = getMatchingResponse(entity);
        if (uri != null && responseClass != null) {
            Response response = sendConditionalGetRequest(uri, entity.getEtag());
            return getEntity(responseClass, response);
        } else {
            return null;
        }
    }

    /**
     * Method that generates a {@link URI} for a specific request.
     *
     * @param tClass   The persistent entity class type to use as path.
     * @param template The query template.
     * @param values   The values to use to build the query.
     * @param <T>      A class type extending {@link SkroutzEntity}
     * @return A URI matching the above input.
     */
    private <T extends SkroutzEntity> URI getMatchingUri(Class<T> tClass, String template, Object... values) {
        UriBuilder builder = UriBuilder.fromPath(target);
        if (tClass.equals(Product.class)) {
            builder.path(PRODUCTS);
        } else if (tClass.equals(Shop.class)) {
            builder.path(SHOPS);
        } else if (tClass.equals(Category.class)) {
            builder.path(CATEGORIES);
        } else if (tClass.equals(Sku.class)) {
            builder.path(SKUS);
        } else if (tClass.equals(Manufacturer.class)) {
            builder.path(MANUFACTURERS);
        } else {
            return null;
        }
        return builder.path(template).build(values);
    }

    /**
     * Method that initializes our custom client configuration to
     * deserialize wrapped objects from JSON.
     */
    protected abstract void initClientConfig();
}
