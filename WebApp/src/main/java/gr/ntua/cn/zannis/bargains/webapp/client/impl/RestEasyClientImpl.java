package gr.ntua.cn.zannis.bargains.webapp.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gr.ntua.cn.zannis.bargains.webapp.client.RestClient;
import gr.ntua.cn.zannis.bargains.webapp.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.webapp.client.requests.filters.Filter;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.RestResponse;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.components.Notifier;
import gr.ntua.cn.zannis.bargains.webapp.ejb.SkroutzEntityManagerImpl;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ejb.EJB;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class RestEasyClientImpl implements RestClient {

    private static RestEasyClientImpl ourInstance = new RestEasyClientImpl();
    private final String token;
    private final ClientConfig configuration = new ClientConfig();
    @EJB
    private SkroutzEntityManagerImpl em;
    private int remainingRequests = 100;

    private RestEasyClientImpl() {
        // todo get/renew api key
        token = null;

        // custom Jackson mapper to handle escaped characters
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true));
        configuration.register(new LoggingFilter(java.util.logging.Logger.getLogger(getClass().getCanonicalName()), true))
                .register(new CsrfProtectionFilter())
                .register(jacksonProvider);
    }

    public static RestEasyClientImpl getInstance() {
        return ourInstance;
    }

    @Override
    public <T extends SkroutzEntity> T get(Class<T> tClass, Long skroutzId) {
        T restEntity;
        T persistentEntity = em.find(tClass, skroutzId);
        URI uri = Utils.getMatchingUri(tClass);
        Class<? extends RestResponse<T>> responseClass = Utils.getMatchingResponse(tClass);
        if (persistentEntity != null && persistentEntity.hasEtag()) {
            Response response = sendGetRequest(uri, persistentEntity.getEtag());
            // if the remote entity matches our local copy, return ours
            if (response.getStatus() == Response.Status.NOT_MODIFIED.getStatusCode()) {
                return persistentEntity;
                // if the remote copy is newer than our local copy, update it and return it
            } else if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String etag = response.getEntityTag().getValue();
                if (response.hasEntity()) {
                    restEntity = response.readEntity(responseClass).getItem();
                    restEntity.setEtag(etag);
                    persistentEntity.updateFrom(restEntity);
                    em.merge(persistentEntity);
                    return persistentEntity;
                }
            }
        } else {
            Response response = sendGetRequest(uri);
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String etag = response.getEntityTag().getValue();
                if (response.hasEntity()) {
                    restEntity = response.readEntity(responseClass).getItem();
                    restEntity.setEtag(etag);
                    em.persist(restEntity);
                    return restEntity;
                }
            }
        }
        Notifier.warn("No entity in the response.");
        return null;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> Page<T> get(Class<T> tClass, Filter... filters) {
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
    public <T extends SkroutzEntity, U extends SkroutzEntity> Page<T> getNested(U parentEntity, Class<T> childClass, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAsList(Class<T> tClass) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity> List<T> getAsList(Class<T> tClass, Filter... filters) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getNestedAsList(U parentEntity, Class<T> childClass) {
        return null;
    }

    @Override
    public <T extends SkroutzEntity, U extends SkroutzEntity> List<T> getNestedAsList(U parentEntity, Class<T> childClass, Filter... filters) {
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
     * Creates and sends an unconditional GET HTTP request to the Skroutz API. All unconditional
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
            response = ResteasyClientBuilder.newClient(configuration).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept("application/vnd.skroutz+json; version=3")
                    .get();
            // todo persist/merge request
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
        } catch (ProcessingException e) {
            Notifier.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + requestUri, e);
            throw e;
        }
        return response;
    }

    /**
     * Creates and sends a conditional GET HTTP request to the Skroutz API. All conditional
     * public GET methods should use this since its preconfigured using our custom
     * configuration. This method follows redirects, accepts JSON entities and contains
     * the required authorization headers. It uses an If-None-Match condition
     * with the provided Etag argument.
     *
     * @param requestUri The target URI.
     * @param eTag       The Entity Tag to add to the request.
     * @return A {@link Response} containing one or more entities.
     */
    private Response sendGetRequest(URI requestUri, String eTag) throws ProcessingException {
        Response response;
        try {
            response = ResteasyClientBuilder.newClient(configuration).target(requestUri)
                    .property(ClientProperties.FOLLOW_REDIRECTS, true)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.IF_NONE_MATCH, eTag)
                    .accept("application/vnd.skroutz+json; version=3")
                    .get();
            // todo persist/merge request
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
        } catch (ProcessingException e) {
            Notifier.error("Υπήρξε πρόβλημα κατά την εκτέλεση του GET request : " + requestUri, e);
            throw e;
        }
        return response;
    }
}
