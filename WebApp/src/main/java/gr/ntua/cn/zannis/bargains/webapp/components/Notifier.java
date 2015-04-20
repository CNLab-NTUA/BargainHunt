package gr.ntua.cn.zannis.bargains.webapp.components;

import com.vaadin.ui.Notification;

/**
 * Custom class to show notifications in Vaadin.
 * @author zannis <zannis.kal@gmail.com>
 */
public class Notifier {

    public static void info(String message) {
        Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
    }

    public static void warn(String message) {
        Notification.show(message, Notification.Type.WARNING_MESSAGE);

    }

    public static void error(String message, Throwable throwable) {
        Notification.show(message, Notification.Type.ERROR_MESSAGE);
        throwable.printStackTrace();
    }
}
