package gr.ntua.cn.zannis.test;

import gr.ntua.cn.zannis.Crawler;
import gr.ntua.cn.zannis.Product;
import gr.ntua.cn.zannis.misc.Utils;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Crawler implementation for Bargain hunting application.
 * @author zannis <zannis.kal@gmail.com
 */
public class CrawlerImpl implements Crawler {
    private static final Logger log = LoggerFactory.getLogger(Crawler.class);
    private String token;
    private Map<String, String> args;
    private UriTemplate template;
    private WebTarget target;

    public CrawlerImpl() {
        log.debug("BargainCrawler started.");
        try {
            Utils.initPropertiesFiles();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
//        requestAccessToken();
    }

    public static void main(String[] args) {
        new CrawlerImpl();
    }

    private void connect() {}

    @Override
    public Product getProduct(Integer productId) {
        return null;
    }

    @Override
    public Product getProduct(String productName) {
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return null;
    }
}
