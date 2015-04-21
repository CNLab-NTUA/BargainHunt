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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class ProductsView extends VerticalLayout implements View {
    String query;
    Category category;
    List<Sku> skus;

    public static final String NAME = "products";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        parseParameters(event.getParameters());

    }

    private void parseParameters(String parameters) {
        String[] splitParameters = parameters.split("/");
        if (splitParameters.length == 2) {
            try {
                this.category = SkroutzRestClient.get().get(Category.class, Long.valueOf(splitParameters[0]));
                this.query = URLDecoder.decode(splitParameters[1], "utf-8");
                Label label = new Label();
                this.skus = SkroutzRestClient.get().getNested(category, Sku.class, new QueryFilter(query)).getItems();
                label.setValue(skus.toString());
                addComponent(label);
            } catch (UnsupportedEncodingException e) {
                Notifier.error("Δεν ήταν δυνατό το διάβασμα του query", e);
            }
        } else {
            Notifier.error("Δεν υπάρχει η σελίδα που αναζητήσατε.", new Exception());
        }
    }
}
