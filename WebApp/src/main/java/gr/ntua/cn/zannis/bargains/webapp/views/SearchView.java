package gr.ntua.cn.zannis.bargains.webapp.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.webapp.components.SearchField;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class SearchView implements View {

    public SearchView() {
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        // build logo
        Label logo = new Label("BargainHunt");
        logo.setStyleName(ValoTheme.LABEL_HUGE);
        // build searchbar
        SearchField searchField = new SearchField();
        // add to ui
        layout.addComponent(logo);
        layout.addComponent(searchField);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        viewChangeEvent.getNewView();
    }
}
