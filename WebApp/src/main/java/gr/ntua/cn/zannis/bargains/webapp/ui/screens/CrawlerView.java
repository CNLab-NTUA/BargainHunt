package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import gr.ntua.cn.zannis.bargains.webapp.crawler.logging.CustomAppender;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzOldRestClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@CDIView(CrawlerView.NAME)
@SuppressWarnings("serial")
public class CrawlerView extends VerticalLayout implements View {

    public static final String NAME = "crawler";

    public static final Logger log = LoggerFactory.getLogger(CrawlerView.class);
    private JPAContainer<Category> categories = JPAContainerFactory.make(Category.class, BargainHuntUI.PERSISTENCE_UNIT);
    private Button crawlButton;
    private Button clearButton;
    private TextArea logArea;

    public CrawlerView() {
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setMargin(true);
        // build logo
        Label logo = new Label("Skroutz Crawler");
        logo.setSizeUndefined();
        logo.setStyleName(ValoTheme.LABEL_HUGE);

        VerticalLayout content = new VerticalLayout();

        // build category chooser panel
        HorizontalLayout categoryLayout = buildPanel();
        categoryLayout.setSizeFull();

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
        content.addComponent(logArea);
        content.setExpandRatio(categoryLayout, 0.1f);
        content.setExpandRatio(logArea, 1f);
        content.setSizeFull();
        // add to ui
        addComponent(logo);
        addComponent(content);
        setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        setComponentAlignment(content, Alignment.MIDDLE_CENTER);
        setExpandRatio(logo, 0.2f);
        setExpandRatio(content, 0.8f);

        // init log appender
        CustomAppender.logTextArea = logArea;
        try {
            Properties loggingProperties = new Properties();
            loggingProperties.load(this.getClass().getClassLoader().getResourceAsStream("log4j-config.properties"));

            PropertyConfigurator.configure(loggingProperties);
        } catch (IOException e) {
            log.error("IO exception στα properties.");
            e.printStackTrace();
        }
    }

    private HorizontalLayout buildPanel() {
        HorizontalLayout categoryLayout = new HorizontalLayout();
        categoryLayout.setSizeFull();

        // fetch categories from db
//        categories = ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().findAll(Category.class);

        // build panel ui
        ComboBox categorySelect = new ComboBox("Επιλέξτε κατηγορία :", categories);
        categorySelect.setItemCaptionPropertyId("name");
        categorySelect.addValueChangeListener(valueChangeEvent -> {
            if (categorySelect.getValue() != null) {
                crawlButton.setEnabled(true);
            } else {
                crawlButton.setEnabled(false);
            }
        });

        crawlButton = new Button("Λήψη προϊόντων");
        crawlButton.setDisableOnClick(true);
        crawlButton.setEnabled(false);
        crawlButton.addClickListener(clickEvent -> {
            log.info("Κατέβασμα προϊόντων της κατηγορίας " + categories.getItem(categorySelect.getValue()).getEntity().getName());
            log.info("---------------------------------------------------------------------------------------");
            Page<Sku> skuPage = SkroutzOldRestClient.getInstance().getNested((categories.getItem(categorySelect.getValue()).getEntity()), Sku.class);
            while (skuPage != null && (skuPage.hasNext() || skuPage.isLastPage())) {
                log.info("Κατέβηκαν " + skuPage.getPer() + " προϊόντα.");
                ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(Sku.class, skuPage.getItems());
                for (Sku sku : skuPage.getItems()) {
                    List<Product> products = SkroutzOldRestClient.getInstance().getAllNested(sku, Product.class);
                    if (products != null) {
                        ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(Product.class, products);
                    }
                }

                skuPage = SkroutzOldRestClient.getInstance().getNextPage(skuPage);
            }
            log.info("To κατέβασμα ολοκληρώθηκε επιτυχώς.");
            crawlButton.setEnabled(true);
        });

        clearButton = new Button("Καθαρισμός log");
        clearButton.addClickListener(clickEvent -> logArea.clear());


        categoryLayout.addComponent(categorySelect);
        categoryLayout.addComponent(crawlButton);
        categoryLayout.addComponent(clearButton);
        return categoryLayout;

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
