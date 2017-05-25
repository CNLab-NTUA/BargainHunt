package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.TestType;
import gr.ntua.cn.zannis.bargains.statistics.impl.ChauvenetTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.GrubbsTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.QuartileTester;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.*;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.TesterPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.captionpositions.CaptionPositions;
import org.vaadin.captionpositions.client.CaptionPosition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zannis zannis.kal@gmail.com
 */
@CDIView(CrawlerView.NAME)
@SuppressWarnings("serial")
public class CrawlerView extends VerticalLayout implements View {


    public static final String NAME = "crawler";

    public static final Logger log = LoggerFactory.getLogger(CrawlerView.class);
    private Set<Category> cachedCategories;
    private TextArea logArea;
    private Map cachedSkus;
    private Map cachedProducts;
    private ComboBox<Category> categorySelect;


    public CrawlerView() throws IOException {

        initCache();
        FileWriter fw = new FileWriter(new File(System.getenv("HOME") + File.separator +
                "offers.txt"));
        buildUI();
    }

    private void initCache() {
        cachedCategories = new HashSet<>(((BargainHuntUI) UI.getCurrent()).getSkroutzEm().findParsed(Category.class));
        cachedSkus = new HashMap<>();
        cachedProducts = new HashMap<>();
    }

    private void buildUI() throws IOException {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setMargin(true);
        // build logo
        Label logo = new Label("Skroutz Crawler");
        logo.setSizeUndefined();
        logo.setStyleName(ValoTheme.LABEL_HUGE);

        VerticalLayout content = new VerticalLayout();

        // build category chooser panel
//        HorizontalLayout categoryLayout = buildPanel();
//        categoryLayout.setSizeFull();
        HorizontalLayout testerLayout = buildTesterPanel();
        testerLayout.setSizeFull();

        // build textarea log
        logArea = new TextArea("Συμβάντα");
        logArea.addValueChangeListener(valueChangeEvent -> {
            if (logArea.getValue() != null) {
                logArea.setCursorPosition(logArea.getValue().length() - 1);
            }
        });
        logArea.setSizeFull();

        // add to content pane
//        content.addComponent(categoryLayout);
        content.addComponent(testerLayout);
        content.addComponent(logArea);
//        content.setExpandRatio(categoryLayout, 0.3f);
        content.setExpandRatio(testerLayout, 0.5f);
        content.setExpandRatio(logArea, 0.5f);
        content.setSizeFull();
        // add to ui
        addComponent(logo);
        addComponent(content);
        setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        setComponentAlignment(content, Alignment.MIDDLE_CENTER);
        setExpandRatio(logo, 0.05f);
        setExpandRatio(content, 0.95f);
    }

