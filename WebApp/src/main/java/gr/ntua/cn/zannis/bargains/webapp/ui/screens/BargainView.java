package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.impl.GrubbsTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.QuartileTester;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzClient;
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
        QuartileTester quartileTester = new QuartileTester(Flexibility.NORMAL);
        GrubbsTester grubbsTester = new GrubbsTester(Flexibility.NORMAL);
        Float grubbsResult = grubbsTester.getMinimumOutlier(prices);
        Float quartileResult = quartileTester.getMinimumOutlier(prices);
        Label resultLabel;
        if (grubbsResult.equals(Float.NaN) || quartileResult.equals(Float.NaN)) {
            resultLabel = new Label("Δυστυχώς το προϊόν δεν είναι σε προσφορά. Δοκιμάστε ξανά στο μέλλον.");
        } else {
            resultLabel = new Label("Είστε τυχεροί! Το προϊόν είναι σε προσφορά στα " + grubbsResult + "€ !");
        }

        bargainLayout.addComponent(label);
        bargainLayout.addComponent(resultLabel);

        addComponent(bargainLayout);
    }

    private void parseParameters(String parameters) {
        String[] splitParameters = parameters.split("/");
        if (splitParameters.length == 1) {
            try {
                this.sku = SkroutzClient.getInstance().get(Sku.class, Integer.valueOf(splitParameters[0]));
                Page<Product> firstPage = SkroutzClient.getInstance().getNested(sku, Product.class);
                this.products = SkroutzClient.getInstance().getAllResultsAsList(firstPage);
            } catch (NumberFormatException e) {
                Notifier.error("Δεν δώσατε κατάλληλο αναγνωριστικό προϊόντος.", true);
            }
        } else {
            String correctUrl = BargainView.NAME + "/" + splitParameters[0];
            getUI().getNavigator().navigateTo(correctUrl);
        }
    }
}
