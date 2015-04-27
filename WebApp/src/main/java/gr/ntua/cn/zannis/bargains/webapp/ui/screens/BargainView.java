package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

/**
 * @author zannis <zannis.kal@gmail.com
 */
@CDIView(BargainView.NAME)
public class BargainView extends VerticalLayout implements View {
    public static final String NAME = "bargains";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
