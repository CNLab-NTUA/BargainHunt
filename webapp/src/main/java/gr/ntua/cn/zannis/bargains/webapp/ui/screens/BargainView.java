package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.impl.ChauvenetTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.GrubbsTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.QuartileTester;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Offer;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Price;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzClient;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles.OfferTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zannis <zannis.kal@gmail.com
 */
@CDIView(BargainView.NAME)
public class BargainView extends VerticalLayout implements View {

    public static final Logger log = LoggerFactory.getLogger(BargainView.class.getSimpleName());

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
        OfferTile tile = null;

        String bargainFound = null;

        List<Float> prices = products.stream().map(Product::getPrice).collect(Collectors.toList());

        if (prices != null && prices.size() >= 3) {
            GrubbsTester grubbsTester = new GrubbsTester(Flexibility.STRONG);
            ChauvenetTester chauvenetTester = new ChauvenetTester(Flexibility.STRONG);
            QuartileTester quartileTester = new QuartileTester(Flexibility.STRONG);
            Float grubbsOutlier = grubbsTester.getMinimumOutlier(prices);
            while (grubbsOutlier.equals(Float.NaN) && grubbsTester.getFlexibility().getMoreFlexible() != null) {
                grubbsTester.setFlexibility(grubbsTester.getFlexibility().getMoreFlexible());
                grubbsOutlier = grubbsTester.getMinimumOutlier(prices);
            }
            Float chauvenetOutlier = chauvenetTester.getMinimumOutlier(prices);
            while (chauvenetOutlier.equals(Float.NaN) && chauvenetTester.getFlexibility().getMoreFlexible() != null) {
                chauvenetTester.setFlexibility(chauvenetTester.getFlexibility().getMoreFlexible());
                chauvenetOutlier = chauvenetTester.getMinimumOutlier(prices);
            }
            Float quartileOutlier = quartileTester.getMinimumOutlier(prices);
            while (quartileOutlier.equals(Float.NaN) && quartileTester.getFlexibility().getMoreFlexible() != null) {
                quartileTester.setFlexibility(quartileTester.getFlexibility().getMoreFlexible());
                quartileOutlier = quartileTester.getMinimumOutlier(prices);
            }

            boolean grubbsResult = !grubbsOutlier.equals(Float.NaN);
            boolean chauvenetResult = !chauvenetOutlier.equals(Float.NaN);
            boolean quartileResult = !quartileOutlier.equals(Float.NaN);

            short acceptedBy = Offer.calculateAcceptedBy(grubbsResult, chauvenetResult, quartileResult);

            if (acceptedBy != 0) {
                bargainFound = "Το προϊόν " + sku.getName() + " βρίσκεται σε προσφορά σύμφωνα με τους ελέγχους: ";
                bargainFound += Offer.acceptedByHumanReadable(acceptedBy);
                log.info(bargainFound);

                Product product = Product.getCheapest(products);
                Float lowestPrice = product.getPrice();
                Price price = Price.fromProduct(product);
                Offer offer = new Offer(product, price, acceptedBy,
                        grubbsTester.getFlexibility(), chauvenetTester.getFlexibility(), quartileTester.getFlexibility());
                Offer dbOffer = ((BargainHuntUI) UI.getCurrent()).getOfferEm().findByProduct(product);
                if (dbOffer != null) {
                    dbOffer.setFinishedAt(Date.from(Instant.now()));
                }
                tile = new OfferTile(offer);
//                ((BargainHuntUI) UI.getCurrent()).getOfferEm().persist(offer);
                log.info("Το προϊόν " + sku.getName() + " βρίσκεται σε προσφορά στα " + lowestPrice + " ευρώ.");

            }
        }

