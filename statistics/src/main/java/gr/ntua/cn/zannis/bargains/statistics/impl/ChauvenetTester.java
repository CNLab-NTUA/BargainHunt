package gr.ntua.cn.zannis.bargains.statistics.impl;

import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class ChauvenetTester extends BaseTester {

    private static final Logger log = LoggerFactory.getLogger(ChauvenetTester.class);

    public ChauvenetTester(Flexibility flexibility) {
        super(flexibility);
        setSignificanceValue(flexibility);
        log.info("Created new ChauvenetTester with " + flexibility.name() + " flexibility.");
    }

    public ChauvenetTester() {
        this(DEFAULT_FLEXIBILITY);
    }

    private void init() {

    }

    @Override
    public Float getMinimumOutlier(List<Float> sample, Flexibility strength) {
        return null;
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample) {
        return null;
    }

    @Override
    public void setValues(List<Float> values) {
        super.setValues(values);
        init();
    }
}
