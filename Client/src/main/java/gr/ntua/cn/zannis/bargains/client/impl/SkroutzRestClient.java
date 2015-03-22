package gr.ntua.cn.zannis.bargains.client.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gr.ntua.cn.zannis.bargains.client.RestClient;
import gr.ntua.cn.zannis.bargains.client.dto.TokenResponse;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.entities.Category;
import gr.ntua.cn.zannis.bargains.entities.Product;
import gr.ntua.cn.zannis.bargains.entities.Shop;
import gr.ntua.cn.zannis.bargains.entities.Sku;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Crawler implementation for Bargain hunting application.
 * @author zannis <zannis.kal@gmail.com>
 */
public final class SkroutzRestClient implements RestClient {

    private static final Logger log = LoggerFactory.getLogger(SkroutzRestClient.class);

    private static final String API_HOST = "https://api.skroutz.gr";
    private static final String SINGLE_PRODUCT = "/products/{id}";
    private static final String SEARCH_PRODUCTS = "/shops/{shopId}/products/search";
    private static final String SINGLE_SHOP = "/shops/{id}";
    private static final String SKUS = "/categories/{categoryId}/skus";
    private final ClientConfig config = new ClientConfig();

    private String token;
    private int remainingRequests;

    public SkroutzRestClient() {
        log.debug("SkroutzClient started.");
        token = Utils.getAccessToken();
        if (token == null) {
            TokenResponse response = Utils.requestAccessToken();
        }

        initClientConfig();
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
        jacksonProvider.setMapper(new ObjectMapper()
                .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true));
        config.register(new LoggingFilter())
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

    public static void main(String[] args) {
        try {
            Utils.initPropertiesFiles();
            SkroutzRestClient client = new SkroutzRestClient();
            Product p = client.getProductById(18427940);
            Product p2 = client.checkProduct(p);
            System.out.println(p2);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Product getProductById(Integer productId) {
        URI productUri = UriBuilder.fromPath(API_HOST).path(SINGLE_PRODUCT).build(productId);
        Response response = sendUnconditionalGetRequest(productUri);
        // check response status first
        if (response.getStatus() != 200) {
            return null;
        } else {
            // parse useful headers
            String eTag = response.getHeaderString("ETag");
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                Product product = response.readEntity(Product.class);
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
    public Product checkProduct(Product product) {
        URI productUri = UriBuilder.fromPath(API_HOST).path(SINGLE_PRODUCT).build(product.getSkroutzId());
        Response response = sendConditionalGetRequest(productUri, product.getEtag());
        // check response status first
        if (response.getStatus() == 304) {
            // the product hasn't changed, update only the checked time
            product.wasJustChecked();
            return product;
        } else if (response.getStatus() == 200) {
            // parse useful headers
            String eTag = response.getHeaderString("ETag");
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                product.updateFromJson(response.readEntity(Product.class), eTag);
                return product;
            } else {
                log.error("No entity in the response.");
                return null;
            }
        } else {
            log.error("Error " + response.getStatus() + " - " + response.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    @Override
    public List<Product> searchProductsByName(String productName) {
//        URI productsUri = UriBuilder.fromPath(API_HOST).path(SEARCH_PRODUCTS)
//                .queryParam("shop_uid", "{shopUid}").build(shopId, shopUid);
        return null;
    }

    @Override
    public List<Sku> searchSkuByName(String productName) {
        return null;
    }

    @Override
    public Shop getShop(Integer shopId) {
        return null;
    }

    @Override
    public Shop getShop(String shopName) {
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
    public List<Product> getAllProducts() {
        return null;
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }
}
