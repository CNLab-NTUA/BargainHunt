package gr.ntua.cn.zannis.bargains.webapp.ui.screens;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.MouseEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author zannis <zannis.kal@gmail.com
 */
@CDIView(InitView.NAME)
public class InitView extends VerticalLayout implements View, MouseEvents.ClickListener {

    public static final String NAME = "init";
    private static final long serialVersionUID = -2974526790845822118L;
    private final Button categoriesButton = new Button("Batch Updates");

    public InitView() {
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setMargin(true);
        Label logo = new Label("Search for offers");
        logo.setSizeUndefined();
        logo.setStyleName(ValoTheme.LABEL_HUGE);

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(logo);


        categoriesButton.addClickListener(event -> {
//            List<Category> categories = SkroutzClient.getInstance().getAllCategories();
//            ((BargainHuntUI) UI.getCurrent()).getSkroutzEm().persistOrMerge(Category.class, categories);
        });
    }

    @Override
    public void click(MouseEvents.ClickEvent clickEvent) {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
