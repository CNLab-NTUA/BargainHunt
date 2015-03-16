package gr.ntua.cn.zannis.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class Utils {

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * Gets key-value pairs from given properties file.
     * @return A Properties object with all the read values or null.
     */
    public static Properties getPropertiesFromFile(String propFileName) {

        Properties properties = new Properties();
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(Misc.DOTFOLDER.toString(), propFileName));
            properties.load(stream);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
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
            stream = new FileOutputStream(new File(Misc.DOTFOLDER.toString(), propFileName));
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
     * @return The access token or empty string if an error occured.
     * @throws IOException Exception thrown if there are problems accessing TOKEN_FILENAME.
     */
    public static String getAccessToken() throws IOException {
        log.debug("Getting access token...");
        Properties tokenProperties = getPropertiesFromFile(Misc.TOKEN_FILENAME);
        if (tokenProperties == null) {
            log.error("File " + Misc.TOKEN_FILENAME + " not found.");
        } else if (tokenProperties.containsKey("access_token")) {
            log.debug("Access token found!");
            return tokenProperties.getProperty("access_token");
        } else {
            log.debug("Access token empty!");
        }
        return "";
    }

    /**
     * This method checks if the required properties files exist, and if not
     * creates them.
     */
    public static void initPropertiesFiles() throws IOException {
        log.debug("Initiating properties files...");
        if (!Files.isDirectory(Misc.DOTFOLDER)) {
            Files.createDirectory(Misc.DOTFOLDER);
            log.debug("Created .BargainCrawler directory.");
        }
        if (!Files.isWritable(Misc.CONFIG_PATH)) {
            Files.createFile(Misc.CONFIG_PATH);
            log.debug("Created " + Misc.CONFIG_FILENAME);
        }
        if (!Files.isWritable(Misc.TOKEN_PATH)) {
            Files.createFile(Misc.TOKEN_PATH);
            log.debug("Created " + Misc.TOKEN_FILENAME);
        }
    }
}
