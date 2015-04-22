package gr.ntua.cn.zannis.bargains.webapp.components.tiles;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
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
            image.setSource(new ThemeResource(DEFAULT_IMAGE_URL));
        } else {
            image.setSource(new ExternalResource(this.entity.getImageUrl()));
        }
        if (this.entity.getName() == null || this.entity.getName().isEmpty()) {
            caption.setValue(DEFAULT_NAME);
        } else {
            caption.setValue(this.entity.getName());
        }
    }
}
