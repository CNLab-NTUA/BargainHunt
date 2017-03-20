package gr.ntua.cn.zannis.bargains.statistics.impl;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class ChauvenetTester extends BaseTester {

    private static final Logger log = LoggerFactory.getLogger(ChauvenetTester.class);

    /**
     * Constructor for a Tester using the Chauvenet outlier test with a custom flexibility
     * parameter.
     *
     * @param flexibility The flexibility value to use.
     */
    public ChauvenetTester(Flexibility flexibility) {
        super(flexibility);
        setSignificanceValue(flexibility);
        log.info("Created new ChauvenetTester with " + flexibility.name() + " flexibility.");
    }

    public ChauvenetTester() {
        this(DEFAULT_FLEXIBILITY);
    }

    @Override
    public Float getBargainPercentage() {
        float mean = (float) StatUtils.mean(this.doubleValues);
        return (mean - getMinimumOutlier(this.values)) / mean * 100;
    }

    private void init() {
        setChauvenetCriticalValues();
    }

    /**
     * Helper method to retrieve the critical values for the Chauvenet Outlier Test.
     */
    private void setChauvenetCriticalValues() {
        this.criticalValues = new double[]{
                1.383, 1.534, 1.645, 1.732, 1.803, 1.863, 1.915, 1.960, 2.000, 2.037, 2.070,
                2.100, 2.128, 2.154, 2.178, 2.200, 2.222, 2.241, 2.260, 2.278, 2.295, 2.311,
                2.326, 2.341, 2.355, 2.369, 2.382, 2.394, 2.406, 2.418, 2.429, 2.440, 2.450,
                2.460, 2.470, 2.479, 2.489, 2.498, // 4-40
                2.576, // 50
                2.807, // 100
                3.291, // 500
                3.481  // 1000
        };
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample, Flexibility flexibility) {
        setFlexibility(flexibility);
        return getMinimumOutlier(sample);
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample) {
        setValues(sample);
        double chauvenetValue = selectChauvenetValue(this.doubleValues.length);
        double mean = StatUtils.mean(this.doubleValues);
        double s = new StandardDeviation().evaluate(this.doubleValues, mean);
        double x = this.doubleValues[0];
        double standardisedDeviation = new Abs().value(mean - x) / s;
        if (standardisedDeviation > chauvenetValue) return getMinimumValue();
        else return Float.NaN;
    }

    @Override
    public TestType getType() {
        return TestType.CHAUVENET;
    }

    private double selectChauvenetValue(int length) {
        if (length <= 3) {
            return Double.NaN;
        } else if (length <= 40) {
            return this.criticalValues[length - 4];
        } else if (length <= 60) {
            return this.criticalValues[38];
        } else if (length <= 120) {
            return this.criticalValues[39];
        } else if (length <= 550) {
            return this.criticalValues[40];
        } else {
            return this.criticalValues[41];
        }
    }

    @Override
    public void setValues(List<Float> values) {
        super.setValues(values);
        init();
    }
}
