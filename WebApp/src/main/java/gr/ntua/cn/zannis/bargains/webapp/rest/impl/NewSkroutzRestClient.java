package gr.ntua.cn.zannis.bargains.webapp.rest.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.vaadin.ui.UI;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Request;
import gr.ntua.cn.zannis.bargains.webapp.rest.RestClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.misc.Utils;
import gr.ntua.cn.zannis.bargains.webapp.rest.requests.filters.Filter;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.RestResponse;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.exceptions.NotModifiedException;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.exceptions.ResponseWithoutEntityException;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.exceptions.UnknownResponseStatusException;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class NewSkroutzRestClient implements RestClient {

    private static final Logger log = LoggerFactory.getLogger(NewSkroutzRestClient.class);
    private final ClientConfig config = new ClientConfig();
    private final HttpHost host;
    private String token;
    private int remainingRequests;
    private CloseableHttpClient defaultClient;
    private CloseableHttpClient multiThreadedClient;

    public NewSkroutzRestClient(String token) {
        this.token = token;
        this.remainingRequests = 100;
        initClient();
        host = new HttpHost("api.skroutz.gr");
    }

    private void initClient() {
        // Jersey uses java.util.logging - bridge to slf4
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        // custom Jackson mapper to handle escaped characters
        jacksonProvider.setMapper(new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true));
        config.register(new LoggingFilter(java.util.logging.Logger.getLogger(getClass().getCanonicalName()), true))
                .register(new CsrfProtectionFilter())
                .register(jacksonProvider);

        // configure the connection manager
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);


        // initialize the client
        defaultClient = HttpClients.custom().setConnectionManager(cm).build();

//        HttpContext context = new HttpClientContext().
    }

    @Override
    public <T extends SkroutzEntity> T get(Class<T> tClass, Integer skroutzId) throws RuntimeException {
        return get(tClass, skroutzId, null);
    }

    @Override
    public <T extends SkroutzEntity> T get(Class<T> tClass, Integer skroutzId, String etag) throws RuntimeException {
        URI uri = Utils.getMatchingUri(tClass, skroutzId);
        Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
        Response response;
        T result;
        // get response
        if (etag == null) {
            response = sendGetRequest(uri);
        } else {
            response = sendGetRequest(uri, etag);
        }
        // what's the response status
        Response.StatusType status = response.getStatusInfo();
        if (status.getStatusCode() == Response.Status.NOT_MODIFIED.getStatusCode()) {   // the entity is not modified from our local copy
            throw new NotModifiedException(tClass, skroutzId);                          // throw exception to return our local copy
        } else if (status.getStatusCode() == Response.Status.OK.getStatusCode()) {
            String newEtag = response.getEntityTag().getValue();                        // the response was ok
            if (response.hasEntity()) {                                                 // parse entity and return it
                result = response.readEntity(responseClass).getItem();
                result.setEtag(newEtag);
                return result;
            } else {
                throw new ResponseWithoutEntityException(tClass, skroutzId);
            }
        } else {
            throw new UnknownResponseStatusException(tClass, skroutzId, status);        // throw new unknown status exception for failed requests
        }
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass, String etag) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass, String etag, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> T get(T entity) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, String etag) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, String etag, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAll(Class<T> tClass) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAll(Class<T> tClass, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getAllNested(U parentEntity, Class<T> childClass) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getAllNested(U parentEntity, Class<T> childClass, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> getNextPage(Page<T> page) {
        return null;
    }

    @Override
    public SearchResults search(String query) throws UnsupportedEncodingException {
        return null;
    }


    /**
     * Helper method to wrap a result as a {@link Page}
     *
     * @param <T>           A class type that extends {@link SkroutzEntity}
     * @param response      The response we got from the server.
     * @param responseClass The class used to deserialize the response.
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
                page.setUri(response.getLocation());
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
     * @return A {@link Response} containing one or more entities or null.
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
            // persist/merge request
            ((BargainHuntUI) UI.getCurrent()).getRequests().saveOrUpdate(new Request(Utils.getFullPathFromUri(requestUri), response.getEntityTag() != null ? response.getEntityTag().getValue() : null));
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
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
     * @return A {@link Response} containing one or more entities.
     */
    private Response sendGetRequest(URI requestUri, String eTag) throws ProcessingException {
        Response response;
        try {
            response = ClientBuilder.newClient(config).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.IF_NONE_MATCH, "\"" + eTag + "\"")
                    .accept("application/vnd.skroutz+json; version=3")
                    .get();
            //persist/merge request
            ((BargainHuntUI) UI.getCurrent()).getRequests().saveOrUpdate(new Request(Utils.getFullPathFromUri(requestUri), response.getEntityTag() != null ? response.getEntityTag().getValue() : null));
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + requestUri, e);
            throw e;
        }
        return response;
    }
}