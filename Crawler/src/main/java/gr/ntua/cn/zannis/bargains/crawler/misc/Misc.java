package gr.ntua.cn.zannis.bargains.crawler.misc;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Miscellaneous strings and other constants.
 * @author zannis <zannis.kal@gmail.com>
 */
public class Misc {
    public static final String CONFIG_FILENAME = "config.properties";
    public static final String TOKEN_FILENAME = "token.properties";
    public static final Path DOTFOLDER = Paths.get(System.getProperty("user.home"), ".BargainCrawler");
    public static final Path CONFIG_PATH = DOTFOLDER.resolve(Paths.get(CONFIG_FILENAME));
    public static final Path TOKEN_PATH = DOTFOLDER.resolve(Paths.get(TOKEN_FILENAME));
}
