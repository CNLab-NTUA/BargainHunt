package gr.ntua.cn.zannis.bargains.client.impl;

import gr.ntua.cn.zannis.bargains.client.RestClient;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.*;
import gr.ntua.cn.zannis.bargains.client.requests.filters.Filter;
import gr.ntua.cn.zannis.bargains.client.responses.RestResponse;
import gr.ntua.cn.zannis.bargains.client.responses.impl.RestResponseImpl;
import gr.ntua.cn.zannis.bargains.client.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Page;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.*;

/**
 * Base class implementing useful methods for actual RestClient. SkroutzRestClient is supposed
 * to extend this.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public abstract class RestClientImpl implements RestClient {

    protected static final Logger log = LoggerFactory.getLogger(SkroutzRestClient.class);

    protected final String target;
    protected final String token;
    protected final ClientConfig config = new ClientConfig();
    private final HashMap<Class, String> pathMap;
    protected int remainingRequests;

    public RestClientImpl(String targetUri, String token) {
        this.target = targetUri;
        this.token = token;
        this.pathMap = Utils.initPathMap();
    }

    @Override
    public <T extends SkroutzEntity> T get(Class<T> tClass, Long skroutzId) {
        T entity = null;
        URI uri = null;
        try {
            uri = getMatchingUri(tClass, ID, skroutzId);
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
            Response response = sendUnconditionalGetRequest(uri);
            entity = getEntity(responseClass, response);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return entity;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass) {
        Page<T> result = null;
        URI uri = null;
        try {
            uri = getMatchingUri(tClass, null);
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
            Response response = sendUnconditionalGetRequest(uri);
            result = extractPage(response, responseClass);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass, Filter... filters) {
        // todo implement this, use getpage by custom uri
        return null;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> getNextPage(Page<T> page) {
        Page<T> result = null;
        URI nextUri = null;
        try {
            if (page.hasNext()) {
                // check if it's the last page
                nextUri = page.getNext() != null ? page.getNext() : page.getLast();
                Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(page.getEntityType());
                Response response = sendUnconditionalGetRequest(nextUri);
                result = extractPage(response, responseClass);
            }
        } catch (ProcessingException e) {
            log.error("������ �������� ���� ��� �������� ��� GET request : " + nextUri, e);
        }
        return result;
    }

    /**
     * Method to fetch paginated results that use query parameters.
     *
     * @param <T> The class that extends {@link SkroutzEntity}
     * @return A {@link Page<T>} object.
     */
    protected <T extends SkroutzEntity> Page<T> getPageByCustomUri(Class<T> tClass, URI uri) {
        Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
        if (uri != null && responseClass != null) {
            Response response = sendUnconditionalGetRequest(uri);
            return extractPage(response, responseClass);
        } else {
            return null;
        }
    }

    /**
     * Method to fetch entity results that use query parameters.
     *
     * @param <T> The class that extends {@link SkroutzEntity}
     * @return A {@link T} object.
     */
    protected <T extends SkroutzEntity> T getEntityByCustomUri(Class<T> tClass, URI uri) {
        Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
        if (uri != null && responseClass != null) {
            Response response = sendUnconditionalGetRequest(uri);
            return getEntity(responseClass, response);
        } else {
            return null;
        }
    }

    /**
     * Method that gets a {@link SkroutzEntity} from a received {@link Response}.
     *
     * @param responseClass The {@link RestResponseImpl} class to use for deserialization.
     * @param response      The {@link Response} we got from the API.
     * @param <T>           The {@link SkroutzEntity} class we want.
     * @return An entity of type {@link T}
     */
    protected <T extends SkroutzEntity> T getEntity(Class<? extends RestResponse<T>> responseClass, Response response) {
        // check response status first
        if (response.getStatus() != 200) {
            log.error(response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            // parse useful headers
            String eTag = StringUtils.substringBetween(response.getHeaderString("ETag"), "\"");
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                RestResponse<T> wrapper = response.readEntity(responseClass);
                wrapper.getItem().setEtag(eTag);
                return wrapper.getItem();
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    /**
     * Method used to check for updates to a persistent entity.
     *
     * @param entity The persistent entity.
     * @return The {@link SkroutzEntity} entity
     * with its possibly updated fields or null if there was an error.
     */
    protected <T extends SkroutzEntity> T getByEntity(T entity) {
        URI uri = getMatchingUri(entity.getClass(), ID, entity.getSkroutzId());
        Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(entity);
        if (uri != null && responseClass != null) {
            Response response = sendConditionalGetRequest(uri, entity.getEtag());
            return getEntity(responseClass, response);
        } else {
            return null;
        }
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(Class<T> childClass, U parentEntity) {
        Page<T> result = null;
        URI uri = null;
        try {
            uri = matchNestedUri(parentEntity.getClass(), parentEntity.getSkroutzId(), childClass, null); //todo
            Response response = sendUnconditionalGetRequest(uri);
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(childClass);
            result = extractPage(response, responseClass);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(Class<T> childClass, U parentEntity, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAsList(Class<T> tClass) {
        return getAllResultsAsList(get(tClass));
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAsList(Class<T> tClass, Filter... filters) {
        return null; //todo
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getNestedAsList(Class<T> childClass, U parentEntity) {
        Page<T> firstPage = getNested(childClass, parentEntity);
        return getAllResultsAsList(firstPage);
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getNestedAsList(Class<T> childClass, U parentEntity, Filter... filters) {
        return null; //todo
    }

    @Override
    public SearchResults search(String query) {
        URI uri = null;
        SearchResults results = null;
        try {
            uri = UriBuilder.fromPath(target).path(SEARCH).queryParam("q", query).build();
            Response response = sendUnconditionalGetRequest(uri);
            Map<String, URI> links = Utils.getLinks(response);
            results = response.readEntity(SearchResults.class);
            results.setLinks(links);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return results;
    }

    /**
     * Method that initializes our custom client configuration to
     * deserialize wrapped objects from JSON.
     */
    protected abstract void initClientConfig();

    /**
     * Helper method to wrap a result as a {@link Page}
     *
     * @param <T>           A class type that extends {@link SkroutzEntity}
     * @param response      The response we got from the server.
     * @param responseClass The {@link RestResponseImpl<T>} class used to deserialize the response.
     * @return The first {@link Page<T>} from the result list.
     */
    private <T extends SkroutzEntity> Page<T> extractPage(Response response, Class<? extends RestResponse<T>> responseClass) {
        // check response status first
        if (response.getStatus() != 200) {
            log.error(response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            // parse useful headers
            Map<String, URI> links = Utils.getLinks(response);
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                Page<T> page = response.readEntity(responseClass).getPage();
                page.setPrev(links.get("prev"));
                page.setNext(links.get("next"));
                page.setLast(links.get("last"));
                return page;
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    private <T extends SkroutzEntity> URI matchNestedUri(Class<? extends SkroutzEntity> parentClass, Long skroutzId, Class<T> childClass, Map<String, String> filters) {
        UriBuilder builder = UriBuilder.fromPath(API_HOST);
        if (parentClass != null && skroutzId != null) {
            builder.path(pathMap.get(parentClass)).path(ID);
        }
        builder.path(pathMap.get(childClass));
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            builder.queryParam(filter.getKey(), filter.getValue());
        }
        return builder.build(skroutzId);
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
    // todo fix this to apply filters
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
        if (template != null) {
            builder.path(template);
        }
        return builder.build(values);
    }

    /**
     * Method to retrieve all results from a paginated response.
     *
     * @param resultPage The
     *                   first page of the results
     * @param <T>        The {@link SkroutzEntity} type.
     * @return A list containing all the results that the web service returned.
     */
    public <T extends SkroutzEntity> List<T> getAllResultsAsList(Page<T> resultPage) {
        List<T> results = new LinkedList<>(resultPage.getItems());
        if (resultPage.hasNext()) {
            do {
                Page<T> nextPage = getNextPage(resultPage);
                results.addAll(nextPage.getItems());
                resultPage = nextPage;
            } while (resultPage.hasNext());
        }
        return results;
    }

    /**
     * Creates an unconditional GET HTTP request to the Skroutz API. All unconditional
     * public GET methods should use this since its preconfigured using our custom
     * configuration. This method follows redirects, accepts JSON entities and contains
     * the required authorization headers.
     *
     * @param requestUri The target URI.
     * @return A {@link javax.ws.rs.core.Response} containing one or more entities or null.
     */
    private Response sendUnconditionalGetRequest(URI requestUri) throws ProcessingException {
        Response response;
        try {
            response = ClientBuilder.newClient(config).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Bearer " + token)
                    .accept("application/vnd.skroutz+json; version=3")
                    .get();
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + requestUri, e);
            throw e;
        }
        return response;
    }

    /**
     * Creates a conditional GET HTTP request to the Skroutz API. All conditional
     * public GET methods should use this since its preconfigured using our custom
     * configuration. This method follows redirects, accepts JSON entities and contains
     * the required authorization headers. It uses an If-None-Match condition
     * with the provided Etag argument.
     *
     * @param requestUri The target URI.
     * @return A {@link javax.ws.rs.core.Response} containing one or more entities.
     */
    private Response sendConditionalGetRequest(URI requestUri, String eTag) throws ProcessingException {
        Response response;
        try {
            response = ClientBuilder.newClient(config).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.IF_NONE_MATCH, eTag)
                    .accept("application/vnd.skroutz+json; version=3")
                    .get();
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + requestUri, e);
            throw e;
        }
        return response;
    }
}
