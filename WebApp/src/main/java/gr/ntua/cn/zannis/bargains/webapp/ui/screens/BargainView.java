package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import gr.ntua.cn.zannis.bargains.webapp.algorithm.FilterStrength;
import gr.ntua.cn.zannis.bargains.webapp.algorithm.OutlierFinder;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzRestClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zannis <zannis.kal@gmail.com
 */
@CDIView(BargainView.NAME)
public class BargainView extends VerticalLayout implements View {

    public static final String NAME = "results";

    private Sku sku;
    private List<Product> products;

    private VerticalLayout bargainLayout = new VerticalLayout();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        parseParameters(event.getParameters());
        if (this.sku != null) {
            renderBargain(sku);
        }
    }

    private void renderBargain(Sku sku) {
        Label label = new Label("Πληροφορίες για το προϊόν " + sku.getName());
        List<Float> prices = products.stream().map(Product::getPrice).collect(Collectors.toList());
        OutlierFinder finder = new OutlierFinder(prices, FilterStrength.DEFAULT);
        List<Float> results = finder.getLowOutliers();
        Label result;
        if (results.isEmpty()) {
            result = new Label("Δυστυχώς το προϊόν δεν είναι σε προσφορά. Δοκιμάστε ξανά στο μέλλον.");
        } else {
            result = new Label("Είστε τυχεροί! Το προϊόν είναι σε προσφορά στα " + results.get(0) + "€ !");
        }

        bargainLayout.addComponent(label);
        bargainLayout.addComponent(result);

        addComponent(bargainLayout);
    }

    private void parseParameters(String parameters) {
        String[] splitParameters = parameters.split("/");
        if (splitParameters.length == 1) {
            try {
                this.sku = SkroutzRestClient.getInstance().get(Sku.class, Integer.valueOf(splitParameters[0]));
                Page<Product> firstPage = SkroutzRestClient.getInstance().getNested(sku, Product.class);
                this.products = SkroutzRestClient.getInstance().getAllResultsAsList(firstPage);
            } catch (NumberFormatException e) {
                Notifier.error("Δεν δώσατε κατάλληλο αναγνωριστικό προϊόντος.", true);
            }
        } else {
            String correctUrl = BargainView.NAME + "/" + splitParameters[0];
            getUI().getNavigator().navigateTo(correctUrl);
        }
    }
}
