package gr.ntua.cn.zannis.bargains.webapp;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import gr.ntua.cn.zannis.bargains.webapp.screens.BargainView;
import gr.ntua.cn.zannis.bargains.webapp.screens.MainView;
import gr.ntua.cn.zannis.bargains.webapp.screens.ProductsView;
import gr.ntua.cn.zannis.bargains.webapp.screens.SearchView;

import javax.servlet.annotation.WebServlet;
import java.util.Locale;

/**
 *
 */
@Theme("bargainhunt")
@Widgetset("gr.ntua.cn.zannis.bargains.webapp.BargainHuntWidgetset")
public class BargainHuntUI extends UI {

    /* JPA Persistence Unit name */
    public static final String PERSISTENCE_UNIT = "bargainhunt";

    /* Internationalization
    ResourceBundle i18nBundle;
    public static ResourceBundle MESSAGES = ResourceBundle.getBundle(, Locale.getDefault()); */
    private Navigator navigator;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());

//        final ResourceBundle i18n = ResourceBundle.getBundle("gr.ntua.cn.zannis.bargains.webapp.i18n.Messages", getLocale());

        getPage().setTitle("BargainHunt");

        initNavigator();

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        setContent(layout);

        navigator.navigateTo(getNavigator().getState());

    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
//        i18nBundle = ResourceBundle.getBundle("gr.ntua.cn.zannis.bargains.webapp.i18n.Messages", getLocale());
    }

    private void initNavigator() {
        navigator = new Navigator(this, this);
        navigator.addView(MainView.NAME, MainView.class);
        navigator.addView(SearchView.NAME, SearchView.class);
        navigator.addView(ProductsView.NAME, ProductsView.class);
        navigator.addView(BargainView.NAME, BargainView.class);
    }

    @Override
    public Navigator getNavigator() {
        return navigator;
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = BargainHuntUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}