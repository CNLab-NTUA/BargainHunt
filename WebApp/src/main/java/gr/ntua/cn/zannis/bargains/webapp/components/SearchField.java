package gr.ntua.cn.zannis.bargains.webapp.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ResourceBundle;

import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;

/**
 * Search Field implementation.
 * @author zannis <zannis.kal@gmail.com>
 */
public class SearchField extends HorizontalLayout {

    private TextField searchField = new TextField();
    private Button searchButton = new Button();
    private ResourceBundle messages;

    public SearchField() {
        buildI18n();
        buildTextField();
        buildButton();
    }

    private void buildI18n() {
        messages = ResourceBundle.getBundle("gr.ntua.cn.zannis.bargains.webapp.i18n.MessagesBundle", getUI().getLocale());
    }

    private void buildTextField() {
        searchField.setBuffered(true);
        searchField.setNullRepresentation("");
        searchField.setInputPrompt(messages.getString("searchPrompt"));
        searchField.addShortcutListener(new Button.ClickShortcut(searchButton, ENTER));
        searchField.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    }

    private void buildButton() {
        searchButton.setIcon(FontAwesome.SEARCH, messages.getString("search"));
        searchButton.addShortcutListener(new Button.ClickShortcut(searchButton, ENTER));
        searchButton.setStyleName(ValoTheme.BUTTON_HUGE);
    }
}
