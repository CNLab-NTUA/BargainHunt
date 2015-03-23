package gr.ntua.cn.zannis.bargains.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gr.ntua.cn.zannis.bargains.client.RestClient;
import gr.ntua.cn.zannis.bargains.client.components.Page;
import gr.ntua.cn.zannis.bargains.client.dto.ProductsResponse;
import gr.ntua.cn.zannis.bargains.client.dto.ShopsResponse;
import gr.ntua.cn.zannis.bargains.client.entities.Category;
import gr.ntua.cn.zannis.bargains.client.entities.Product;
import gr.ntua.cn.zannis.bargains.client.entities.Shop;
import gr.ntua.cn.zannis.bargains.client.entities.Sku;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
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
import java.util.List;

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
        URI productUri = UriBuilder.fromPath(API_HOST).path(SINGLE_PRODUCT).build(productId);
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
                ProductsResponse wrapper = response.readEntity(ProductsResponse.class);
                wrapper.getProduct().setEtag(eTag);
                wrapper.getProduct().setInsertedAt(new Date());
                wrapper.getProduct().setCheckedAt(new Date());
                return wrapper.getProduct();
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    @Override
    public Product checkProduct(Product product) {
        URI productUri = UriBuilder.fromPath(API_HOST).path(SINGLE_PRODUCT).build(product.getSkroutzId());
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
                product.updateFromJson(response.readEntity(ProductsResponse.class).getProduct(), eTag);
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
                ProductsResponse wrapper = response.readEntity(ProductsResponse.class);
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
        URI shopUri = UriBuilder.fromPath(API_HOST).path(SINGLE_SHOP).build(shopId);
        Response response = sendUnconditionalGetRequest(shopUri);
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
                ShopsResponse wrapper = response.readEntity(ShopsResponse.class);
                wrapper.getShop().setEtag(eTag);
                wrapper.getShop().setInsertedAt(new Date());
                return wrapper.getShop();
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
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

            Link next = checkLinks(response);

            // TODO: parse Link headers for more pages
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                ShopsResponse wrapper = response.readEntity(ShopsResponse.class);
                List<Shop> shops = wrapper.getShops();
                for (Shop shop : shops) {
                    shop.setInsertedAt(new Date());
                }
//                if (next != null) {
//                    shops.addAll()
//                }
                return null;
            } else {
                log.error("No entity in the response.");
                return null;
            }
        }
    }

    private Link checkLinks(Response response) {
        if (response.hasLink("next")) {
            return response.getLink("next");
        } else if (response.hasLink("last")) {
            return response.getLink("last");
        } else {
            return null;
        }
    }

    @Override
    public Page<Sku> searchSkuByName(String productName) {
        return null;
    }

    @Override
    public Page<Product> searchProductsByName(String productName) {
//        URI productsUri = UriBuilder.fromPath(API_HOST).path(SEARCH_PRODUCTS)
//                .queryParam("shop_uid", "{shopUid}").build(shopId, shopUid);
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

    public int getRemainingRequests() {
        return remainingRequests;
    }
}
