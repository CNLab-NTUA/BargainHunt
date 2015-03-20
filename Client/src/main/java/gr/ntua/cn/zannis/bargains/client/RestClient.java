package gr.ntua.cn.zannis.bargains.client;

import gr.ntua.cn.zannis.bargains.entities.Category;
import gr.ntua.cn.zannis.bargains.entities.Product;
import gr.ntua.cn.zannis.bargains.entities.Shop;
import gr.ntua.cn.zannis.bargains.entities.Sku;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public interface RestClient {

    /**
     * Create a request for a specific product using its id.
     * @param productId The product id.
     * @return The {@link gr.ntua.cn.zannis.bargains.entities.Product} entity
     * or null if there was an error
     */
    public Product getProductById(Integer productId);

    /**
     * Create a conditional request for a specific product using its id and an Etag
     * acquired from the server.
     * @param productId The product id.
     * @param eTag The 32-character tag.
     * @return The {@link gr.ntua.cn.zannis.bargains.entities.Product} entity
     * or null if there was an error
     */
    public Product getProductById(Integer productId, String eTag);

    public List<Product> searchProductsByName(String name);

    public List<Sku> searchSkuByName(String productName);

    public Shop getShop(Integer shopId);

    public Shop getShop(String shopName);

    public Category getCategory(Integer categoryId);

    public Category getCategory(String categoryName);

    public List<Product> getAllProducts();

}
