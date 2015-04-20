package gr.ntua.cn.zannis.bargains.webapp.screens;

import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.components.Notifier;
import gr.ntua.cn.zannis.bargains.webapp.components.tiles.CategoryTile;
import gr.ntua.cn.zannis.bargains.webapp.components.tiles.ManufacturerTile;
import gr.ntua.cn.zannis.bargains.webapp.components.tiles.ShopTile;
import gr.ntua.cn.zannis.bargains.webapp.components.tiles.SkuTile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class SearchView extends VerticalLayout implements View, LayoutEvents.LayoutClickListener {

    public static final String NAME = "search";
    private String query;
    private SearchResults searchResults;

    private VerticalLayout strongMatchesLayout = new VerticalLayout();
    private VerticalLayout categoryLayout = new VerticalLayout();

    public SearchView() {
        buildUI();
    }

    private void buildUI() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        query = parseParameters(event.getParameters());
        if (query == null || query.isEmpty()) {
            Notifier.error("Πρέπει να κάνετε κάποια αναζήτηση", new Exception());
        } else if (query.length() < 3) {
            Notifier.error("Πρέπει να εισάγετε από 3 χαρακτήρες και πάνω για να γίνει αναζήτηση.", new Exception());
        } else {
            searchResults = SkroutzRestClient.get().search(query);

            if (searchResults.hasStrongMatches()) {
                renderStrongMatches(searchResults.getStrongMatches());
            }

            renderCategoryLayout(SkroutzRestClient.get().getAllResultsAsList(searchResults.getPage()));
        }
    }

    private void renderStrongMatches(Meta.StrongMatches strongMatches) {
        Label label = new Label("Μήπως ψάχνετε...");
        GridLayout grid = new GridLayout();
        grid.setRows(1);
        List<VerticalLayout> viewsToAdd = new ArrayList<>();
        if (strongMatches.hasSku()) {
            viewsToAdd.add(new SkuTile(strongMatches.getSku()));
        }
        if (strongMatches.hasCategory()) {
            viewsToAdd.add(new CategoryTile(strongMatches.getCategory()));
        }
        if (strongMatches.hasShop()) {
            viewsToAdd.add(new ShopTile(strongMatches.getShop()));
        }
        if (strongMatches.hasManufacturer()) {
            viewsToAdd.add(new ManufacturerTile(strongMatches.getManufacturer()));
        }
        if (viewsToAdd.size() != 0) {
            grid.setColumns(viewsToAdd.size());

            viewsToAdd.forEach(grid::addComponent);
            strongMatchesLayout.addComponent(label);
            strongMatchesLayout.addComponent(grid);
            addComponent(strongMatchesLayout);
        }
    }

    private void renderCategoryLayout(List<Category> categories) {
        Label label = new Label("Βρέθηκαν προϊόντα στις παρακάτω κατηγορίες :");
        label.setStyleName(ValoTheme.LABEL_H4);
        GridLayout grid = new GridLayout();
        if (categories.size() < 4) {
            grid.setColumns(searchResults.getCategories().size());
        } else {
            grid.setColumns(4);
        }
        grid.setRows(categories.size() / grid.getColumns());
        for (Category c : categories) {
            CategoryTile view = new CategoryTile(c);
            view.addLayoutClickListener(layoutClickEvent -> {
                try {
                    getUI().getNavigator().navigateTo(ProductsView.NAME + "/"
                            + view.getEntity().getSkroutzId()
                            + "/" + URLEncoder.encode(query, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Notifier.error("Προβλημα στο encoding", e);
                }
            });
            grid.addComponent(view);
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
    public void layoutClick(LayoutEvents.LayoutClickEvent layoutClickEvent) {
        if (layoutClickEvent.getClickedComponent().getClass().isAssignableFrom(CategoryTile.class)) {
            getUI().getNavigator().navigateTo(ProductsView.NAME + "/"
                    + ((CategoryTile) layoutClickEvent.getClickedComponent()).getEntity().getSkroutzId()
                    + "/" + query);
        } else if (layoutClickEvent.getClickedComponent().getClass().isAssignableFrom(SkuTile.class)) {

        } else if (layoutClickEvent.getClickedComponent().getClass().isAssignableFrom(ShopTile.class)) {

        } else if (layoutClickEvent.getClickedComponent().getClass().isAssignableFrom(ManufacturerTile.class)) {

        }
    }
}
