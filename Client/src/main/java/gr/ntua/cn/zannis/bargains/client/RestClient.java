package gr.ntua.cn.zannis.bargains.client;

import gr.ntua.cn.zannis.bargains.entities.Category;
import gr.ntua.cn.zannis.bargains.entities.Product;
import gr.ntua.cn.zannis.bargains.entities.Shop;
import gr.ntua.cn.zannis.bargains.entities.Sku;

import java.util.List;

/**
 * The RESTful Client interface that targets an API that can provide
 * products, shops, and their metadata.
 * @author zannis <zannis.kal@gmail.com
 */
public interface RestClient {

    /**
     * Create a request for a specific product using its id. This is supposed to
     * be used when we don't have a persistent instance of the product.
     * @param productId The product id.
     * @return The {@link gr.ntua.cn.zannis.bargains.entities.Product} entity
     * or null if there was an error.
     */
    public Product getProductById(Integer productId);

    /**
     * Create a conditional request for a specific product using its persistent
     * entity.
     * @param product The persistent entity.
     * @return The {@link gr.ntua.cn.zannis.bargains.entities.Product} entity
     * with its possibly updated fields or null if there was an error.
     */
    public Product checkProduct(Product product);

    public Product getProductByShopUid(String shopUid);

    public List<Product> searchProductsByName(String name);

    public List<Sku> searchSkuByName(String productName);

    public Shop getShop(Integer shopId);

    public Shop getShop(String shopName);

    public Category getCategory(Integer categoryId);

    public Category getCategory(String categoryName);

    public List<Product> getAllProducts();

}