        if (tile != null) {
            Label offerLabel = new Label(bargainFound);
            offerLabel.setStyleName(ValoTheme.LABEL_LARGE);
            bargainLayout.addComponents(offerLabel, tile);
            bargainLayout.setComponentAlignment(offerLabel, Alignment.MIDDLE_CENTER);
            bargainLayout.setComponentAlignment(tile, Alignment.MIDDLE_CENTER);
        } else {
            Label noOfferLabel = new Label("Δυστυχώς το προϊόν δεν είναι σε προσφορά αυτή τη στιγμή. " +
                    "Δοκιμάστε ξανά στο μέλλον ή δοκιμάστε κάποιο από τα παρακάτω:");
            HorizontalLayout offerBar = new HorizontalLayout();

            int numberOfOffers = 5;
            List<Offer> offers = ((BargainHuntUI) UI.getCurrent()).getOfferEm().getTopActive(numberOfOffers, sku.getCategoryId());
            offers.forEach(o -> offerBar.addComponent(new OfferTile(o)));
            offerBar.setMargin(new MarginInfo(true));
            offerBar.setSpacing(true);
            bargainLayout.addComponents(noOfferLabel, offerBar);
            bargainLayout.setComponentAlignment(noOfferLabel, Alignment.MIDDLE_CENTER);
            bargainLayout.setComponentAlignment(offerBar, Alignment.MIDDLE_CENTER);
        }

        // add line charts with vaadin charts
        Sku.PriceHistory history = SkroutzClient.getInstance().getPriceHistory(sku.getSkroutzId());

        Chart chart = new Chart();
        chart.setHeight("450px");
        chart.setWidth("100%");

        Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.LINE);

        configuration.getTitle().setText("Ιστορικό Τιμών");

        List<String> dates = new ArrayList<>();

        for (Sku.PricePoint p : history.getLowest()) {
            dates.add(new SimpleDateFormat("dd/MM/yy").format(p.getDate()));
        }

        configuration.getxAxis().setCategories(dates.toArray(new String[0]));

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle("Τιμή (Ευρώ)"));

        configuration
                .getTooltip()
                .setFormatter(
                        "'<b>'+ this.series.name +'</b><br/>'+this.x +': '+ this.y +' €'");

        PlotOptionsLine plotOptions = new PlotOptionsLine();
        plotOptions.setDataLabels(new DataLabels(true));
        plotOptions.setEnableMouseTracking(false);
        configuration.setPlotOptions(plotOptions);

        ListSeries ls = new ListSeries();
        ls.setName("Μέσος όρος");
        List<Number> averagePrices = Arrays.stream(history.getAverage()).filter(pricePoint -> dates.contains(new SimpleDateFormat("dd/MM/yy").format(pricePoint.getDate()))).map(Sku.PricePoint::getPrice).collect(Collectors.toList());
        ls.setData(averagePrices);
        configuration.addSeries(ls);

        ls = new ListSeries();
        ls.setName("Χαμηλότερη τιμή");
        List<Number> lowestPrices = Arrays.stream(history.getLowest()).map(Sku.PricePoint::getPrice).collect(Collectors.toList());
        ls.setData(lowestPrices);
        configuration.addSeries(ls);

        chart.drawChart(configuration);

        bargainLayout.addComponent(chart);

        addComponent(bargainLayout);
        setComponentAlignment(bargainLayout, Alignment.MIDDLE_CENTER);
    }

    private void parseParameters(String parameters) {
        String[] splitParameters = parameters.split("/");
        if (splitParameters.length == 1) {
            try {
                Sku dbSku = ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().find(Sku.class, Integer.valueOf(splitParameters[0]));
                this.sku =  SkroutzClient.getInstance().get(Sku.class, Integer.valueOf(splitParameters[0]), dbSku);
                this.products = SkroutzClient.getInstance().getNestedAsList(sku, Product.class);
            } catch (NumberFormatException e) {
                Notifier.error("Δεν δώσατε κατάλληλο αναγνωριστικό προϊόντος.", true);
                this.sku = null;
                this.products = null;
            }
        } else {
            String correctUrl = BargainView.NAME + "/" + splitParameters[0];
            getUI().getNavigator().navigateTo(correctUrl);
        }
    }
}