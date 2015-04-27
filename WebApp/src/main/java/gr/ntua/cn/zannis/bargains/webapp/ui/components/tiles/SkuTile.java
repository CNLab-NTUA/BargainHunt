package gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class SkuTile extends EntityTile<Sku> {

    public SkuTile(Sku sku) {
        super(sku);
    }

    public SkuTile(Sku entity, MouseEvents.ClickListener listener) {
        super(entity, listener);
    }

    @Override
    protected void renderComponents() {
        if (this.entity.getImages() == null || this.entity.getImages().getMain().isEmpty()) {
            image.setSource(new ThemeResource(DEFAULT_IMAGE_URL));
        } else {
            image.setSource(new ExternalResource(this.entity.getImages().getMain()));
        }
        if (this.entity.getName() == null || this.entity.getName().isEmpty()) {
            caption.setValue(DEFAULT_NAME);
        } else {
            caption.setValue(this.entity.getName());
        }
    }
}
