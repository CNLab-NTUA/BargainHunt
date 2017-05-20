package gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Offer;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.ui.BargainHuntUI;
import org.apache.commons.math3.stat.StatUtils;

/**
 * A tile class for the Offer object, a reference to a product that is
 * currently in sale.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class OfferTile extends Panel {

    private static final String DEFAULT_NAME = "Χωρίς όνομα";
    private static final String DEFAULT_IMAGE_URL = "img/default-image.jpg";

    private Offer entity;
    private Embedded image;
    private Label productName;
    private Label offerPercentage;
    private Label shopName;

    public OfferTile(Offer entity) {
        this.entity = entity;
        buildUI();
    }

    public OfferTile(Offer entity, MouseEvents.ClickListener listener) {
        this.entity = entity;
        addClickListener(listener);
    }

    private void buildUI() {
        setSizeUndefined();

        VerticalLayout layout = new VerticalLayout();

        productName = new Label();
        offerPercentage = new Label();
        shopName = new Label();
        image = new Embedded();
        renderComponents();

        image.setHeight("160px");

        productName.setWidth("200px");
        offerPercentage.setWidth("200px");
        shopName.setWidth("200px");

        layout.addComponent(image);
        layout.addComponent(productName);
//        layout.addComponent(shopName);
        layout.addComponent(offerPercentage);
        layout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(productName, Alignment.MIDDLE_CENTER);
//        layout.setComponentAlignment(shopName, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(offerPercentage, Alignment.BOTTOM_RIGHT);

        addClickListener(event -> Page.getCurrent().open(this.entity.getProduct().getClickUrl(), null));
        setContent(layout);
    }

    public Offer getEntity() {
        return entity;
    }

    private void renderComponents() {
        Sku sku = ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().find(Sku.class, this.entity.getProduct().getSkuId());
        if (this.entity.getProduct() == null || this.entity.getProduct().getSku() == null) {
            image.setSource(new ThemeResource(DEFAULT_IMAGE_URL));
        } else {
            if (sku.getImage() != null) {
                image.setSource(new ExternalResource(sku.getImage()));
            } else {
                image.setSource(new ExternalResource(sku.getCategory().getImageUrl()));
            }
            if (this.entity.getProduct().getName() == null || this.entity.getProduct().getName().isEmpty()) {
                productName.setValue(DEFAULT_NAME);
            } else {
                productName.setValue(this.entity.getProduct().getName());
            }
            if (this.entity.getProduct().getShop() == null || this.entity.getProduct().getShop().getName().isEmpty()) {
                shopName.setValue("στο " + this.entity.getProduct().getShop().getName());
            }
        }
        if (this.entity.getPrice() == null) {
            offerPercentage.setValue(DEFAULT_NAME);
        } else {
            double bargainPercentage = (1d - ((double) this.entity.getPrice().getPrice()) / StatUtils.mean(sku.getProducts().stream().mapToDouble(Product::getPrice).toArray())) * 100;
            offerPercentage.setValue("Τιμή : " + this.entity.getPrice().getPrice() + "€, Έκπτωση : " + String.format("%,.2f", bargainPercentage) + "%");
        }

    }

    public Label getShopName() {
        return shopName;
    }

    public Label getOfferPercentage() {
        return offerPercentage;
    }

    public Label getProductName() {
        return productName;
    }
}
