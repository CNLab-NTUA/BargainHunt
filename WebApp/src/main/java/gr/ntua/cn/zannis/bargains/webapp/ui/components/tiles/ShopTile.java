package gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Shop;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class ShopTile extends EntityTile<Shop> {

    public ShopTile(Shop entity) {
        super(entity);
    }

    public ShopTile(Shop entity, MouseEvents.ClickListener listener) {
        super(entity, listener);
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
