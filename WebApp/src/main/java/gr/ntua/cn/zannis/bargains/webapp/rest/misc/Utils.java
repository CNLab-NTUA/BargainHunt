package gr.ntua.cn.zannis.bargains.webapp.rest.misc;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.*;
import gr.ntua.cn.zannis.bargains.webapp.rest.requests.filters.Filter;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.RestResponse;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;

import static gr.ntua.cn.zannis.bargains.webapp.rest.misc.Const.*;


/**
 * General utilities class for use in the entire project.
 * @author zannis <zannis.kal@gmail.com>
 */
public class Utils {

    private final static Logger log = LoggerFactory.getLogger(Utils.class);
    private final static Map<Class<? extends SkroutzEntity>, String> PATH_MAP;
    private final static Map<Class<? extends SkroutzEntity>, Class> RESPONSE_MAP;

    static {
        HashMap<Class<? extends SkroutzEntity>, String> pathMap = new HashMap<>();
        pathMap.put(Product.class, PRODUCTS);
        pathMap.put(Shop.class, SHOPS);
        pathMap.put(Category.class, CATEGORIES);
        pathMap.put(Sku.class, SKUS);
        pathMap.put(Manufacturer.class, MANUFACTURERS);
        PATH_MAP = Collections.unmodifiableMap(pathMap);

        HashMap<Class<? extends SkroutzEntity>, Class<? extends RestResponse<?>>> responseMap = new HashMap<>();
        responseMap.put(Product.class, ProductResponse.class);
        responseMap.put(Shop.class, ShopResponse.class);
        responseMap.put(Category.class, CategoryResponse.class);
        responseMap.put(Sku.class, SkuResponse.class);
        responseMap.put(Manufacturer.class, ManufacturerResponse.class);
        RESPONSE_MAP = Collections.unmodifiableMap(responseMap);
    }

    /**
     * Gets key-value pairs from given properties file.
     * @param propFileName The filename to look for.
     * @return A Properties object with all the read values or null.
     */
    public static Properties getPropertiesFromFile(String propFileName) {

        Properties properties = null;
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(DOTFOLDER.toString(), propFileName));
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
            stream = new FileOutputStream(new File(DOTFOLDER.toString(), propFileName));
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
        String property = null;
        Properties tokenProperties = getPropertiesFromFile(TOKEN_FILENAME);

