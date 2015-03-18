package gr.ntua.cn.zannis.bargains.crawler.impl;

import gr.ntua.cn.zannis.bargains.crawler.RestClient;
import gr.ntua.cn.zannis.bargains.crawler.dto.TokenResponse;
import gr.ntua.cn.zannis.bargains.crawler.misc.RestRequest;
import gr.ntua.cn.zannis.bargains.crawler.misc.Utils;
import gr.ntua.cn.zannis.bargains.entities.Category;
import gr.ntua.cn.zannis.bargains.entities.Product;
import gr.ntua.cn.zannis.bargains.entities.Shop;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Crawler implementation for Bargain hunting application.
 * @author zannis <zannis.kal@gmail.com>
 */
public class SkroutzRestClient implements RestClient {
    private static final Logger log = LoggerFactory.getLogger(SkroutzRestClient.class);
    private String token;
    private Map<String, String> args;
    private UriTemplate template;
    private WebTarget target;

    public SkroutzRestClient() {
        log.debug("SkroutzClient started.");
    }

    public static void main(String[] args) {
        String token;
        try {
            Utils.initPropertiesFiles();
            token = Utils.getAccessToken();
            if (token == null) {
                TokenResponse response = Utils.requestAccessToken();
            }

            Client client = ClientBuilder.newClient();
            System.out.println(client.target(RestRequest.product(12176638)).request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Bearer " + token)
                    .accept("application/vnd.skroutz+json; version=3").get(String.class));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Product getProduct(Integer productId) {
        return null;
    }

    @Override
    public Product getProduct(String productName) {
        return null;
    }

    @Override
    public Shop getShop(Integer shopId) {
        return null;
    }

    @Override
    public Shop getShop(String shopName) {
        return null;
    }

    @Override
    public Category getCategory(Integer categoryId) {
        return null;
    }

    @Override
    public Category getCategory(String categoryName) {
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return null;
    }
}