    private HorizontalLayout buildTesterPanel() {

        HorizontalLayout panelLayout = new HorizontalLayout();
        panelLayout.setSizeFull();
        panelLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        VerticalLayout testersLayout = new VerticalLayout();
        TesterPanel[] testerPanels = new TesterPanel[3];

        int i = 0;
        for (TestType type : TestType.values()) {
            testerPanels[i] = (new TesterPanel(type));
            testersLayout.addComponent(testerPanels[i++]);
        }

        VerticalLayout categoriesLayout = new VerticalLayout();
        Label categories = new Label("Αναζήτηση σε");
        CheckBox allCategories = new CheckBox("Όλες τις κατηγορίες");
        new CaptionPositions(categoriesLayout).setCaptionPosition(allCategories, CaptionPosition.LEFT);
        allCategories.setValue(false);
        categorySelect = new ComboBox<>("Επιλογή", this.cachedCategories);
        new CaptionPositions(panelLayout).setCaptionPosition(categorySelect, CaptionPosition.LEFT);
        categorySelect.setItemCaptionGenerator(Category::getName);
        TextField resultsToRetrieve = new TextField("Σύνολο αποτελεσμάτων", "250");
        categoriesLayout.addComponents(categories, allCategories, categorySelect, resultsToRetrieve);
        testersLayout.addComponents(testerPanels);
        testersLayout.setSizeFull();
        Button crawlButton = new Button("Αναζήτηση");
        final List<Object> selectedCategories = new ArrayList<>();

        crawlButton.addClickListener((Button.ClickListener) (Button.ClickEvent event) -> {

            GrubbsTester grubbsTester = null;
            ChauvenetTester chauvenetTester = null;
            QuartileTester quartileTester = null;
            Flexibility grubbsFlexibility = null;
            Flexibility chauvenetFlexibility = null;
            Flexibility quartileFlexibility = null;

            for (TesterPanel panel : testerPanels) {
                if (panel.getTester() != null) {
                    if (panel.getTester().getType() == TestType.GRUBBS) {
                        grubbsTester = ((GrubbsTester) panel.getTester());
                        grubbsFlexibility = panel.getFlexibility();
                    } else if (panel.getTester().getType() == TestType.CHAUVENET) {
                        chauvenetTester = ((ChauvenetTester) panel.getTester());
                        chauvenetFlexibility = panel.getFlexibility();
                    } else {
                        quartileTester = ((QuartileTester) panel.getTester());
                        quartileFlexibility = panel.getFlexibility();
                    }
                }
            }

            selectedCategories.clear();
            if (allCategories.getValue()) {
                selectedCategories.addAll(this.cachedCategories);
            } else {
                selectedCategories.add(categorySelect.getValue());
            }

            int pages = 10;
            try {
                if (Integer.parseInt(resultsToRetrieve.getValue()) % 25 == 0) {
                    pages = Integer.parseInt(resultsToRetrieve.getValue()) / 25;
                }
            } catch (NumberFormatException e) {
                logInfo("Λάθος όριο αποτελεσμάτων, χρησιμοποιείται το default : 250");
            }


            for (Object c : selectedCategories) {
                String logCategory = "Κατέβασμα προϊόντων της κατηγορίας " + ((Category) c).getName();
                logInfo(logCategory);
                logInfo("---------------------------------------------------------------------------------------");
                cachedSkus = new HashMap<Integer, Sku>();
                cachedProducts = new HashMap<Integer, Product>();
                try {
                    Page<Sku> skuPage = SkroutzClient.getInstance().getNested(((Category) c), Sku.class);
                    while (skuPage != null && skuPage.getCurrentPage() <= pages && (skuPage.hasNext() || skuPage.isLastPage())) {
                        logInfo("Κατέβηκαν " + skuPage.getPer() + " μοναδικά προϊόντα.");
                        for (Sku sku : skuPage.getItems()) {
//                            cachedSkus.put(sku.getSkroutzId(), sku);
                            List<Product> products = SkroutzClient.getInstance().getNestedAsList(sku, Product.class);
                            if (products != null) {
                                for (Product p : products) {
                                    Price price = Price.fromProduct(p);
                                    if (p.getPrices() == null) {
                                        p.setPrices(new ArrayList<>());
                                    }
                                    if (p.getPrices().stream().noneMatch(tp -> tp.getPrice() == price.getPrice())) {
                                        p.getPrices().add(price);
                                    }
//                                    cachedProducts.put(p.getSkroutzId(), p);
                                }

                                List<Float> prices = products.stream().filter(pr -> pr.getSkuId() == sku.getSkroutzId()).map(Product::getPrice).collect(Collectors.toList());
                                if (prices != null && prices.size() >= 3) {

                                    // make the outlier tests
                                    Float grubbsOutlier = grubbsTester != null ? grubbsTester.getMinimumOutlier(prices) : Float.NaN;
                                    while (grubbsTester != null && grubbsOutlier.equals(Float.NaN) && grubbsTester.getFlexibility().getMoreFlexible() != null) {
                                        grubbsTester.setFlexibility(grubbsTester.getFlexibility().getMoreFlexible());
                                        grubbsOutlier = grubbsTester.getMinimumOutlier(prices);
                                    }
                                    Float chauvenetOutlier = chauvenetTester != null ? chauvenetTester.getMinimumOutlier(prices) : Float.NaN;
                                    while (chauvenetTester != null && chauvenetOutlier.equals(Float.NaN) && chauvenetTester.getFlexibility().getMoreFlexible() != null) {
                                        chauvenetTester.setFlexibility(chauvenetTester.getFlexibility().getMoreFlexible());
                                        chauvenetOutlier = chauvenetTester.getMinimumOutlier(prices);
                                    }
                                    Float quartileOutlier = quartileTester != null ? quartileTester.getMinimumOutlier(prices) : Float.NaN;
                                    while (quartileTester != null && quartileOutlier.equals(Float.NaN) && quartileTester.getFlexibility().getMoreFlexible() != null) {
                                        quartileTester.setFlexibility(quartileTester.getFlexibility().getMoreFlexible());
                                        quartileOutlier = quartileTester.getMinimumOutlier(prices);
                                    }

                                    boolean grubbsResult = !grubbsOutlier.equals(Float.NaN);
                                    boolean chauvenetResult = !chauvenetOutlier.equals(Float.NaN);
                                    boolean quartileResult = !quartileOutlier.equals(Float.NaN);

                                    short acceptedBy = Offer.calculateAcceptedBy(grubbsResult, chauvenetResult, quartileResult);

                                    if (acceptedBy != 0) {
                                        String bargainFound = "Το προϊόν " + sku.getName() + " βρίσκεται σε προσφορά σύμφωνα με τους ελέγχους: ";
                                        bargainFound += Offer.acceptedByHumanReadable(acceptedBy);
                                        logInfo(bargainFound);
                                    }


                                    Float lowestPrice = null;
                                    if (grubbsResult) {
                                        lowestPrice = grubbsOutlier;
                                    } else if (chauvenetResult) {
                                        lowestPrice = chauvenetOutlier;
                                    } else if (quartileResult) {
                                        lowestPrice = quartileOutlier;
                                    }
                                    if (acceptedBy != 0) {
                                        // some outlier check confirmed an offer
                                        Product product = Product.getCheapest(products);
                                        Offer offer;
                                        Offer dbOffer = ((BargainHuntUI) UI.getCurrent()).getOfferEm().findByProduct(product);
                                        if (dbOffer != null) {
                                            dbOffer.setFinishedAt(Date.from(Instant.now()));
                                        }
                                        if (product.getPrices() != null && !product.getPrices().isEmpty()) {
                                            offer = new Offer(product, product.getPrices().stream()
                                                    .filter(pr -> pr.getPrice() == product.getPrice()).findFirst().get(), acceptedBy,
                                                    grubbsFlexibility, chauvenetFlexibility, quartileFlexibility);
                                        } else {
                                            offer = new Offer(product, Price.fromProduct(product), acceptedBy, grubbsTester.getFlexibility(),
                                                    chauvenetTester.getFlexibility(), quartileTester.getFlexibility());
                                        }

                                        ((BargainHuntUI) UI.getCurrent()).getOfferEm().persist(offer);
                                        logInfo("Το προϊόν " + sku.getName() + " βρίσκεται σε προσφορά στα " + lowestPrice + " ευρώ.");
                                    }
                                }

                                logInfo("Κατέβηκαν " + products.size() + " τιμές για το προϊόν " + sku.getName() + ".");
                            }
                        }

                        skuPage = SkroutzClient.getInstance().getNextPage(skuPage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logInfo("Υπήρξε πρόβλημα: " + e.getMessage());
                }
            }
        });
        panelLayout.addComponents(testersLayout, categoriesLayout, crawlButton);
        panelLayout.setExpandRatio(testersLayout, 0.50f);
        panelLayout.setExpandRatio(categoriesLayout, 0.25f);
        panelLayout.setExpandRatio(crawlButton, 0.25f);
        return panelLayout;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    private void logInfo(String message) {
        log.info(message);
        logArea.setValue(logArea.getValue() + "\n" + message);
        logArea.setCursorPosition(logArea.getValue().length());
        UI.getCurrent().push();
    }
}
