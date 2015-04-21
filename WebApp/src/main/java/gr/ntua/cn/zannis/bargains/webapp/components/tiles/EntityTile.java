package gr.ntua.cn.zannis.bargains.webapp.components.tiles;

import com.vaadin.ui.*;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public abstract class EntityTile<T extends SkroutzEntity> extends Panel {

    protected final T entity;
    protected Embedded image;
    protected Label caption;

    public EntityTile(T entity) {
        this.entity = entity;
        buildUI();
    }

    private void buildUI() {
        setSizeUndefined();
//        setStyleName("custom-tile");

        VerticalLayout layout = new VerticalLayout();

        caption = new Label();
        renderComponents();

        // only set height, width will be adjusted accordingly
        image.setHeight("160px");

        caption.setWidthUndefined();

        layout.addComponent(image);
        layout.addComponent(caption);
        layout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(caption, Alignment.MIDDLE_CENTER);

        setContent(layout);
    }

    public T getEntity() {
        return entity;
    }

    protected abstract void renderComponents();

}
