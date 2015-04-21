package gr.ntua.cn.zannis.bargains.webapp.screens;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.client.requests.filters.QueryFilter;
import gr.ntua.cn.zannis.bargains.webapp.components.Notifier;
import gr.ntua.cn.zannis.bargains.webapp.components.ResponsiveGridLayout;
import gr.ntua.cn.zannis.bargains.webapp.components.tiles.SkuTile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class ProductsView extends VerticalLayout implements View {

    public static final String NAME = "products";

    private String query;
    private Category category;
    private List<Sku> skus;

    private VerticalLayout resultsLayout = new VerticalLayout();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        parseParameters(event.getParameters());
        if (this.category != null && this.skus != null) {
            renderSkus(this.skus);
        }
    }

    private void renderSkus(List<Sku> skus) {
        Label label = new Label("Αποτελέσματα");
        ResponsiveGridLayout grid = new ResponsiveGridLayout();
        for (Sku s : skus) {
            SkuTile tile = new SkuTile(s);
            tile.addClickListener(event -> {
                String uri = BargainView.NAME + "/" + tile.getEntity().getSkroutzId();
                getUI().getNavigator().navigateTo(uri);
            });
            grid.addComponent(tile);
        }
        resultsLayout.addComponent(label);
        resultsLayout.addComponent(grid);

        addComponent(resultsLayout);
    }

    private void parseParameters(String parameters) {
        String[] splitParameters = parameters.split("/");
        if (splitParameters.length == 2) {
            try {
                this.category = SkroutzRestClient.get().get(Category.class, Long.valueOf(splitParameters[0]));
                this.query = URLDecoder.decode(splitParameters[1], "utf-8");
                this.skus = SkroutzRestClient.get().getNested(category, Sku.class, new QueryFilter(query)).getItems();
            } catch (UnsupportedEncodingException e) {
                Notifier.error("Δεν ήταν δυνατό το διάβασμα του query", e);
            }
        } else {
            Notifier.error("Δεν υπάρχει η σελίδα που αναζητήσατε.", new Exception());
        }
    }
}
