package gr.ntua.cn.zannis.bargains.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gr.ntua.cn.zannis.bargains.client.RestClient;
import gr.ntua.cn.zannis.bargains.client.dto.impl.*;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.client.persistence.PersistentEntity;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.*;

/**
 * Crawler implementation for Bargain hunting application.
 * @author zannis <zannis.kal@gmail.com>
 */
public final class SkroutzRestClient implements RestClient {

    private static final Logger log = LoggerFactory.getLogger(SkroutzRestClient.class);
    private final ClientConfig config = new ClientConfig();

    private String token;
    private int remainingRequests;

    public SkroutzRestClient() {
        log.debug("SkroutzClient started.");
        token = Utils.getAccessToken();
        if (token == null) {
            Utils.requestAccessToken();
        }
        initClientConfig();
    }

    public static void main(String[] args) {
        try {
            Utils.initPropertiesFiles();
            SkroutzRestClient client = new SkroutzRestClient();
//            Product p = client.getProductById(18427940);
//            Product p2 = client.checkProduct(p);
//            client.getProductByShopUid(11, "2209985");
            client.searchShopsByName("plaisio");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method that initializes our custom client configuration to
     * deserialize wrapped objects from JSON.
     * Update: removed Root Unwrapping.
     */
    private void initClientConfig() {
        // Jersey uses java.util.logging - bridge to slf4
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true));
        config.register(new LoggingFilter(java.util.logging.Logger.getLogger(getClass().getCanonicalName()), true))
                .register(new CsrfProtectionFilter())
                .register(jacksonProvider);
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
     * Helper function to extract Link headers from a HTTP response.
     * @param response The response to extract headers from.
     * @return A map of Link urls.
     */
    private Map<String, Link> getLinks(Response response) {
        Map<String, Link> map = new HashMap<>(3);
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

    @Override
    public Product getProductById(Integer productId) {
        return getById(Product.class, productId);
    }


    // TODO: getByEntity
    @Override
    public Product checkProduct(Product product) {
        URI productUri = UriBuilder.fromPath(API_HOST).path(PRODUCTS).build(product.getSkroutzId());
        Response response = sendConditionalGetRequest(productUri, product.getEtag());
        // check response status first
        if (response.getStatus() == 304) {
            product.wasJustChecked();
            return product;
        } else if (response.getStatus() == 200) {
            // parse useful headers
            String eTag = response.getHeaderString("ETag");
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                product.updateFromJson(response.readEntity(ProductResponse.class).getProduct(), eTag);
                return product;
            } else {
                log.error("No entity in the response.");
                return null;
            }
        } else {
            log.error(response.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    @Override
    public Product getProductByShopUid(long shopId, String shopUid) {
        URI productUri = UriBuilder.fromPath(API_HOST).path(SEARCH_PRODUCTS)
                .queryParam("shop_uid", shopUid).build(shopId);
        Response response = sendUnconditionalGetRequest(productUri);
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
                ProductResponse wrapper = response.readEntity(ProductResponse.class);
                Product product = wrapper.getProducts().get(0);
                product.setEtag(eTag);
                product.setInsertedAt(new Date());
                product.setCheckedAt(new Date());
                return product;
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    @Override
    public Shop getShopById(Integer shopId) {
        return getById(Shop.class, shopId);
    }

    @Override
    public Page<Shop> searchShopsByName(String shopName) {
        URI shopUri = UriBuilder.fromPath(API_HOST).path("/shops/search")
                .queryParam("q", shopName).build();
        Response response = sendUnconditionalGetRequest(shopUri);
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
                Page<Shop> page = response.readEntity(ShopResponse.class).toPage(links);
                for (Shop shop : page.getItems()) {
                    shop.setInsertedAt(new Date());
                }
                return page;
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    @Override
    public Page<Sku> searchSkuByName(String productName) {
        return null;
    }

    @Override
    public Page<Product> searchProductsByName(String productName) {
        return null;
    }

    @Override
    public Category getCategory(Integer categoryId) {
        return null;
    }

    @Override
    public Category getCategory(String categoryName) {
        return null;
    }

    @Override
    public Page<Category> getAllCategories() {
        return getAll(Category.class);
    }

    public <T extends PersistentEntity> Page<T> nextPage(Page<T> page) {
        if (page.hasNext()) {
            // TODO get link, unconditional query at link, getFirstPage
        }
    }

    public <T extends PersistentEntity> Page<T> getAll(Class<T> tClass) {
        URI uri = getMatchingUri(tClass, null, null);
        Class<? extends RestResponseImpl<T>> responseClass = getMatchingResponse(tClass);
        Response response = sendUnconditionalGetRequest(uri);
        return getFirstPage(responseClass, response);
    }

    private <T extends PersistentEntity> Page<T> getFirstPage(Class<? extends RestResponseImpl<T>> responseClass, Response response) {
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

    public <T extends PersistentEntity> T getById(Class<T> tClass, Integer id) {
        URI uri = getMatchingUri(tClass, ID, id);
        Class<? extends RestResponseImpl<T>> responseClass = getMatchingResponse(tClass);
        if (uri != null && responseClass != null) {
            Response response = sendUnconditionalGetRequest(uri);
            return getEntity(responseClass, response);
        } else {
            return null;
        }
    }

    /**
     * Method that matches a {@link PersistentEntity} to its corresponding {@link RestResponseImpl}
     *
     * @param tClass The {@link PersistentEntity} class type.
     * @param <T>    The actual {@link PersistentEntity}.
     * @return The matching {@link RestResponseImpl}.
     */
    @SuppressWarnings("unchecked")
    private <T extends PersistentEntity> Class<? extends RestResponseImpl<T>> getMatchingResponse(Class<T> tClass) {
        if (tClass.equals(Product.class)) {
            return (Class<? extends RestResponseImpl<T>>) ProductResponse.class;
        } else if (tClass.equals(Shop.class)) {
            return (Class<? extends RestResponseImpl<T>>) ShopResponse.class;
        } else if (tClass.equals(Category.class)) {
            return (Class<? extends RestResponseImpl<T>>) CategoryResponse.class;
        } else if (tClass.equals(Sku.class)) {
            return (Class<? extends RestResponseImpl<T>>) SkuResponse.class;
        } else if (tClass.equals(Manufacturer.class)) {
            return (Class<? extends RestResponseImpl<T>>) ManufacturerResponse.class;
        } else {
            return null;
        }
    }

    /**
     * Method that gets a {@link PersistentEntity} from a received {@link Response}.
     * @param responseClass The {@link RestResponseImpl} class to use for deserialization.
     * @param response The {@link Response} we got from the API.
     * @param <T> The {@link PersistentEntity} class we want.
     * @return An entity of type {@link T}
     */
    private <T extends PersistentEntity> T getEntity(Class<? extends RestResponseImpl<T>> responseClass, Response response) {
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
                RestResponseImpl<T> wrapper = response.readEntity(responseClass);
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
     * Method that generates a {@link URI} for a specific request.
     *
     * @param tClass   The persistent entity class type to use as path.
     * @param template The query template.
     * @param values   The values to use to build the query.
     * @param <T>      A class type extending {@link PersistentEntity}
     * @return A URI matching the above input.
     */
    private <T extends PersistentEntity> URI getMatchingUri(Class<T> tClass, String template, Object... values) {
        UriBuilder builder = UriBuilder.fromPath(API_HOST);
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

    public int getRemainingRequests() {
        return remainingRequests;
    }
}
