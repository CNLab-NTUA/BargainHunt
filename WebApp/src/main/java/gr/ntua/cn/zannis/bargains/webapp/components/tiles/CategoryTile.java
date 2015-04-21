package gr.ntua.cn.zannis.bargains.webapp.components.tiles;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.webapp.components.Notifier;
import gr.ntua.cn.zannis.bargains.webapp.screens.ProductsView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class CategoryTile extends EntityTile<Category> {

    public CategoryTile(Category category) {
        super(category);
    }

    @Override
    protected void renderComponents() {
        if (this.entity != null) {
            if (this.entity.getImageUrl() == null || this.entity.getImageUrl().isEmpty()) {
                image = new Embedded(null, new ThemeResource("img/default-category.png"));
            } else {
                image = new Embedded(null, new ExternalResource(this.entity.getImageUrl()));
            }
            if (this.entity.getName() == null || this.entity.getName().isEmpty()) {
                caption.setValue("Κενή κατηγορία");
            } else {
                caption.setValue(this.entity.getName());
            }
        } else {
            image = new Embedded(null, new ThemeResource("img/default-category.png"));
            caption.setValue("Κενή κατηγορία");
        }
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent layoutClickEvent) {
        try {
            getUI().getNavigator().navigateTo(ProductsView.NAME + "/"
                    + getEntity().getSkroutzId()
                    + "/" + URLEncoder.encode(query, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Notifier.error("Προβλημα στο encoding", e);
        }
    }
}
