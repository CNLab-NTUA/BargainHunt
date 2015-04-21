package gr.ntua.cn.zannis.bargains.webapp.components.tiles;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class CategoryTile extends EntityTile<Category> {

    public CategoryTile(Category category) {
        super(category);
    }

    @Override
    protected void renderComponents() {
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
    }
}
