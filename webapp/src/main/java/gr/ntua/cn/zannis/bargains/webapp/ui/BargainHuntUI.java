package gr.ntua.cn.zannis.bargains.webapp.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;
import gr.ntua.cn.zannis.bargains.webapp.ejb.OfferEntityManager;
import gr.ntua.cn.zannis.bargains.webapp.ejb.SkroutzEntityManager;
import gr.ntua.cn.zannis.bargains.webapp.ejb.dao.RequestDAO;
import gr.ntua.cn.zannis.bargains.webapp.ejb.dao.impl.RequestDAOImpl;
import gr.ntua.cn.zannis.bargains.webapp.ui.screens.*;

import javax.inject.Inject;
import java.util.Locale;

/**
 * Main UI class for BargainHunt. Initializes the {@link Navigator} and the EJB classes
 * we are using inside the project.
 */
@CDIUI("")
@Push(PushMode.MANUAL)
@Theme("bargainhunt")
//@Widgetset("AppWidgetset")
public class BargainHuntUI extends UI {

    @Inject
    SkroutzEntityManager skroutzEm;

    @Inject
    OfferEntityManager offerEm;

    @Inject
    RequestDAOImpl requests;

    @Inject
    CDIViewProvider viewProvider;

    private Navigator navigator;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());

        getPage().setTitle("BargainHunt");
        initNavigator();
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
    }

    private void initNavigator() {
        navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);
        navigator.addView(MainView.NAME, MainView.class);
        navigator.addView(SearchView.NAME, SearchView.class);
        navigator.addView(ProductsView.NAME, ProductsView.class);
        navigator.addView(BargainView.NAME, BargainView.class);
        navigator.addView(CrawlerView.NAME, CrawlerView.class);
        navigator.navigateTo(getNavigator().getState());
    }

    @Override
    public Navigator getNavigator() {
        return navigator;
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public SkroutzEntityManager getSkroutzEm() {
        return skroutzEm;
    }

    public OfferEntityManager getOfferEm() {
        return offerEm;
    }

    public RequestDAO getRequests() {
        return requests;
    }

}
