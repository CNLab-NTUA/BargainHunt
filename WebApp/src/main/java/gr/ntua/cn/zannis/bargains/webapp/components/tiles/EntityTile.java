package gr.ntua.cn.zannis.bargains.webapp.components.tiles;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public abstract class EntityTile<T extends SkroutzEntity> extends VerticalLayout {

    protected final T entity;
    protected Embedded image;
    protected Label caption;

    public EntityTile(T entity) {
        this.entity = entity;
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setStyleName("custom-view");

        caption = new Label();
        renderComponents();

        // only set height, width will be adjusted accordingly
        image.setHeight("160px");

        caption.setWidthUndefined();

        addComponent(image);
        addComponent(caption);
        setComponentAlignment(image, Alignment.MIDDLE_CENTER);
        setComponentAlignment(caption, Alignment.MIDDLE_CENTER);
    }

    public T getEntity() {
        return entity;
    }

    protected abstract void renderComponents();

}
