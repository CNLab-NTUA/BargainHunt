package gr.ntua.cn.zannis.bargains.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.client.responses.impl.TokenResponse;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Page;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.naming.AuthenticationException;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.*;

/**
 * The Skroutz REST API Client, implemented as a singleton.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public final class SkroutzRestClient extends RestClientImpl {

    private static volatile SkroutzRestClient instance;

    public SkroutzRestClient(String token) {
        super(API_HOST, token);
        log.debug("SkroutzClient started.");
        initClientConfig();
        log.debug("Client configuration done.");
    }

    /**
     * Static method to retrieve singleton instance.
     * @return The singleton instance of {@link SkroutzRestClient}
     */
     public static synchronized SkroutzRestClient get() {
        if (instance == null) {
            String token = Utils.getLocalAccessToken();
            if (token == null) {
                TokenResponse response;
                try {
                    response = Utils.requestAccessToken();
                    token = response.getAccessToken();
                    instance = new SkroutzRestClient(token);
                } catch (AuthenticationException e) {
                    log.error("Authentication error", e);
                }
            } else {
                instance = new SkroutzRestClient(token);
            }
        }
        return instance;
    }

    /**
     * Method to get a page of {@link Product}s from a given {@link Sku}.
     *
     * @param sku The sku to retrieve products from.
     * @return A {@link Page<Product>}.
     */
    private Page<Product> getProductsFromSku(Sku sku) {
        URI uri = UriBuilder.fromPath(API_HOST).path(SKUS).path(ID).path(PRODUCTS).build(sku.getSkroutzId());
        return getPageByCustomUri(Product.class, uri);
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
     *
     * @param productId The product id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * or null if there was an error.
     */
    public Product getProductById(Long productId) {
        return get(Product.class, productId);
    }

    public Product checkProduct(Product product) {
        return getByEntity(product);
    }

    /**
     * Create a request for a specific product in a specific shop when we know its shop_uid.
     * This is supposed to be used when we don't have a persistent instance of the product.
     *
     * @param shopId  The shop id.
     * @param shopUid The product's shop_uid.
     * @return The {@link Product} entity
     * or null if there was an error.
     */
    public Product getProductByShopUid(long shopId, String shopUid) {
        URI uri = UriBuilder.fromPath(API_HOST).path(SHOPS).path(ID).path(PRODUCTS).path(SEARCH)
                .queryParam("shop_uid", shopUid).build(shopId);
        // we use this because the response is wrapped in an array.
        Page<Product> page = getPageByCustomUri(Product.class, uri);
        if (page != null) {
            return page.getFirstItem();
        } else {
            return null;
        }
    }

    /**
     * Create a request for a specific shop using its id. This is supposed to
     * be used when we don't have a persistent instance of the shop.
     *
     * @param shopId The shop id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop} entity
     * or null if there was an error.
     */
    public Shop getShopById(Long shopId) {
        return get(Shop.class, shopId);
    }

    /**
     * Create a request for a specific {@link Shop} using a String query. This is supposed to
     * be used when we don't have a persistent instance of the {@link Shop} and we don't know
     * its' id. Can return multiple results.
     *
     * @param shopName The {@link Shop} name we search for.
     * @return A {@link Page} containing
     * the returned shops.
     */
    public Page<Shop> searchShopsByName(String shopName) {
        URI uri = UriBuilder.fromPath(API_HOST).path(SHOPS).path(SEARCH)
                .queryParam("q", shopName).build();
        return getPageByCustomUri(Shop.class, uri);
    }

    /**
     * Create a request for a specific {@link Sku} using a String query and it's category ID.
     * This is supposed to be used when we don't have a persistent instance of the {@link Sku}
     * and we don't know its' id. Can return multiple results.
     *
     * @param query The {@link Sku} name we search for.
     * @return A {@link Page<Sku>} containing the returned SKUs.
     */
    public Page<Sku> searchSkusFromCategory(Long categoryId, String query) {
        URI uri = UriBuilder.fromPath(API_HOST).path(CATEGORIES).path(ID).path(SKUS)
                .queryParam("q", query).build(categoryId);
        return getPageByCustomUri(Sku.class, uri);
    }

    /**
     * Create a request for a specific {@link Product} using a String query. This is supposed to
     * be used when we don't have a persistent instance of the {@link Product} and we don't know
     * its' id. Can return multiple results.
     *
     * @param productName The {@link Product} name we search for.
     * @return A {@link Page} containing
     * the returned products.
     */
    public Page<Product> searchProductsByName(String productName) {
        URI uri = UriBuilder.fromPath(API_HOST).path(PRODUCTS).path(SEARCH)
                .queryParam("q", productName).build();
        return getPageByCustomUri(Product.class, uri);
    }

    public Category getCategoryById(Long categoryId) {
        return get(Category.class, categoryId);
    }

    public Sku getSkuById(Long skuId) {
        return get(Sku.class, skuId);
    }

    public List<Category> getAllCategories() {
        return getAsList(Category.class);
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }
}
