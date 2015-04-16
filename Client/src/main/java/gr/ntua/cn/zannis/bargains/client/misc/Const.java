package gr.ntua.cn.zannis.bargains.client.misc;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Miscellaneous strings and other constants.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class Const {

    // general files and paths
    public static final String CONFIG_FILENAME = "config.properties";
    public static final String TOKEN_FILENAME = "token.properties";
    public static final Path DOTFOLDER = Paths.get(System.getProperty("user.home"), ".BargainHunt");
    public static final Path CONFIG_PATH = DOTFOLDER.resolve(Paths.get(CONFIG_FILENAME));
    public static final Path TOKEN_PATH = DOTFOLDER.resolve(Paths.get(TOKEN_FILENAME));

    // uri templates
    public static final String API_HOST = "https://api.skroutz.gr";
    public static final String PRODUCTS = "/products";
    public static final String SHOPS = "/shops";
    public static final String CATEGORIES = "/categories";
    public static final String SKUS = "/skus";
    public static final String MANUFACTURERS = "/manufacturers";
    public static final String ID = "/{id}";
    public static final String SEARCH = "/search";

    // persistence
    public static final String PERSISTENCE_UNIT = "BargainHunt";

}
