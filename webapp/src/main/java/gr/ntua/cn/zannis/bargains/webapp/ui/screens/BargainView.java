package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.impl.GrubbsTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.QuartileTester;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Offer;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Price;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;

import java.time.Instant;
import java.util.Date;
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

//        List<Float> prices = products.stream().filter(pr -> pr.getSkuId() == dbSku.getSkroutzId()).map(Product::getPrice).collect(Collectors.toList());
//        if (prices != null && prices.size() >= 3) {
//
//            // make the outlier tests
//            Float grubbsOutlier = grubbsTester != null ? grubbsTester.getMinimumOutlier(prices) : Float.NaN;
//            while (grubbsTester != null && grubbsOutlier.equals(Float.NaN) && grubbsTester.getFlexibility().getMoreFlexible() != null) {
//                grubbsTester.setFlexibility(grubbsTester.getFlexibility().getMoreFlexible());
//                grubbsOutlier = grubbsTester.getMinimumOutlier(prices);
//            }
//            Float chauvenetOutlier = chauvenetTester != null ? chauvenetTester.getMinimumOutlier(prices) : Float.NaN;
//            while (chauvenetTester != null && chauvenetOutlier.equals(Float.NaN) && chauvenetTester.getFlexibility().getMoreFlexible() != null) {
//                chauvenetTester.setFlexibility(chauvenetTester.getFlexibility().getMoreFlexible());
//                chauvenetOutlier = chauvenetTester.getMinimumOutlier(prices);
//            }
//            Float quartileOutlier = quartileTester != null ? quartileTester.getMinimumOutlier(prices) : Float.NaN;
//            while (quartileTester != null && quartileOutlier.equals(Float.NaN) && quartileTester.getFlexibility().getMoreFlexible() != null) {
//                quartileTester.setFlexibility(quartileTester.getFlexibility().getMoreFlexible());
//                quartileOutlier = quartileTester.getMinimumOutlier(prices);
//            }
//
//            boolean grubbsResult = !grubbsOutlier.equals(Float.NaN);
//            boolean chauvenetResult = !chauvenetOutlier.equals(Float.NaN);
//            boolean quartileResult = !quartileOutlier.equals(Float.NaN);
//
//            short acceptedBy = Offer.calculateAcceptedBy(grubbsResult, chauvenetResult, quartileResult);
//
//            if (acceptedBy != 0) {
//                String bargainFound = "Το προϊόν " + dbSku.getName() + " βρίσκεται σε προσφορά σύμφωνα με τους ελέγχους: ";
//                switch (acceptedBy) {
//                    case 1:
//                        bargainFound += "Grubbs";
//                        break;
//                    case 2:
//                        bargainFound += "Chauvenet";
//                        break;
//                    case 3:
//                        bargainFound += "Τεταρτημορία";
//                        break;
//                    case 4:
//                        bargainFound += "Grubbs, Chauvenet";
//                        break;
//                    case 5:
//                        bargainFound += "Grubbs, Τεταρτημόρια";
//                        break;
//                    case 6:
//                        bargainFound += "Chauvenet, Τεταρτημόρια";
//                        break;
//                    case 7:
//                        bargainFound += "Grubbs, Chauvenet, Τεταρτημόρια";
//                        break;
//                }
//                logInfo(bargainFound);
//            }
//
//
//            Float lowestPrice = null;
//            if (grubbsResult) {
//                lowestPrice = grubbsOutlier;
//            } else if (chauvenetResult) {
//                lowestPrice = chauvenetOutlier;
//            } else if (quartileResult) {
//                lowestPrice = quartileOutlier;
//            }
//            if (acceptedBy != 0) {
//                // some outlier check confirmed an offer
//                Product product = Product.getCheapest(products);
//                Offer offer;
//                Offer dbOffer = ((BargainHuntUI) UI.getCurrent()).getOfferEm().findByProduct(product);
//                if (dbOffer != null) {
//                    dbOffer.setFinishedAt(Date.from(Instant.now()));
//                }
//                if (product.getPrices() != null && !product.getPrices().isEmpty()) {
//                    offer = new Offer(product, product.getPrices().stream()
//                            .filter(pr -> pr.getPrice() == product.getPrice()).findFirst().get(), acceptedBy,
//                            grubbsFlexibility, chauvenetFlexibility, quartileFlexibility);
//                } else {
//                    offer = new Offer(product, Price.fromProduct(product), acceptedBy, grubbsTester.getFlexibility(),
//                            chauvenetTester.getFlexibility(), quartileTester.getFlexibility());
//                }
//
//                ((BargainHuntUI) UI.getCurrent()).getOfferEm().persist(offer);
//                logInfo("Το προϊόν " + dbSku.getName() + " βρίσκεται σε προσφορά στα " + lowestPrice + " ευρώ.");
//            }
//        }



        Label resultLabel = new Label();
//        if (grubbsResult.equals(Float.NaN) || quartileResult.equals(Float.NaN)) {
//            resultLabel = new Label("Δυστυχώς το προϊόν δεν είναι σε προσφορά. Δοκιμάστε ξανά στο μέλλον.");
//        } else {
//            resultLabel = new Label("Είστε τυχεροί! Το προϊόν είναι σε προσφορά στα " + grubbsResult + "€ !");
//        }

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
