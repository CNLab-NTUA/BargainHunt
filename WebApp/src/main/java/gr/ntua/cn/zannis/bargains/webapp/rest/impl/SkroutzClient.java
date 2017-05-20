package gr.ntua.cn.zannis.bargains.webapp.rest.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.vaadin.ui.UI;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Request;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.rest.RestClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.misc.Utils;
import gr.ntua.cn.zannis.bargains.webapp.rest.requests.filters.Filter;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.RestResponse;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl.UriResponse;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gr.ntua.cn.zannis.bargains.webapp.rest.misc.Const.API_HOST;
import static gr.ntua.cn.zannis.bargains.webapp.rest.misc.Const.SEARCH;
import static javax.ws.rs.core.Response.Status.*;

/**
 *
 * The Skroutz REST API Client, implemented as a singleton.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
@Stateless
@LocalBean
public class SkroutzClient implements RestClient {

    private static volatile SkroutzClient instance;
    protected static final Logger log = LoggerFactory.getLogger(SkroutzClient.class);
    private String target;
    private String token;
    final ClientConfig config = new ClientConfig();

    int remainingRequests;

    @PersistenceContext
    private EntityManager em;

    private SkroutzClient(String token) {
        this.target = API_HOST;
        this.token = token;
        log.debug("SkroutzClient started.");
        initClientConfig();
        log.debug("Client configuration done.");
    }

    public SkroutzClient() {
        SkroutzClient.getInstance();
    }

    /**
     * Method to get a page of {@link Product}s from a given {@link Sku}.
     *
     * @param sku The sku to retrieve products from.
     * @return A {@link Page <Product>}.
     */
    public Page<Product> getProductsFromSku(Sku sku) {
        return getNested(sku, Product.class);
    }

    /**
     * Static method to retrieve singleton instance.
     *
     * @return The singleton instance of {@link SkroutzClient}
     */
    public static synchronized SkroutzClient getInstance() {
        if (instance == null) {
            String token = Utils.getLocalAccessToken();
            if (token == null) {
                try {
                    Utils.initClientPropertyFiles();
                    token = Utils.updateAccessToken();
                    instance = new SkroutzClient(token);
                } catch (IOException e) {
                    log.error("Authentication error", e);
                }
            } else {

                instance = new SkroutzClient(token);
            }
        }
        return instance;
    }

    @Override
    public <T extends SkroutzEntity> T get(Class<T> tClass, Integer skroutzId) {
        T restEntity = null;
        URI uri = Utils.getMatchingUri(tClass, skroutzId);
        Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
        try {
            UriResponse response = sendGetRequest(uri);
            if (response.getResponse().getStatus() == Response.Status.OK.getStatusCode()) {
                String etag = response.getResponse().getEntityTag().getValue();
                if (response.getResponse().hasEntity()) {
                    restEntity = response.getResponse().readEntity(responseClass).getItem();
                    restEntity.setEtag(etag);
                } else {
                    Notifier.warn("No entity in the response.");
                }
            } else {
                Notifier.error("Request " + uri + " failed with status " + response.getResponse().getStatus(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restEntity;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass) {
        Page<T> result = null;
        URI uri = Utils.getMatchingUri(tClass);
        try {
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
            UriResponse response = sendGetRequest(uri);
            result = extractPage(response, responseClass);
//            if (result != null) {
//                ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(tClass, result.getItems());
//            }
        } catch (Exception e) {
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
            UriResponse response = sendGetRequest(uri);
            result = extractPage(response, responseClass);
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + uri, e);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SkroutzEntity> T get(T entity) {
        return get((Class<T>) entity.getClass(), entity.getSkroutzId());
    }

    @Override
    public <T extends SkroutzEntity> Page<T> getNextPage(Page<T> page) {
        Page<T> result = null;
        URI nextUri = null;
        try {
            if (page.hasNext()) {
                // check if it's the last page
                nextUri = page.getNext() != null ? page.getNext() : page.getLast();
                if (nextUri != null) {
                    Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(page.getEntityType());
                    UriResponse response = sendGetRequest(nextUri);
                    result = extractPage(response, responseClass);
                }
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
        try { // todo add conditional uri here
            UriResponse response = sendGetRequest(uri);
            Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(childClass);
            result = extractPage(response, responseClass);
            if (result != null && result.getItems() != null && result.getItems().size() > 0) {
                result.setItems(((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(childClass, result.getItems()));
            }
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
            UriResponse response = sendGetRequest(uri);
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
    public SearchResults search(String query) throws UnsupportedEncodingException {
        SearchResults results = null;
        String encodedQuery = URLEncoder.encode(query, "utf-8");
        URI uri = UriBuilder.fromUri(target).path(SEARCH).queryParam("q", encodedQuery).build();
        try {
            UriResponse response = sendGetRequest(uri);
            Map<String, URI> links = Utils.getLinks(response.getResponse());
            results = response.getResponse().readEntity(SearchResults.class);
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
    private void initClientConfig() {
        // Jersey uses java.util.logging - bridge to slf4
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        // custom Jackson mapper to handle escaped characters
        jacksonProvider.setMapper(new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true));
        config.register(new LoggingFeature(java.util.logging.Logger.getLogger(getClass().getCanonicalName())))
                .register(jacksonProvider);
    }

    /**
     * Helper method to wrap a result as a {@link Page}
     *
     * @param <T>           A class type that extends {@link SkroutzEntity}
     * @param uriResponse   The response we got from the server.
     * @param responseClass The class used to deserialize the response.
     * @return The first {@link Page<T>} from the result list.
     */
    private <T extends SkroutzEntity> Page<T> extractPage(UriResponse uriResponse, Class<? extends RestResponse<T>> responseClass) {
        // check response status first
        Response response = uriResponse.getResponse();
        int status = response.getStatus();
        if (status == UNAUTHORIZED.getStatusCode()) {
            Notifier.error("Το αίτημα απέτυχε. Το Skroutz access_token ανανεώνεται...", true);
            try {
                Utils.updateAccessToken();
                Notifier.info("Το access_token ανανεώθηκε. Παρακαλώ ξαναπροσπαθήστε!");
            } catch (IOException e) {
                Notifier.error("Υπήρξε πρόβλημα στην ανανέωση του access_token.", true);
                e.printStackTrace();
            }
            return null;
        } else if (status == NOT_MODIFIED.getStatusCode()) {
            // todo get etag, check with db etag, ...
            return null;
        } else if (status == OK.getStatusCode()) {
            // parse useful headers
            Map<String, URI> links = Utils.getLinks(response);
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                RestResponse<T> parsedResponse = response.readEntity(responseClass);
                Page<T> page = parsedResponse.getPage();
                if (links.size() > 0) {
                    page.setPrev(links.get("prev"));
                    page.setNext(links.get("next"));
                    page.setLast(links.get("last"));
                } else {
                    String currentUrl = uriResponse.getUri().toString();
                    String currentUrlWithoutParams = currentUrl.indexOf('?') > -1 ? currentUrl.substring(0, currentUrl.indexOf('?')) : currentUrl;

                    Map<String, List<String>> currentUrlParams = Utils.getQueryParams(currentUrl);
                    if (currentUrlParams.containsKey("page")) {
                        if (currentUrlParams.get("page").get(0).equals("1")) {
                            page.setPrev(null);
                        } else {
                            page.setPrev(URI.create(currentUrlWithoutParams + "?page=" + (parsedResponse.getMeta().getPagination().getCurrentPage() - 1)));
                        }
                        if (Integer.parseInt(currentUrlParams.get("page").get(0)) == (parsedResponse.getMeta().getPagination().getTotalPages())) {
                            page.setNext(null);
                        } else {
                            page.setNext(URI.create(currentUrlWithoutParams + "?page=" + (parsedResponse.getMeta().getPagination().getCurrentPage() + 1)));
                        }
                        page.setLast(URI.create(currentUrlWithoutParams + "?page=" + parsedResponse.getMeta().getPagination().getTotalPages()));
                    } else {
                        page.setPrev(null);
                        if (parsedResponse.getMeta().getPagination().getTotalPages() > 1) {
                            page.setNext(URI.create(currentUrlWithoutParams + "?page=" + (parsedResponse.getMeta().getPagination().getCurrentPage() + 1)));
                        } else {
                            page.setNext(null);
                        }
                        page.setLast(URI.create(currentUrlWithoutParams + "?page=" + parsedResponse.getMeta().getPagination().getTotalPages()));
                    }
                }
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
    private UriResponse sendGetRequest(URI requestUri) throws ProcessingException {
        UriResponse response = new UriResponse();
        try {
            response.setResponse(ClientBuilder.newClient(config).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept("application/vnd.skroutz+json; version=3")
                    .get());
            response.setUri(requestUri);
            // persist/merge request
            ((BargainHuntUI) UI.getCurrent()).getRequests().saveOrUpdate(new Request(Utils.getFullPathFromUri(requestUri), response.getResponse().getEntityTag() != null ? response.getResponse().getEntityTag().getValue() : null));
            remainingRequests = Integer.parseInt(response.getResponse().getHeaderString("X-RateLimit-Remaining"));
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
    private UriResponse sendGetRequest(URI requestUri, String eTag) throws ProcessingException {
        UriResponse response = new UriResponse();
        try {
            response.setResponse(ClientBuilder.newClient(config).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.IF_NONE_MATCH, "\"" + eTag + "\"")
                    .accept("application/vnd.skroutz+json; version=3")
                    .get());
            response.setUri(requestUri);
            //persist/merge request
//            ((BargainHuntUI) UI.getCurrent()).getRequests().saveOrUpdate(new Request(Utils.getFullPathFromUri(requestUri), response.getResponse().getEntityTag() != null ? response.getResponse().getEntityTag().getValue() : null));
            remainingRequests = Integer.parseInt(response.getResponse().getHeaderString("X-RateLimit-Remaining"));
        } catch (ProcessingException e) {
            log.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + requestUri, e);
            throw e;
        }
        return response;
    }
}
