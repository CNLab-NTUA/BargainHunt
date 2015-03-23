package gr.ntua.cn.zannis.bargains.client.misc;

import gr.ntua.cn.zannis.bargains.client.dto.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

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
    public static String getAccessToken() {
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
    public static TokenResponse requestAccessToken() {
        log.debug("Requesting access token...");

        Properties config = Utils.getPropertiesFromFile(Const.CONFIG_FILENAME);

        if (config == null) {
            log.debug("Config file empty, please get a proper client_id and client_secret.");
            return null;
        } else {
            Client client = ClientBuilder.newClient();
            UriBuilder builder = UriBuilder.fromUri("https://www.skroutz.gr").path("oauth2/token")
                    .queryParam("client_id", config.getProperty("client_id"))
                    .queryParam("client_secret", config.getProperty("client_secret"))
                    .queryParam("grant_type", "client_credentials")
                    .queryParam("redirect_uri", config.getProperty("redirect_uri"))
                    .queryParam("scope", "public");

            return client.target(builder).request()
                    .post(Entity.entity(new TokenResponse(), MediaType.APPLICATION_JSON_TYPE), TokenResponse.class);
        }
    }
}
