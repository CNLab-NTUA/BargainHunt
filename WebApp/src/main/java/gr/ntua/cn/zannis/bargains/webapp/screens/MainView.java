package gr.ntua.cn.zannis.bargains.webapp.screens;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.webapp.components.SearchField;

/**
 * A minimal main view for the BargainHunt application.
 * @author zannis <zannis.kal@gmail.com>
 */
public class MainView extends VerticalLayout implements View {

    public static final String NAME = "";

    public MainView() {
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setMargin(true);
        // build logo
        Label logo = new Label("BargainHunt");
        logo.setSizeUndefined();
        logo.setStyleName(ValoTheme.LABEL_HUGE);
        // build searchbar
        SearchField searchField = new SearchField();


        // add to ui
        addComponent(logo);
        setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        addComponent(searchField);
        setComponentAlignment(searchField, Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        viewChangeEvent.getNewView();
    }
}
