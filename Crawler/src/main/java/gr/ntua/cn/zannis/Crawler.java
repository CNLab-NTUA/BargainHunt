package gr.ntua.cn.zannis;

import java.util.List;

/**
 * Created by zannis on 3/13/15.
 */
public interface Crawler {

    public Product getProduct(Integer productId);

    public Product getProduct(String productName);

    public List<Product> getAllProducts();

}
