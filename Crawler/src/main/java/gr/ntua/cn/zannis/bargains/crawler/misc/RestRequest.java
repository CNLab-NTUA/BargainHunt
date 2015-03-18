package gr.ntua.cn.zannis.bargains.crawler.misc;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class RestRequest {

    private static final String API_HOST = "https://api.skroutz.gr";
    private static final String SINGLE_PRODUCT = "/products/{id}";
    private static final String SEARCH_PRODUCTS = "/shops/{shopId}/products/search";
    private static final String SINGLE_SHOP = "/shops/{id}";
    private static final String SKUS = "/categories/{categoryId}/skus";


    public static URI product(Integer productId) {
        return UriBuilder.fromPath(API_HOST).path(SINGLE_PRODUCT).build(productId);
    }

    public static URI searchProducts(Integer shopId, Integer shopUid) {
        return UriBuilder.fromPath(API_HOST).path(SEARCH_PRODUCTS)
                .queryParam("shop_uid", "{shopUid}").build(shopId, shopUid);
    }

}