        if (tokenProperties == null) {
            log.error(TOKEN_FILENAME + " does not exist.");
        } else if (tokenProperties.containsKey("access_token")) {
            String tokenProperty = tokenProperties.getProperty("access_token");
            property = !tokenProperty.isEmpty() ? tokenProperty : null;
            if (property == null) {
                log.debug("Access token empty! :(");
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
    public static void initClientPropertyFiles() throws IOException {
        if (!Files.isDirectory(DOTFOLDER)) {
            log.debug("First time running!");
            Files.createDirectory(DOTFOLDER);
            log.debug("Created directory " + DOTFOLDER + ".");
        }
        if (!Files.isWritable(CONFIG_PATH)) {
            Files.createFile(CONFIG_PATH);
            log.debug("Created file " + CONFIG_PATH + ".");
        }
        if (!Files.isWritable(TOKEN_PATH)) {
            Files.createFile(TOKEN_PATH);
            log.debug("Created file " + TOKEN_PATH + ".");
        }
        log.debug("Properties files initialized successfully!");
    }

    /**
     * Attempts to get a new OAuth 2.0 access token and writes it to the config file.
     * @return True on success, false on fail.
     */
    public static String requestAccessToken() throws IOException {
        log.debug("Requesting access token...");

        Properties config = Utils.getPropertiesFromFile(Const.CONFIG_FILENAME);

        if (config == null) {
            throw new IOException("Config file empty, please get a proper client_id and client_secret.");
        } else {
            UriBuilder builder = UriBuilder.fromUri("https://www.skroutz.gr").path("oauth2/token")
                    .queryParam("client_id", config.getProperty("client_id"))
                    .queryParam("client_secret", config.getProperty("client_secret"))
                    .queryParam("grant_type", "client_credentials")
                    .queryParam("redirect_uri", config.getProperty("redirect_uri"))
                    .queryParam("scope", "public");
            try {
                AccessTokenResponse response = ClientBuilder.newClient().target(builder).request()
                        .post(null, AccessTokenResponse.class);
                return response.getToken();
            } catch (ProcessingException e) {
                log.error("Error requesting a new access token", e);
                return null;
            }
        }
    }

    /**
     * Method that matches a {@link SkroutzEntity} to its corresponding {@link RestResponseImpl}
     * @param tClass The {@link SkroutzEntity} class type.
     * @param <T>    The actual {@link SkroutzEntity}.
     * @return The matching {@link RestResponseImpl}.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SkroutzEntity> Class<? extends RestResponse<T>> getMatchingResponse(Class<T> tClass) {
        return RESPONSE_MAP.get(tClass);
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
     * Returns the REST API target uri for all the results from the given class.
     * @param tClass The class type to look for.
     * @param <T> One of the {@link SkroutzEntity} class types.
     * @return The correct uri.
     */
    public static <T extends SkroutzEntity> URI getMatchingUri(Class<T> tClass) {
        UriBuilder builder = UriBuilder.fromUri(API_HOST)
                .path(PATH_MAP.get(tClass));
        return builder.build();
    }

    /**
     * Returns the REST API target uri for all the results from the given class with the given filters.
     * @param tClass The class type to look for.
     * @param filters An array of filters to apply.
     * @param <T> One of the {@link SkroutzEntity} class types.
     * @return The correct uri.
     */
    public static <T extends SkroutzEntity> URI getMatchingUri(Class<T> tClass, Filter... filters) {
        UriBuilder builder = UriBuilder.fromUri(API_HOST)
                .path(PATH_MAP.get(tClass));
        for (Filter f : filters) {
            builder.queryParam(f.getName(), f.getValue());
        }
        return builder.build();
    }

    /**
     * Returns the REST API target uri for a specific result from the given class.
     * @param tClass The class type to look for.
     * @param id The item's <code>skroutzId</code>.
     * @param <T> One of the {@link SkroutzEntity} class types.
     * @return The correct uri.
     */
    public static <T extends SkroutzEntity> URI getMatchingUri(Class<T> tClass, Integer id) {
        UriBuilder builder = UriBuilder.fromUri(API_HOST)
                .path(PATH_MAP.get(tClass))
                .path(ID);
        return builder.build(id);
    }

    /**
     * Returns the REST API target uri for a specific result from the given class using the specified filters.
     * @param tClass The class type to look for.
     * @param id The item's <code>skroutzId</code>.
     * @param filters An array of filters to apply.
     * @param <T> One of the {@link SkroutzEntity} class types.
     * @return The correct uri.
     */
    public static <T extends SkroutzEntity> URI getMatchingUri(Class<T> tClass, Integer id, Filter... filters) {
        UriBuilder builder = UriBuilder.fromUri(API_HOST)
                .path(PATH_MAP.get(tClass))
                .path(ID);
        for (Filter f : filters) {
            builder.queryParam(f.getName(), f.getValue());
        }
        return builder.build(id);
    }

    /**
     * Returns the REST API target uri for all the results from the given category that belong to <code>parent</code>.
     * @param parent The parent entity.
     * @param childClass The children class type.
     * @param <T> One of the {@link SkroutzEntity} class types.
     * @param <U> Another one of the {@link SkroutzEntity} class types.
     * @return The correct uri.
     */
    public static <T extends SkroutzEntity, U extends SkroutzEntity> URI getMatchingUri(U parent, Class<T> childClass) {
        UriBuilder builder = UriBuilder.fromUri(API_HOST)
                .path(PATH_MAP.get(parent.getClass()))
                .path(ID)
                .path(PATH_MAP.get(childClass));
        return builder.build(parent.getSkroutzId());
    }

    /**
     * Returns the REST API target uri for all the results from the given category that belong to <code>parent</code>
     * using the specified filters.
     * @param parent The parent entity.
     * @param childClass The children class type.
     * @param filters An array of filters to apply.
     * @param <T> One of the {@link SkroutzEntity} class types.
     * @param <U> Another one of the {@link SkroutzEntity} class types.
     * @return The correct uri.
     */
    public static <T extends SkroutzEntity, U extends SkroutzEntity> URI getMatchingUri(U parent, Class<T> childClass, Filter... filters) {
        UriBuilder builder = UriBuilder.fromUri(API_HOST)
                .path(PATH_MAP.get(parent.getClass()))
                .path(ID)
                .path(PATH_MAP.get(childClass));
        for (Filter f : filters) {
            builder.queryParam(f.getName(), f.getValue());
        }
        return builder.build(parent.getSkroutzId());
    }

    /**
     * Returns the REST API target uri for a specific entity.
     * @param entity The entity to retrieve.
     * @param <T> One of the {@link SkroutzEntity} class types.
     * @return The correct uri.
     */
    public static <T extends SkroutzEntity> URI getMatchingUri(T entity) {
        UriBuilder builder = UriBuilder.fromUri(API_HOST)
                .path(PATH_MAP.get(entity.getClass()))
                .path(ID);
        return builder.build(entity.getSkroutzId());
    }

    /**
     * Method to extract the path including the queries from a {@link URI}.
     *
     * @param requestUri The uri to parse
     * @return The full path after the authority.
     */
    public static String getFullPathFromUri(URI requestUri) {
        StringBuilder sb = new StringBuilder(requestUri.getPath());
        if (requestUri.getQuery() != null) {
            sb.append("?").append(requestUri.getQuery());
        }
        return sb.toString();
    }


    /**
     * Extract query parameters from a given URL
     *
     * @param url The url as a string
     * @return A HashMap with parameters and values
     */
    public static Map<String, List<String>> getQueryParams(String url) {
        try {
            Map<String, List<String>> params = new HashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }

                    List<String> values = params.computeIfAbsent(key, k -> new ArrayList<>());
                    values.add(value);
                }
            }

            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }
}
