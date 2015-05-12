package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import gr.ntua.cn.zannis.bargains.webapp.algorithm.SimpleOutlierFinder;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@CDIView(TestView.NAME)
public class TestView extends VerticalLayout implements View {

    public static final String NAME = "test";

    public static final Logger log = LoggerFactory.getLogger(TestView.class);

    private List<Label> labels;

    public TestView() {
        Category upsCategory = ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().find(Category.class, 41);
        for (Sku sku : upsCategory.getSkus()) {
            Label label = new Label();
            List<Float> prices = sku.getProducts().stream().map(Product::getPrice).collect(Collectors.toList());
            SimpleOutlierFinder finder = new SimpleOutlierFinder(prices);
            label.setValue(sku.getName() + " - low :" + finder.getLowOutliers() + " - average : " + finder.getNormalizedMean() + " - high :" + finder.getHighOutliers());
            log.info(label.getValue());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
