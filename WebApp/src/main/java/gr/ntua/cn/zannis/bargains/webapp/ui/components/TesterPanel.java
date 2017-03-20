package gr.ntua.cn.zannis.bargains.webapp.ui.components;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import gr.ntua.cn.zannis.bargains.statistics.Tester;
import gr.ntua.cn.zannis.bargains.statistics.impl.ChauvenetTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.GrubbsTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.QuartileTester;
import org.apache.commons.math3.exception.NullArgumentException;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class TesterPanel extends HorizontalLayout {

    private final CheckBox testerOn;
    private Tester tester;
    private Tester.Flexibility flexibility;

    public TesterPanel(Tester.TestType type) {
        this.setSizeFull();

        if (type == null) {
            throw new NullArgumentException();
        }
        String caption = "Έλεγχος ";
        switch (type) {
            case GRUBBS:
                caption = caption.concat("Grubbs");
                tester = new GrubbsTester();
                break;
            case CHAUVENET:
                caption = caption.concat("Chauvenet");
                tester = new ChauvenetTester();
                break;
            case QUARTILE:
                caption = caption.concat("Τεταρτημορίων");
                tester = new QuartileTester();
                break;
        }

        testerOn = new CheckBox(caption, true);

        ComboBox<String> flexibilitySelect = new ComboBox<>("Εύρος εμπιστοσύνης :");
        flexibilitySelect.setItems("Χαμηλό", "Κανονικό", "Υψηλό");
        flexibilitySelect.setValue("Κανονικό");
        flexibilitySelect.addValueChangeListener(event -> {
            if (flexibilitySelect.getValue().equals("Χαμηλό")) {
                flexibility = Tester.Flexibility.RELAXED;
                tester.setFlexibility(Tester.Flexibility.RELAXED);
            } else if (flexibilitySelect.getValue().equals("Κανονικό")) {
                flexibility = Tester.Flexibility.NORMAL;
                tester.setFlexibility(Tester.Flexibility.NORMAL);
            } else if (flexibilitySelect.getValue().equals("Υψηλό")) {
                flexibility = Tester.Flexibility.STRONG;
                tester.setFlexibility(Tester.Flexibility.STRONG);
            } else {
                flexibility = Tester.Flexibility.DEFAULT;
                tester.setFlexibility(Tester.Flexibility.DEFAULT);
            }
        });

        this.addComponent(testerOn);
        this.addComponent(flexibilitySelect);
    }

    public Tester getTester() {
        if (testerOn.getValue()) {
            return tester;
        }
        return null;
    }

    public Tester.Flexibility getFlexibility() {
        return flexibility;
    }
}
