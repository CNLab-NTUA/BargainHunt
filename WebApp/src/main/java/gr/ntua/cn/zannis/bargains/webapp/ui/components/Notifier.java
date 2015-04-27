package gr.ntua.cn.zannis.bargains.webapp.ui.components;

import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to show notifications in Vaadin. Wraps {@link Notification} to add logging when needed.
 * @author zannis <zannis.kal@gmail.com>
 */
public class Notifier {

    public static final Logger log = LoggerFactory.getLogger(Notifier.class.getSimpleName());

    public static void info(String message) {
        Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
    }

    public static void warn(String message) {
        Notification.show(message, Notification.Type.WARNING_MESSAGE);

    }

    public static void error(String message, Throwable throwable) {
        Notification.show(message, Notification.Type.ERROR_MESSAGE);
        log.error(message, throwable);
    }

    public static void error(String message, boolean addToServerLog) {
        Notification.show(message, Notification.Type.ERROR_MESSAGE);
        if (addToServerLog) {
            log.error(message);
        }
    }
}
