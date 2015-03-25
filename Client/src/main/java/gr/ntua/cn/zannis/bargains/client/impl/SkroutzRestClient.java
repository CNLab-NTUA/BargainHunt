package gr.ntua.cn.zannis.bargains.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gr.ntua.cn.zannis.bargains.client.dto.impl.ProductResponse;
import gr.ntua.cn.zannis.bargains.client.dto.impl.ShopResponse;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.*;

/**
 * Crawler implementation for Bargain hunting application.
 * @author zannis <zannis.kal@gmail.com>
 */
public final class SkroutzRestClient extends RestClientImpl {

    public SkroutzRestClient(String token) {
        super(API_HOST, token);
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

    @Override
    protected void initClientConfig() {
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
     * Create a request for a specific product using its id. This is supposed to
     * be used when we don't have a persistent instance of the product.
     * @param productId The product id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * or null if there was an error.
     */
    public Product getProductById(Integer productId) {
        return getById(Product.class, productId);
    }


    // TODO: implement getByEntity
    /**
     * Create a conditional request for a specific product using its persistent
     * entity.
     * @param product The persistent entity.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * with its possibly updated fields or null if there was an error.
     */
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

    /**
     * Create a request for a specific product in a specific shop when we know its shop_uid.
     * This is supposed to be used when we don't have a persistent instance of the product.
     * @param shopId The shop id.
     * @param shopUid The product's shop_uid.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * or null if there was an error.
     */
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

    /**
     * Create a request for a specific shop using its id. This is supposed to
     * be used when we don't have a persistent instance of the shop.
     * @param shopId The shop id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop} entity
     * or null if there was an error.
     */
    public Shop getShopById(Integer shopId) {
        return getById(Shop.class, shopId);
    }

    /**
     * Create a request for a specific shop using a String query. This is supposed to
     * be used when we don't have a persistent instance of the shop and we don't know
     * its id. Can return multiple results.
     * @param shopName The shop name we search for.
     * @return A {@link gr.ntua.cn.zannis.bargains.client.dto.meta.Page} containing
     * the returned shops.
     */
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

    public Page<Sku> searchSkuByName(String productName) {
        return null;
    }

    public Page<Product> searchProductsByName(String productName) {
        return null;
    }

    public Category getCategory(Integer categoryId) {
        return null;
    }

    public Category getCategory(String categoryName) {
        return null;
    }

    public Page<Category> getAllCategories() {
        return getAll(Category.class);
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }
}
