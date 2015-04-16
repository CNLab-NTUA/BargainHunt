package gr.ntua.cn.zannis.bargains.client.impl;

import gr.ntua.cn.zannis.bargains.client.RestClient;
import gr.ntua.cn.zannis.bargains.client.exceptions.NoEntityFoundException;
import gr.ntua.cn.zannis.bargains.client.exceptions.UnexpectedResponseStatusException;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.SEARCH;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.OK;

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
    protected int remainingRequests;

    public RestClientImpl(String targetUri, String token) {
        this.target = targetUri;
        this.token = token;
    }

    @Override
    public <T extends SkroutzEntity> T get(Class<T> tClass, Long skroutzId) {
        T entity = null;
        URI uri = Utils.getMatchingUri(tClass, skroutzId);
        try {
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
            Response response = sendGetRequest(uri);
            entity = getEntity(responseClass, response);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return entity;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass) {
        Page<T> result = null;
        URI uri = Utils.getMatchingUri(tClass);
        try {
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
            Response response = sendGetRequest(uri);
            result = extractPage(response, responseClass);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass, Filter... filters) {
        Page<T> result = null;
        URI uri = Utils.getMatchingUri(tClass, filters);
        try {
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
            Response response = sendGetRequest(uri);
            result = extractPage(response, responseClass);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity> T get(T entity) {
        T result = null;
        URI uri = Utils.getMatchingUri(entity);
        try {
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(entity);
            Response response = sendGetRequest(uri, entity.getEtag());
            result = getEntity(responseClass, response);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
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
                Response response = sendGetRequest(nextUri);
                result = extractPage(response, responseClass);
            }
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + nextUri, e);
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass) {
        Page<T> result = null;
        URI uri = Utils.getMatchingUri(parentEntity, childClass);
        try {
            Response response = sendGetRequest(uri);
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(childClass);
            result = extractPage(response, responseClass);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, Filter... filters) {
        Page<T> result = null;
        URI uri = Utils.getMatchingUri(parentEntity, childClass, filters);
        try {
            Response response = sendGetRequest(uri);
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(childClass);
            result = extractPage(response, responseClass);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAsList(Class<T> tClass) {
        return getAllResultsAsList(get(tClass));
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAsList(Class<T> tClass, Filter... filters) {
        return getAllResultsAsList(get(tClass, filters));
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getNestedAsList(U parentEntity, Class<T> childClass) {
        Page<T> firstPage = getNested(parentEntity, childClass);
        return getAllResultsAsList(firstPage);
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getNestedAsList(U parentEntity, Class<T> childClass, Filter... filters) {
        Page<T> firstPage = getNested(parentEntity, childClass, filters);
        return getAllResultsAsList(firstPage);
    }

    @Override
    public SearchResults search(String query) {
        SearchResults results = null;
        URI uri = UriBuilder.fromPath(target).path(SEARCH).queryParam("q", query.trim()).build();
        try {
            Response response = sendGetRequest(uri);
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
     * Method that gets a {@link SkroutzEntity} from a received {@link Response}.
     *
     * @param responseClass The {@link RestResponseImpl} class to use for deserialization.
     * @param response      The {@link Response} we got from the API.
     * @param <T>           The {@link SkroutzEntity} class we want.
     * @return An entity of type {@link T}
     */
    private <T extends SkroutzEntity> T getEntity(Class<? extends RestResponse<T>> responseClass, Response response) throws RuntimeException {
        // check response status first
        int status = response.getStatus();
        if (status == NOT_MODIFIED.getStatusCode()) {
            // todo get etag, check with db etag, ...
            return null;
        } else if (status == OK.getStatusCode()) {
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
                throw new NoEntityFoundException();
            }
        } else {
            log.error(response.getStatusInfo().getReasonPhrase());
            throw new UnexpectedResponseStatusException(response.getStatusInfo());
        }
    }

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
        int status = response.getStatus();
        if (status == NOT_MODIFIED.getStatusCode()) {
            // todo get etag, check with db etag, ...
            return null;
        } else if (status == OK.getStatusCode()) {
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
        } else {
            log.error(response.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    /**
     * Method to retrieve all results from a paginated response.
     *
     * @param resultPage The
     *                   first page of the results
     * @param <T>        The {@link SkroutzEntity} type.
     * @return A list containing all the results that the web service returned or an empty list.
     */
    public <T extends SkroutzEntity> List<T> getAllResultsAsList(Page<T> resultPage) {
        if (!resultPage.getItems().isEmpty()) {
            List<T> results = new LinkedList<>(resultPage.getItems());
            if (resultPage.hasNext()) {
                do {
                    Page<T> nextPage = getNextPage(resultPage);
                    results.addAll(nextPage.getItems());
                    resultPage = nextPage;
                } while (resultPage.hasNext());
            }
            return results;
        } else {
            return new ArrayList<>();
        }
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
    private Response sendGetRequest(URI requestUri) throws ProcessingException {
        Response response;
        try {
            response = ClientBuilder.newClient(config).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
    private Response sendGetRequest(URI requestUri, String eTag) throws ProcessingException {
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
