package gr.ntua.cn.zannis.test;

import gr.ntua.cn.zannis.Crawler;
import gr.ntua.cn.zannis.Product;
import gr.ntua.cn.zannis.dto.ClientTokenResponse;
import gr.ntua.cn.zannis.misc.Misc;
import gr.ntua.cn.zannis.misc.Utils;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    /**
     * Attempts to get a new OAuth 2.0 access token and writes it to the config file.
     * @return True on success, false on fail.
     */
    private boolean requestAccessToken() {
        log.debug("Requesting access token.");
        boolean result = false;
        Properties config = Utils.getPropertiesFromFile(Misc.CONFIG_FILENAME);

        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri("https://www.skroutz.gr")
                .path("oauth2/token").queryParam("client_id", config.getProperty("client_id"))
                .queryParam("client_secret", config.getProperty("client_secret"))
                .queryParam("grant_type", "client_credentials")
                .queryParam("redirect_uri", config.getProperty("redirect_uri"))
                .queryParam("scope", "public");

        ClientTokenResponse tokenResponse = client.target(builder).request()
                .post(Entity.entity(new ClientTokenResponse(), MediaType.APPLICATION_JSON_TYPE), ClientTokenResponse.class);

        Properties token = new Properties();
        token.setProperty("access_token", tokenResponse.getAccessToken());
        token.setProperty("token_type", tokenResponse.getTokenType());
        token.setProperty("expires_in", String.valueOf(tokenResponse.getExpiresIn()));

        if (Utils.savePropertiesToFile(token, Misc.TOKEN_FILENAME)) {
            this.token = tokenResponse.getAccessToken();
            result = true;
        }
        if (result) {
            log.debug("New access token acquired!");
        }
        return result;
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
