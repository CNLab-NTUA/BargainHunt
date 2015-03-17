package gr.ntua.cn.zannis.bargains.crawler;

import gr.ntua.cn.zannis.bargains.entities.Category;
import gr.ntua.cn.zannis.bargains.entities.Product;
import gr.ntua.cn.zannis.bargains.entities.Shop;

import java.util.List;

/**
 * Created by zannis on 3/13/15.
 */
public interface Crawler {

    public Product getProduct(Integer productId);

    public Product getProduct(String productName);

    public Shop getShop(Integer shopId);

    public Shop getShop(String shopName);

    public Category getCategory(Integer categoryId);

    public Category getCategory(String categoryName);

    public List<Product> getAllProducts();

}
