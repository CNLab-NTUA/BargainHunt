package gr.ntua.cn.zannis.bargains.webapp.components;

import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class ResponsiveGridLayout extends CssLayout {

    public ResponsiveGridLayout() {
        setSizeFull();
        addStyleName("responsive-grid");
        Responsive.makeResponsive(this);
    }
}
