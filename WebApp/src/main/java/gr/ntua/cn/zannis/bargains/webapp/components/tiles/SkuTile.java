package gr.ntua.cn.zannis.bargains.webapp.components.tiles;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class SkuTile extends EntityTile<Sku> {

    public SkuTile(Sku sku) {
        super(sku);
    }

    @Override
    protected void renderComponents() {
        if (this.entity.getImages() == null || this.entity.getImages().getMain().isEmpty()) {
            image = new Embedded(null, new ThemeResource("img/default-product.png"));
        } else {
            image = new Embedded(null, new ExternalResource(this.entity.getImages().getMain()));
        }
        if (this.entity.getName() == null || this.entity.getName().isEmpty()) {
            caption.setValue("Άκυρο προϊόν");
        } else {
            caption.setValue(this.entity.getName());
        }
    }
}
