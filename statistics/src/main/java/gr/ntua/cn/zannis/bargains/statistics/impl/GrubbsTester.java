package gr.ntua.cn.zannis.bargains.statistics.impl;

import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class GrubbsTester extends BaseTester {

    private static final Logger log = LoggerFactory.getLogger(GrubbsTester.class);

    /**
     * Constructor for a Tester using the Grubbs outlier test with a custom flexibility
     * parameter.
     * @param flexibility The flexibility value to use.
     */
    public GrubbsTester(Flexibility flexibility) {
        super(flexibility);
        setSignificanceValue(flexibility);
        log.info("Created new GrubbsTester with " + flexibility.name() + " flexibility.");
    }

    /**
     * Constructor for a Tester using the Grubbs outlier test using the default flexibility
     * parameter of a=0.05.
     */
    public GrubbsTester() {
        this(DEFAULT_FLEXIBILITY);
    }

    /**
     * Method to init required values, gets called by <code>setValues</code>}
     */
    private void init() {
       setCriticalValues();
    }

    /**
     * Method to calculate Grubbs criterion for the value at the given index of the
     * sorted array.
     * @param index The position of the value in the array. 0 for the standard procedure.
     * @return The value T_n as described by the Grubbs criterion
     */
    private double calculateGrubbsTestCriterion(int index) {
        double mean = StatUtils.mean(this.doubleValues);
        double s    = new StandardDeviation().evaluate(this.doubleValues, mean);
        double x    = this.doubleValues[index];
        return new Abs().value(mean - x)/s;
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample, Flexibility strength) {
        setFlexibility(strength);
        return getMinimumOutlier(sample);
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample) {
        setValues(sample);
        int N = this.doubleValues.length;
        double criticalValue = this.criticalValues[N - 1];
        double testValue = (N - 1)/Math.sqrt(N)
                * Math.sqrt((criticalValue*criticalValue)/(N - 2 + criticalValue*criticalValue));
        return calculateGrubbsTestCriterion(0) > testValue ? (float) this.doubleValues[0] : Float.NaN;
    }

    @Override
    public void setValues(List<Float> values) {
        super.setValues(values);
        init();
    }
}
