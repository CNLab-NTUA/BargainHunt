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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private Button crawlButton;
    private TextArea logArea;
    private BufferedWriter bw;
    private Set<Sku> cachedSkus;
    private Set<Product> cachedProducts;

    public CrawlerView() throws IOException {

        initCache();
        FileWriter fw = new FileWriter(new File(System.getenv("HOME") + File.separator +
                "offers.txt"));
        bw = new BufferedWriter(fw);
        buildUI();
    }

    private void initCache() {
        cachedCategories = new HashSet<>(((BargainHuntUI) UI.getCurrent()).getSkroutzEm().findParsed(Category.class));
        cachedSkus = new HashSet<>();
        cachedProducts = new HashSet<>();
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
        HorizontalLayout categoryLayout = buildPanel();
        categoryLayout.setSizeFull();
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
        content.addComponent(categoryLayout);
        content.addComponent(testerLayout);
        content.addComponent(logArea);
        content.setExpandRatio(categoryLayout, 0.1f);
        content.setExpandRatio(testerLayout, 0.4f);
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
        ComboBox<Category> categorySelect = new ComboBox<>("Επιλογή", this.cachedCategories);
        new CaptionPositions(panelLayout).setCaptionPosition(categorySelect, CaptionPosition.LEFT);
        categorySelect.setItemCaptionGenerator(Category::getName);
        categoriesLayout.addComponents(categories, allCategories, categorySelect);
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

            if (allCategories.getValue()) {
                selectedCategories.addAll(this.cachedCategories);
            } else {
                selectedCategories.add(categorySelect.getValue());
            }


            for (Object c : selectedCategories) {
                if (cachedSkus.isEmpty()) {
                    Collection<Sku> skus = ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().findParsed(Sku.class, ((Category) c).getSkroutzId());
                    cachedSkus.addAll(skus);
                }
                for (Sku s : cachedSkus) {
                    if (cachedProducts.stream().noneMatch(p -> p.getSkuId() == s.getSkroutzId())) {
                        cachedProducts.addAll(((BargainHuntUI) UI.getCurrent()).getSkroutzEm().findProductsForSku(s.getSkroutzId()));
                    }
                    List<Float> prices = cachedProducts.stream().filter(p -> p.getSkuId() == s.getSkroutzId()).map(Product::getPrice).collect(Collectors.toList());
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
                            String bargainFound = "Το προϊόν " + s.getName() + " βρίσκεται σε προσφορά σύμφωνα με τους ελέγχους: ";
                            switch (acceptedBy) {
                                case 1:
                                    bargainFound += "Grubbs";
                                    break;
                                case 2:
                                    bargainFound += "Chauvenet";
                                    break;
                                case 3:
                                    bargainFound += "Τεταρτημορία";
                                    break;
                                case 4:
                                    bargainFound += "Grubbs, Chauvenet";
                                    break;
                                case 5:
                                    bargainFound += "Grubbs, Τεταρτημόρια";
                                    break;
                                case 6:
                                    bargainFound += "Chauvenet, Τεταρτημόρια";
                                    break;
                                case 7:
                                    bargainFound += "Grubbs, Chauvenet, Τεταρτημόρια";
                                    break;
                            }
                            log.info(bargainFound);
                            logArea.setValue(logArea.getValue() + "\n" + bargainFound);
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
                            Product product = Product.getCheapest(s.getProducts());
                            Offer offer;
                            if (product.getPrices() != null && !product.getPrices().isEmpty()) {
                                offer = new Offer(product, product.getPrices().stream()
                                        .filter(p -> p.getPrice() == product.getPrice()).findFirst().get(), acceptedBy,
                                        grubbsFlexibility, chauvenetFlexibility, quartileFlexibility);
                            } else {
                                offer = new Offer(product, Price.fromProduct(product), acceptedBy, grubbsTester.getFlexibility(),
                                        chauvenetTester.getFlexibility(), quartileTester.getFlexibility());
                            }

                            ((BargainHuntUI) UI.getCurrent()).getOfferEm().persist(offer);
                            String bargainFound = "Το προϊόν " + s.getName() + " βρίσκεται σε προσφορά στα " + lowestPrice + " ευρώ.";
                            log.info(bargainFound);
                            if (bw != null) {
                                try {
                                    bw.append(offer.toString()).append(System.lineSeparator());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            logArea.setValue(logArea.getValue() + "\n" + bargainFound);
                        }
                    }
                }
            }
        });
        panelLayout.addComponents(testersLayout, categoriesLayout, crawlButton);
        panelLayout.setExpandRatio(testersLayout, 0.50f);
        panelLayout.setExpandRatio(categoriesLayout, 0.25f);
        panelLayout.setExpandRatio(crawlButton, 0.25f);
        return panelLayout;
    }

    private HorizontalLayout buildPanel() throws IOException {
        HorizontalLayout categoryLayout = new HorizontalLayout();
        categoryLayout.setSizeFull();
        categoryLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

//        fetch cachedCategories from db
//        not needed, cachedCategories already exist in the JPAContainer
//        cachedCategories = ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().findAll(Category.class);

        // build panel ui
        NativeSelect<String> flexibilitySelect = new NativeSelect<>("Εύρος εμπιστοσύνης :");
        new CaptionPositions(categoryLayout).setCaptionPosition(flexibilitySelect, CaptionPosition.LEFT);

        ComboBox<Category> categorySelect = new ComboBox<>(null, this.cachedCategories);
        categorySelect.setEmptySelectionAllowed(false);
        categorySelect.setEmptySelectionCaption("Επιλέξτε κατηγορία :");
        categorySelect.setItemCaptionGenerator(Category::getName);
        categorySelect.addValueChangeListener(valueChangeEvent -> {
            if (flexibilitySelect.getValue() != null && categorySelect.getValue() != null) {
                crawlButton.setEnabled(true);
            } else {
                crawlButton.setEnabled(false);
            }
        });

        flexibilitySelect.setItems("Χαμηλό", "Κανονικό", "Υψηλό");
        flexibilitySelect.addValueChangeListener(valueChangeEvent -> {
            if (flexibilitySelect.getValue() != null && categorySelect.getValue() != null) {
                crawlButton.setEnabled(true);
            } else {
                crawlButton.setEnabled(false);
            }
        });

        crawlButton = new Button("Λήψη προϊόντων");
        crawlButton.setDisableOnClick(true);
        crawlButton.setEnabled(false);
        crawlButton.addClickListener(clickEvent -> {
            switch (flexibilitySelect.getValue()) {
                case "Χαμηλό":
                    break;
                case "Κανονικό":
                    break;
                case "Υψηλό":
                    break;
            }
            String logCategory = "Κατέβασμα προϊόντων της κατηγορίας " + cachedCategories.stream().map(Category::getName);
            log.info(logCategory);
            logArea.setValue(logArea.getValue() + "\n" + logCategory);
            log.info("---------------------------------------------------------------------------------------");
            try {
                Page<Sku> skuPage = SkroutzClient.getInstance().getNested(categorySelect.getValue(), Sku.class);
                while (skuPage != null && skuPage.getCurrentPage() < 10 && (skuPage.hasNext() || skuPage.isLastPage())) {
//                while (skuPage != null && (skuPage.hasNext() || skuPage.isLastPage())) {
                    String logSku = "Κατέβηκαν " + skuPage.getPer() + " μοναδικά προϊόντα.";
                    log.info(logSku);
                    logArea.setValue(logArea.getValue() + "\n" + logSku);
                    ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(Sku.class, skuPage.getItems());
                    for (Sku sku : skuPage.getItems()) {
                        Sku dbSku = ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().find(Sku.class, sku.getSkroutzId());
                        if (dbSku == null || dbSku.getProducts() == null || dbSku.getProducts().isEmpty()) {
                            List<Product> products = SkroutzClient.getInstance().getNestedAsList(sku, Product.class);
                            if (products != null) {
                                ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(Product.class, products);
                                for (Product p :
                                        products) {
                                    Price price = Price.fromProduct(p);
                                    if (p.getPrices() == null) {
                                        p.setPrices(new ArrayList<>());
                                    }
                                    if (p.getPrices().stream().noneMatch(tp -> tp.getPrice() == price.getPrice())) {
                                        p.getPrices().add(price);
                                    }
                                }


                                String logProducts = "Κατέβηκαν " + products.size() + " τιμές για το προϊόν " + sku.getName() + ".";
                                log.info(logProducts);
                                logArea.setValue(logArea.getValue() + "\n" + logProducts);
                            }
                        }
                    }
                    if (!skuPage.isLastPage()) {
                        skuPage = SkroutzClient.getInstance().getNextPage(skuPage);
                    }
                }
                String logComplete = "To κατέβασμα ολοκληρώθηκε.";
                log.info(logComplete);
                logArea.setValue(logArea.getValue() + "\n" + logComplete);
                crawlButton.setEnabled(true);
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
                String exception = "Παρουσιάστηκε exception.";
                logArea.setValue(logArea.getValue() + "\n" + exception);

            }

        });

        Button clearButton = new Button("Καθαρισμός log");
        clearButton.addClickListener(clickEvent -> logArea.clear());


        categoryLayout.addComponent(categorySelect);
        categoryLayout.addComponent(flexibilitySelect);
        categoryLayout.addComponent(crawlButton);
        categoryLayout.addComponent(clearButton);
        return categoryLayout;

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }
}
