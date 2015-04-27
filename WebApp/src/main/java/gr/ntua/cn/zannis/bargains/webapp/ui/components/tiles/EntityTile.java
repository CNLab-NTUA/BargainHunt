package gr.ntua.cn.zannis.bargains.webapp.ui.components.tiles;

import com.vaadin.event.MouseEvents;
import com.vaadin.ui.*;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public abstract class EntityTile<T extends SkroutzEntity> extends Panel {

    protected static final String DEFAULT_NAME = "Χωρίς όνομα";
    protected static final String DEFAULT_IMAGE_URL = "img/default-image.jpg";

    protected final T entity;
    protected Embedded image;
    protected Label caption;
    
    public EntityTile(T entity) {
        this.entity = entity;
        buildUI();
    }

    public EntityTile(T entity, MouseEvents.ClickListener listener) {
        this.entity = entity;
        addClickListener(listener);
    }

    private void buildUI() {
        setSizeUndefined();

        VerticalLayout layout = new VerticalLayout();

        caption = new Label();
        image = new Embedded();
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
