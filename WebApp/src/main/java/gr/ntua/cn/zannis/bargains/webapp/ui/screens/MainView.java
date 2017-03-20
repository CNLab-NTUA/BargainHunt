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
        HorizontalLayout offerBar = new HorizontalLayout();

        int numberOfOffers = 5;
        List<Offer> offers = ((BargainHuntUI) UI.getCurrent()).getOfferEm().getTopActive(numberOfOffers);
        for (int i = 0; i < numberOfOffers; i++) {
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
        addComponent(offerBar);
        setComponentAlignment(offerBar, Alignment.MIDDLE_CENTER);
        offerBar.setMargin(new MarginInfo(true));
        offerBar.setSpacing(true);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        viewChangeEvent.getNewView();
    }
}
