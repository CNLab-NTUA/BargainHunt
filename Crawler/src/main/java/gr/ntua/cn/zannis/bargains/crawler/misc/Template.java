package gr.ntua.cn.zannis.bargains.crawler.misc;

import org.glassfish.jersey.uri.UriTemplate;

import javax.ws.rs.core.UriBuilder;

/**
 * A class containing static {@link org.glassfish.jersey.uri.UriTemplate} fields
 * for the requests to the Skroutz REST API.
 * @author zannis <zannis.kal@gmail.com
 */
public class Template {

    public static final UriBuilder API_URI = UriBuilder.fromUri("https://api.skroutz.gr");
    public static final UriTemplate SINGLE_PRODUCT = new UriTemplate("/products/{id}");
    public static final UriTemplate SHOP_TEMPLATE = new UriTemplate("/shops/{id}");
}
