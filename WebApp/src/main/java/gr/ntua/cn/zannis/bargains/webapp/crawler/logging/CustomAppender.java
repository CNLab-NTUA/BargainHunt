package gr.ntua.cn.zannis.bargains.webapp.crawler.logging;

/**
 * @author zannis <zannis.kal@gmail.com>
 */

import com.vaadin.ui.TextArea;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class CustomAppender extends WriterAppender {

    public static TextArea logTextArea;

    @Override
    public void append(LoggingEvent loggingEvent) {

        final String logMessage = this.layout.format(loggingEvent);

        logTextArea.setValue(logTextArea.getValue() + logMessage);
    }

}