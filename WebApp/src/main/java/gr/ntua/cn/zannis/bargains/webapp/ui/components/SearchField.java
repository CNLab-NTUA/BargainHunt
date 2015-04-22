package gr.ntua.cn.zannis.bargains.webapp.ui.components;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.webapp.ui.screens.SearchView;
import org.vaadin.jouni.restrain.Restrain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Search Field implementation using Vaadin ActionButtonTextField Addon.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class SearchField extends HorizontalLayout {

    private TextField queryField = new TextField();
    private Button searchButton = new Button();

//    private ResourceBundle messages;

    public SearchField() {
//        buildI18n();
        buildTextField();
        buildButton();
        new Restrain(this).setMinWidth("200px").setMaxWidth("400px");
    }

    private void buildButton() {
        searchButton.setIcon(FontAwesome.SEARCH, "Αναζήτηση");
        searchButton.addClickListener(clickEvent -> search(queryField.getValue()));
        addComponent(searchButton);
    }

    private void buildI18n() {
//        messages = ResourceBundle.getBundle("gr.ntua.cn.zannis.bargains.webapp.i18n.MessagesBundle", getUI().getLocale());
    }

    private void buildTextField() {
        queryField.setBuffered(true);
        queryField.setNullRepresentation("");
//        searchField.setInputPrompt(messages.getString("searchPrompt"));
        queryField.setInputPrompt("Αναζήτηση...");
        setStyleName(ValoTheme.TEXTFIELD_HUGE);
        queryField.setWidth("300px");
        queryField.addValidator(new StringLengthValidator("Πρέπει να εισάγετε τουλάχιστον 3 χαρακτήρες.", 3, 200, false));
        queryField.setValidationVisible(false);
        queryField.setImmediate(false);
        queryField.addShortcutListener(new ShortcutListener("", ShortcutAction.KeyCode.ENTER, new int[]{}) {
            @Override
            public void handleAction(Object sender, Object target) {
                search(queryField.getValue());
            }
        });
        addComponent(queryField);
        setExpandRatio(queryField, 1);
    }

    private void search(String query) {
        try {
            queryField.validate();
            if (queryField.isValid()) {
                getUI().getNavigator().navigateTo(SearchView.NAME + "/" + URLEncoder.encode(query, "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            Notifier.error("Δεν ήταν δυνατή η αναζήτηση.", e);
        }
    }
}
