package gr.ntua.cn.zannis.bargains.crawler;

import gr.ntua.cn.zannis.bargains.entities.Category;
import gr.ntua.cn.zannis.bargains.entities.Product;
import gr.ntua.cn.zannis.bargains.entities.Shop;
import gr.ntua.cn.zannis.bargains.entities.Sku;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public interface RestClient {

    public Product getProductById(Integer productId);

    public List<Product> searchProductsByName(String name);

    public List<Sku> searchSkuByName(String productName);

    public Shop getShop(Integer shopId);

    public Shop getShop(String shopName);

    public Category getCategory(Integer categoryId);

    public Category getCategory(String categoryName);

    public List<Product> getAllProducts();

}
