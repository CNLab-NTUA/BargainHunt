package gr.ntua.cn.zannis.bargains.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import gr.ntua.cn.zannis.bargains.client.dto.impl.TokenResponse;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.*;

/**
 * Crawler implementation for Bargain hunting application.
 * @author zannis <zannis.kal@gmail.com>
 */
public final class SkroutzRestClient extends RestClientImpl {

    public SkroutzRestClient(String token) {
        super(API_HOST, token);
        log.debug("SkroutzClient started.");
        initClientConfig();
        log.debug("Client configuration done.");
    }

    public static void main(String[] args) {
        try {
            SkroutzRestClient client = null;
            Utils.initPropertiesFiles();
            String token = Utils.getAccessToken();
            if (token == null) {
                TokenResponse response = Utils.requestAccessToken();
                if (response != null) {
                    client = new SkroutzRestClient(response.getAccessToken());
                }
            } else {
                client = new SkroutzRestClient(token);
            }
            if (client != null) {
                // run queries here!
//                Product p = client.getProductById(18427940);
//                Product p2 = client.checkProduct(p);
//                Product p3 = client.getProductByShopUid(11, "2209985");
//                Page<Shop> plaisioPage = client.searchShopsByName("pla");
//                Category testCateg = client.getCategoryById(30);
//                Sku motoE = client.getSkuById(4977937);
//                Page<Product> motoeProductsPage = client.getProductsFromSku(motoE);
//                List<Product> products = client.getAllResults(Product.class, motoeProductsPage);
//                System.out.println(products.size());
                List<Category> categories = client.getAllCategories();
                System.out.println(categories.size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to get a page of {@link Product}s from a given {@link Sku}.
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
     * @param productId The product id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * or null if there was an error.
     */
    public Product getProductById(Integer productId) {
        return getById(Product.class, productId);
    }

    public Product checkProduct(Product product) {
        return getByEntity(product);
    }

    /**
     * Create a request for a specific product in a specific shop when we know its shop_uid.
     * This is supposed to be used when we don't have a persistent instance of the product.
     * @param shopId The shop id.
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
     * @param shopId The shop id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop} entity
     * or null if there was an error.
     */
    public Shop getShopById(Integer shopId) {
        return getById(Shop.class, shopId);
    }

    /**
     * Create a request for a specific {@link Shop} using a String query. This is supposed to
     * be used when we don't have a persistent instance of the {@link Shop} and we don't know
     * its' id. Can return multiple results.
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
     * Create a request for a specific {@link Sku} using a String query. This is supposed to
     * be used when we don't have a persistent instance of the {@link Sku} and we don't know
     * its' id. Can return multiple results.
     * @param skuName The {@link Sku} name we search for.
     * @return A {@link Page} containing
     * the returned SKUs.
     */
    public Page<Sku> searchSkusByName(String skuName) {
        URI uri = UriBuilder.fromPath(API_HOST).path(SKUS).path(SEARCH)
                .queryParam("q", skuName).build();
        return getPageByCustomUri(Sku.class, uri);
    }

    /**
     * Create a request for a specific {@link Product} using a String query. This is supposed to
     * be used when we don't have a persistent instance of the {@link Product} and we don't know
     * its' id. Can return multiple results.
     * @param productName The {@link Product} name we search for.
     * @return A {@link Page} containing
     * the returned products.
     */
    public Page<Product> searchProductsByName(String productName) {
        URI uri = UriBuilder.fromPath(API_HOST).path(PRODUCTS).path(SEARCH)
                .queryParam("q", productName).build();
        return getPageByCustomUri(Product.class, uri);
    }

    public Category getCategoryById(Integer categoryId) {
        return getById(Category.class, categoryId);
    }

    /**
     * Create a request for a specific {@link Category} using a String query. This is supposed to
     * be used when we don't have a persistent instance of the {@link Category} and we don't know
     * its' id. Can return multiple results.
     * @param categoryName The {@link Category} name we search for.
     * @return A {@link Page} containing
     * the returned categories.
     */
    public Page<Category> searchCategoriesByName(String categoryName) {
        URI uri = UriBuilder.fromPath(API_HOST).path(CATEGORIES).path(SEARCH)
                .queryParam("q", categoryName).build();
        return getPageByCustomUri(Category.class, uri);
    }

    public Sku getSkuById(Integer skuId) {
        return getById(Sku.class, skuId);
    }

    public List<Category> getAllCategories() {
        return getAllResults(Category.class, getAll(Category.class));
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }
}
