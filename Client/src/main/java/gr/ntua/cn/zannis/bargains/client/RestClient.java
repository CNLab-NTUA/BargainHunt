package gr.ntua.cn.zannis.bargains.client;

import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;

/**
 * The RESTful Client interface that targets an API that can provide
 * products, shops, categories, skus and their metadata.
 * @author zannis <zannis.kal@gmail.com
 */
public interface RestClient {

    /**
     * Create a request for a specific product using its id. This is supposed to
     * be used when we don't have a persistent instance of the product.
     * @param productId The product id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * or null if there was an error.
     */
    Product getProductById(Integer productId);

    /**
     * Create a conditional request for a specific product using its persistent
     * entity.
     * @param product The persistent entity.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * with its possibly updated fields or null if there was an error.
     */
    Product checkProduct(Product product);

    /**
     * Create a request for a specific product in a specific shop when we know its shop_uid.
     * This is supposed to be used when we don't have a persistent instance of the product.
     * @param shopId The shop id.
     * @param shopUid The product's shop_uid.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Product} entity
     * or null if there was an error.
     */
    Product getProductByShopUid(long shopId, String shopUid);

    Page<Product> searchProductsByName(String name);

    Page<Sku> searchSkuByName(String productName);

    /**
     * Create a request for a specific shop using its id. This is supposed to
     * be used when we don't have a persistent instance of the shop.
     * @param shopId The shop id.
     * @return The {@link gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop} entity
     * or null if there was an error.
     */
    Shop getShopById(Integer shopId);

    /**
     * Create a request for a specific shop using a String query. This is supposed to
     * be used when we don't have a persistent instance of the shop and we don't know
     * its id. Can return multiple results.
     * @param shopName The shop name we search for.
     * @return A {@link gr.ntua.cn.zannis.bargains.client.dto.meta.Page} containing
     * the returned shops.
     */
    Page<Shop> searchShopsByName(String shopName);

    Category getCategory(Integer categoryId);

    Category getCategory(String categoryName);

    Page<Category> getAllCategories();

}
