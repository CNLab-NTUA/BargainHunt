package gr.ntua.cn.zannis.bargains.webapp.ui.components;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.TestType;
import gr.ntua.cn.zannis.bargains.statistics.Tester;
import gr.ntua.cn.zannis.bargains.statistics.impl.ChauvenetTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.GrubbsTester;
import gr.ntua.cn.zannis.bargains.statistics.impl.QuartileTester;
import org.apache.commons.math3.exception.NullArgumentException;
import org.vaadin.captionpositions.CaptionPositions;
import org.vaadin.captionpositions.client.CaptionPosition;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class TesterPanel extends HorizontalLayout {

    private final CheckBox testerOn;
    private Tester tester;
    private Flexibility flexibility;
    private final TestType type;

    public TesterPanel(TestType type) {
        this.setSizeFull();
        this.setHeight("80px");

        if (type == null) {
            throw new NullArgumentException();
        }
        this.type = type;
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
        flexibilitySelect.addValueChangeListener(event -> {
            if (flexibilitySelect.getValue().equals("Χαμηλό")) {
                flexibility = Flexibility.RELAXED;
                tester.setFlexibility(Flexibility.RELAXED);
            } else if (flexibilitySelect.getValue().equals("Κανονικό")) {
                flexibility = Flexibility.NORMAL;
                tester.setFlexibility(Flexibility.NORMAL);
            } else if (flexibilitySelect.getValue().equals("Υψηλό")) {
                flexibility = Flexibility.STRONG;
                tester.setFlexibility(Flexibility.STRONG);
            } else {
                flexibility = Flexibility.NORMAL;
                tester.setFlexibility(Flexibility.NORMAL);
            }
        });
        flexibilitySelect.setValue("Υψηλό");


        this.addComponent(testerOn);
        this.addComponent(flexibilitySelect);
        // set checkbox caption to the left
        new CaptionPositions(this).setCaptionPosition(flexibilitySelect, CaptionPosition.LEFT);

    }

    public Tester getTester() {
        if (testerOn.getValue()) {
            return tester;
        }
        return null;
    }

    public Flexibility getFlexibility() {
        return flexibility;
    }
}
