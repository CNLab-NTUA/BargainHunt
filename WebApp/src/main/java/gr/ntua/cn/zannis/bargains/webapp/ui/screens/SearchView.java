package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.MouseEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.ResponsiveGridLayout;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles.CategoryTile;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles.ManufacturerTile;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles.ShopTile;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles.SkuTile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@CDIView(SearchView.NAME)
public class SearchView extends VerticalLayout implements View, MouseEvents.ClickListener {

    public static final String NAME = "search";
    private static final long serialVersionUID = -2974526790845822198L;
    private final VerticalLayout strongMatchesLayout = new VerticalLayout();
    private final VerticalLayout categoryLayout = new VerticalLayout();
    private String query;
    private SearchResults searchResults;

    public SearchView() {
        buildUI();
    }

    private void buildUI() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        query = parseParameters(event.getParameters());
        if (query == null || query.isEmpty()) {
            Notifier.error("Πρέπει να κάνετε κάποια αναζήτηση", true);
        } else if (query.length() < 3) {
            Notifier.error("Πρέπει να εισάγετε από 3 χαρακτήρες και πάνω για να γίνει αναζήτηση.", true);
        } else {
            try {
                searchResults = SkroutzClient.getInstance().search(query);
                // render results and persist them if needed
                if (searchResults != null) {
                    if (searchResults.hasStrongMatches()) {
                        renderStrongMatches(searchResults.getStrongMatches());
                        ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMergeStrongMatches(searchResults.getStrongMatches());
                    }
                    renderCategoryLayout(searchResults.getCategories()); // currently fetches only the first page of results
                    ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(Category.class, searchResults.getCategories());

                }
            } catch (UnsupportedEncodingException e) {
                Notifier.error("Η αναζήτησή σας δεν είναι δυνατή στη συγκεκριμένη κωδικοποίηση.", e);
            }
        }
    }

    private void renderStrongMatches(Meta.StrongMatches strongMatches) {
        Label label = new Label("Μήπως ψάχνετε...");
        ResponsiveGridLayout grid = new ResponsiveGridLayout();
        List<Panel> panels = new ArrayList<>();
        if (strongMatches.hasSku()) {
            panels.add(new SkuTile(strongMatches.getSku()));
        }
        if (strongMatches.hasCategory()) {
            panels.add(new CategoryTile(strongMatches.getCategory()));
        }
        if (strongMatches.hasShop()) {
            panels.add(new ShopTile(strongMatches.getShop()));
        }
        if (strongMatches.hasManufacturer()) {
            panels.add(new ManufacturerTile(strongMatches.getManufacturer()));
        }
        if (panels.size() != 0) {
            panels.forEach(grid::addComponent);
            strongMatchesLayout.addComponent(label);
            strongMatchesLayout.addComponent(grid);
            addComponent(strongMatchesLayout);
        }
    }

    private void renderCategoryLayout(List<Category> categories) {
        Label label = new Label("Βρέθηκαν προϊόντα στις παρακάτω κατηγορίες :");
        label.setStyleName(ValoTheme.LABEL_H4);
        ResponsiveGridLayout grid = new ResponsiveGridLayout();
        for (Category c : categories) {
            CategoryTile tile = new CategoryTile(c);
            tile.addClickListener(this);
            grid.addComponent(tile);
        }
        categoryLayout.addComponent(label);
        categoryLayout.addComponent(grid);

        addComponent(categoryLayout);
    }

    private String parseParameters(String parameters) {
        String[] splitParameters = parameters.split("/");
        if (splitParameters.length > 0) {
            try {
                return URLDecoder.decode(splitParameters[0], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void click(MouseEvents.ClickEvent clickEvent) {
        String uri = null;
        if (clickEvent.getComponent().getClass().isAssignableFrom(CategoryTile.class)) {
            uri = ProductsView.NAME + "/"
                    + ((CategoryTile) clickEvent.getComponent()).getEntity().getSkroutzId() + "/";
            try {
                uri = uri.concat(URLEncoder.encode(query, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                uri = uri.concat(query);
            }
        } else if (clickEvent.getComponent().getClass().isAssignableFrom(SkuTile.class)) {
            uri = BargainView.NAME + "/"
                    + ((SkuTile) clickEvent.getComponent()).getEntity().getSkroutzId();
        } else if (clickEvent.getComponent().getClass().isAssignableFrom(ShopTile.class)) {
            uri = ShopView.NAME + "/"
                    + ((ShopTile) clickEvent.getComponent()).getEntity().getSkroutzId();
        } else if (clickEvent.getComponent().getClass().isAssignableFrom(ManufacturerTile.class)) {
            uri = ManufacturerView.NAME + "/" +
                    ((ManufacturerTile) clickEvent.getComponent()).getEntity().getSkroutzId();
        }
        if (uri != null) {
            getUI().getNavigator().navigateTo(uri);
        }
    }
}
