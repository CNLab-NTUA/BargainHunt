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
import gr.ntua.cn.zannis.bargains.webapp.views.SearchView;

import javax.servlet.annotation.WebServlet;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 */
@Theme("mytheme")
@Widgetset("gr.ntua.cn.zannis.bargains.webapp.BargainHuntWidgetset")
public class BargainHuntUI extends UI {

    private Navigator navigator;
    public static ResourceBundle MESSAGES = ResourceBundle.getBundle("gr.ntua.cn.zannis.bargains.webapp.i18n.Messages", Locale.getDefault());

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("BargainHunt");

        initNavigator();

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        setContent(layout);

        navigator.navigateTo("");

    }

    private void initNavigator() {
        navigator = new Navigator(this, this);
        navigator.addView("", new SearchView());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = BargainHuntUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    @Override
    public Navigator getNavigator() {
        return navigator;
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
