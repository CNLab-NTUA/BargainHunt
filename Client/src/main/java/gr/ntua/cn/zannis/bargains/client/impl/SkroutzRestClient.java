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
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
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

        initConfig();
    }

    private void initConfig() {
        // custom Jackson ObjectMapper to process wrapped entities
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        // configure our client with the custom JacksonJaxbJsonProvider
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(mapper);
        config.register(new LoggingFilter())
                .register(new CsrfProtectionFilter())
                .register(jacksonProvider);
    }

    public static void main(String[] args) {
        try {
            Utils.initPropertiesFiles();
            SkroutzRestClient client = new SkroutzRestClient();
            Product p = client.getProductById(18427940);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Product getProductById(Integer productId) {
        URI productUri = UriBuilder.fromPath(API_HOST).path(SINGLE_PRODUCT).build(productId);
        // generate GET request and send it
        Response response = ClientBuilder.newClient(config).target(productUri)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + token)
                .accept("application/vnd.skroutz+json; version=3")
                .get();
        // check response status first
        if (response.getStatus() != 200) {
            log.error("Error " + response.getStatus() + " - " + response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            // parse useful headers
            String eTag = response.getHeaderString("ETag");
            remainingRequests = Integer.parseInt(response.getHeaderString("X-RateLimit-Remaining"));
            // parse entity
            if (response.hasEntity()) {
                Product product = response.readEntity(Product.class);
                product.setEtag(eTag);
                return product;
            } else {
                log.error("No entity in the response.");
                log.debug(response.toString());
                return null;
            }
        }
    }

    @Override
    public Product getProductById(Integer productId, String eTag) {
        URI productUri = UriBuilder.fromPath(API_HOST).path(SINGLE_PRODUCT).build(productId);
        Response response = ClientBuilder.newClient(config).target(productUri).request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + token)
                .accept("application/vnd.skroutz+json; version=3")
                .get();
        System.out.println(response.getHeaders());
        if (response.hasEntity()) {
            return response.readEntity(Product.class);
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

    public void setRemainingRequests(int remainingRequests) {
        this.remainingRequests = remainingRequests;
    }
}
