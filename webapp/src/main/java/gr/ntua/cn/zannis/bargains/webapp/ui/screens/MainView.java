package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Offer;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.SearchField;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles.OfferTile;

import java.util.ArrayList;
import java.util.List;

/**
 * A minimal main view for the BargainHunt application.
 * @author zannis <zannis.kal@gmail.com>
 */
@CDIView(MainView.NAME)
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
        // build latest offers
        Label offerLogo = new Label("Οι τελευταίες προσφορές");
        offerLogo.setSizeUndefined();
        offerLogo.setStyleName(ValoTheme.LABEL_LARGE);
        HorizontalLayout offerBar = new HorizontalLayout();

        // hardcoded categories because why not
        // getting cellphones, tablets, tvs -> 40, 1105, 12
        ArrayList<Offer> offers = new ArrayList<>();
        offers.addAll(((BargainHuntUI) UI.getCurrent()).getOfferEm().getTopActive(2, 40));
        offers.addAll(((BargainHuntUI) UI.getCurrent()).getOfferEm().getTopActive(2, 1105));
        offers.addAll(((BargainHuntUI) UI.getCurrent()).getOfferEm().getTopActive(1, 12));
        for (int i = 0; i < offers.size(); i++) {
            OfferTile offerTile;
            if (offers.size() > i) {
                offerTile = new OfferTile(offers.get(i));
            } else {
                offerTile = new OfferTile(new Offer());
            }
            offerBar.addComponent(offerTile);
        }

        // add to ui
        addComponent(logo);
        setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        addComponent(searchField);
        setComponentAlignment(searchField, Alignment.MIDDLE_CENTER);
        addComponent(offerLogo);
        setComponentAlignment(offerLogo, Alignment.MIDDLE_CENTER);
        addComponent(offerBar);
        setComponentAlignment(offerBar, Alignment.MIDDLE_CENTER);
        setExpandRatio(logo, .1f);
        setExpandRatio(searchField, .2f);
        setExpandRatio(offerLogo, .05f);
        setExpandRatio(offerBar, .65f);
        offerBar.setMargin(new MarginInfo(true));
        offerBar.setSpacing(true);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        viewChangeEvent.getNewView();
    }
}
