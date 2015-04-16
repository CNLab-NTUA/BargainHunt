package gr.ntua.cn.zannis.bargains.client.misc;

import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.*;
import gr.ntua.cn.zannis.bargains.client.responses.RestResponse;
import gr.ntua.cn.zannis.bargains.client.responses.impl.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static gr.ntua.cn.zannis.bargains.client.misc.Const.*;


/**
 * General utilities class for use in the entire project.
 * @author zannis <zannis.kal@gmail.com>
 */
public class Utils {

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * Gets key-value pairs from given properties file.
     * @param propFileName The filename to look for.
     * @return A Properties object with all the read values or null.
     */
    public static Properties getPropertiesFromFile(String propFileName) {

        Properties properties = null;
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(Const.DOTFOLDER.toString(), propFileName));
            properties = new Properties();
            properties.load(stream);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (properties == null) {
                log.debug(propFileName + " is empty. Returning null.");
            } else if (properties.isEmpty()) {
                log.debug(propFileName + " is empty. Returning null.");
                properties = null;
            }
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return properties;
    }

    /**
     * Gets a properties object and attempts to save it in the config file.
     * @param props The properties object to save.
     * @param propFileName The filename to save at.
     * @return True on success, false on fail.
     */
    public static boolean savePropertiesToFile(Properties props, String propFileName) {
        boolean result = false;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(Const.DOTFOLDER.toString(), propFileName));
            props.store(stream, null);
            result = true;
        } catch (FileNotFoundException e) {
            log.error("File " + propFileName + " not found.");
        } catch (IOException e) {
            log.error("IO error: " + e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * Attempts to find the access token in the TOKEN_FILENAME variable.
     * @return The access token or null if an error occured.
     */
    public static String getLocalAccessToken() {
        log.debug("Getting access token...");
        String property = null;
        Properties tokenProperties = getPropertiesFromFile(Const.TOKEN_FILENAME);

        if (tokenProperties == null) {
            log.error("Error accessing " + Const.TOKEN_FILENAME + ".");
        } else if (tokenProperties.containsKey("access_token")) {
            property = tokenProperties.getProperty("access_token");
            if (property == null) {
                log.debug("Access token empty! Maybe request a new one?");
            } else {
                log.debug("Access token found!");
            }
        }
        return property;
    }

    /**
     * This method checks if the required properties files exist, and if not
     * creates them.
     */
    public static void initPropertiesFiles() throws IOException {
        log.debug("Initializing properties files...");
        if (!Files.isDirectory(Const.DOTFOLDER)) {
            Files.createDirectory(Const.DOTFOLDER);
            log.debug("Created .BargainCrawler directory.");
        }
        if (!Files.isWritable(Const.CONFIG_PATH)) {
            Files.createFile(Const.CONFIG_PATH);
            log.debug("Created " + Const.CONFIG_FILENAME);
        }
        if (!Files.isWritable(Const.TOKEN_PATH)) {
            Files.createFile(Const.TOKEN_PATH);
            log.debug("Created " + Const.TOKEN_FILENAME);
        }
        log.debug("Properties files initialized successfully!");
    }

    /**
     * Attempts to get a new OAuth 2.0 access token and writes it to the config file.
     * @return True on success, false on fail.
     */
    public static TokenResponse requestAccessToken() throws AuthenticationException {
        log.debug("Requesting access token...");

        Properties config = Utils.getPropertiesFromFile(Const.CONFIG_FILENAME);

        if (config == null) {
            throw new AuthenticationException("Config file empty, please get a proper client_id and client_secret.");
        } else {
            UriBuilder builder = UriBuilder.fromUri("https://www.skroutz.gr").path("oauth2/token")
                    .queryParam("client_id", config.getProperty("client_id"))
                    .queryParam("client_secret", config.getProperty("client_secret"))
                    .queryParam("grant_type", "client_credentials")
                    .queryParam("redirect_uri", config.getProperty("redirect_uri"))
                    .queryParam("scope", "public");

            return ClientBuilder.newClient().target(builder).request()
                    .post(Entity.entity(new TokenResponse(), MediaType.APPLICATION_JSON_TYPE), TokenResponse.class);
        }
    }

    // client
    /**
     * Method that matches a {@link SkroutzEntity} to its corresponding {@link RestResponseImpl}
     * @param tClass The {@link SkroutzEntity} class type.
     * @param <T>    The actual {@link SkroutzEntity}.
     * @return The matching {@link RestResponseImpl}.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SkroutzEntity> Class<? extends RestResponse<T>> getMatchingResponse(Class<T> tClass) {
        if (tClass.equals(Product.class)) {
            return (Class<? extends RestResponse<T>>) ProductResponse.class;
        } else if (tClass.equals(Shop.class)) {
            return (Class<? extends RestResponse<T>>) ShopResponse.class;
        } else if (tClass.equals(Category.class)) {
            return (Class<? extends RestResponse<T>>) CategoryResponse.class;
        } else if (tClass.equals(Sku.class)) {
            return (Class<? extends RestResponse<T>>) SkuResponse.class;
        } else if (tClass.equals(Manufacturer.class)) {
            return (Class<? extends RestResponse<T>>) ManufacturerResponse.class;
        } else {
            return null;
        }
    }

    /**
     * Method that matches a {@link SkroutzEntity} object to its corresponding {@link RestResponseImpl}
     * @param entity The {@link SkroutzEntity} object.
     * @param <T>    The actual {@link SkroutzEntity}.
     * @return The matching {@link RestResponseImpl}.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SkroutzEntity> Class<? extends RestResponse<T>> getMatchingResponse(T entity) {
        return getMatchingResponse((Class<T>) entity.getClass());
    }

    /**
     * Helper function to extract Link headers from a HTTP response.
     * @param response The response to extract headers from.
     * @return A map of Link urls, empty if no {@link Link} is found.
     */
    public static Map<String, URI> getLinks(Response response) {
        Map<String, URI> map = new HashMap<>();
        // custom made parser to parse concatenated link headers cause Jersey can't do it
        String linkString = response.getHeaderString(HttpHeaders.LINK);
        if (linkString == null) {
            return map;
        } else {
            String[] links = linkString.split(",");
            for (String s : links) {
                String url = StringUtils.substringBetween(s, "<", ">");
                String tag = StringUtils.substringBetween(s, "\"");
                map.put(tag, URI.create(url));
            }
        }
        return map;
    }

    /**
     * Initializes a {@link HashMap<Class, String>} that maps classes to paths
     * in the REST API server.
     *
     * @return The HashMap containing the results.
     */
    public static HashMap<Class, String> initPathMap() {
        HashMap<Class, String> map = new HashMap<>();
        map.put(Product.class, PRODUCTS);
        map.put(Shop.class, SHOPS);
        map.put(Category.class, CATEGORIES);
        map.put(Sku.class, SKUS);
        map.put(Manufacturer.class, MANUFACTURERS);
        return map;
    }
}
