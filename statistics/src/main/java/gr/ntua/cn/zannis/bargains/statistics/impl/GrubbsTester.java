package gr.ntua.cn.zannis.bargains.statistics.impl;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.TestUtils;
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

    @Override
    public Float getBargainPercentage() {
        float mean = (float) StatUtils.mean(this.doubleValues);
        return (mean - getMinimumOutlier(this.values)) / mean * 100;
    }

    /**
     * Method to init required values, gets called by <code>setValues</code>}
     */
    private void init() {
        setTCriticalValues();
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


    /**
     * Helper method to set the critical values for a T-distribution given a flexibility level.
     * The criticalValues array contains 101 values where the first 100 are the respective critical
     * values for a sample of i+1 degrees of freedom, and the last one is the critical value for infinity,
     * to be used with any sample with size larger than 100.
     * The flexibility levels match with the <i>alpha</i> variable, this means that:
     * NORMAL  : α = 0.95
     * RELAXED : α = 0.90
     * STRONG  : α = 0.99
     */
    private void setTCriticalValues() {
        switch (flexibility) {
            case RELAXED:
                this.criticalValues = new double[]{
                        3.078, 1.886, 1.638, 1.533, 1.476, 1.440, 1.415, 1.397, 1.383, 1.372, 1.363, 1.356, 1.350,
                        1.345, 1.341, 1.337, 1.333, 1.330, 1.328, 1.325, 1.323, 1.321, 1.319, 1.318, 1.316, 1.315,
                        1.314, 1.313, 1.311, 1.310, 1.309, 1.309, 1.308, 1.307, 1.306, 1.306, 1.305, 1.304, 1.304,
                        1.303, 1.303, 1.302, 1.302, 1.301, 1.301, 1.300, 1.300, 1.299, 1.299, 1.299, 1.298, 1.298,
                        1.298, 1.297, 1.297, 1.297, 1.297, 1.296, 1.296, 1.296, 1.296, 1.295, 1.295, 1.295, 1.295,
                        1.295, 1.294, 1.294, 1.294, 1.294, 1.294, 1.293, 1.293, 1.293, 1.293, 1.293, 1.293, 1.292,
                        1.292, 1.292, 1.292, 1.292, 1.292, 1.292, 1.292, 1.291, 1.291, 1.291, 1.291, 1.291, 1.291,
                        1.291, 1.291, 1.291, 1.291, 1.290, 1.290, 1.290, 1.290, 1.290, 1.282
                };
                break;
            case NORMAL:
                this.criticalValues = new double[]{
                        6.314, 2.920, 2.353, 2.132, 2.015, 1.943, 1.895, 1.860, 1.833, 1.812, 1.796, 1.782, 1.771,
                        1.761, 1.753, 1.746, 1.740, 1.734, 1.729, 1.725, 1.721, 1.717, 1.714, 1.711, 1.708, 1.706,
                        1.703, 1.701, 1.699, 1.697, 1.696, 1.694, 1.692, 1.691, 1.690, 1.688, 1.687, 1.686, 1.685,
                        1.684, 1.683, 1.682, 1.681, 1.680, 1.679, 1.679, 1.678, 1.677, 1.677, 1.676, 1.675, 1.675,
                        1.674, 1.674, 1.673, 1.673, 1.672, 1.672, 1.671, 1.671, 1.670, 1.670, 1.669, 1.669, 1.669,
                        1.668, 1.668, 1.668, 1.667, 1.667, 1.667, 1.666, 1.666, 1.666, 1.665, 1.665, 1.665, 1.665,
                        1.664, 1.664, 1.664, 1.664, 1.663, 1.663, 1.663, 1.663, 1.663, 1.662, 1.662, 1.662, 1.662,
                        1.662, 1.661, 1.661, 1.661, 1.661, 1.661, 1.661, 1.660, 1.660, 1.645
                };
                break;
            case STRONG:
                this.criticalValues = new double[]{
                        31.821, 6.965, 4.541, 3.747, 3.365, 3.143, 2.998, 2.896, 2.821, 2.764, 2.718, 2.681, 2.650,
                        2.624, 2.602, 2.583, 2.567, 2.552, 2.539, 2.528, 2.518, 2.508, 2.500, 2.492, 2.485, 2.479,
                        2.473, 2.467, 2.462, 2.457, 2.453, 2.449, 2.445, 2.441, 2.438, 2.434, 2.431, 2.429, 2.426,
                        2.423, 2.421, 2.418, 2.416, 2.414, 2.412, 2.410, 2.408, 2.407, 2.405, 2.403, 2.402, 2.400,
                        2.399, 2.397, 2.396, 2.395, 2.394, 2.392, 2.391, 2.390, 2.389, 2.388, 2.387, 2.386, 2.385,
                        2.384, 2.383, 2.382, 2.382, 2.381, 2.380, 2.379, 2.379, 2.378, 2.377, 2.376, 2.376, 2.375,
                        2.374, 2.374, 2.373, 2.373, 2.372, 2.372, 2.371, 2.370, 2.370, 2.369, 2.369, 2.368, 2.368,
                        2.368, 2.367, 2.367, 2.366, 2.366, 2.365, 2.365, 2.365, 2.364, 2.326
                };
                break;
            default:
                this.criticalValues = new double[]{
                        6.314, 2.920, 2.353, 2.132, 2.015, 1.943, 1.895, 1.860, 1.833, 1.812, 1.796, 1.782, 1.771,
                        1.761, 1.753, 1.746, 1.740, 1.734, 1.729, 1.725, 1.721, 1.717, 1.714, 1.711, 1.708, 1.706,
                        1.703, 1.701, 1.699, 1.697, 1.696, 1.694, 1.692, 1.691, 1.690, 1.688, 1.687, 1.686, 1.685,
                        1.684, 1.683, 1.682, 1.681, 1.680, 1.679, 1.679, 1.678, 1.677, 1.677, 1.676, 1.675, 1.675,
                        1.674, 1.674, 1.673, 1.673, 1.672, 1.672, 1.671, 1.671, 1.670, 1.670, 1.669, 1.669, 1.669,
                        1.668, 1.668, 1.668, 1.667, 1.667, 1.667, 1.666, 1.666, 1.666, 1.665, 1.665, 1.665, 1.665,
                        1.664, 1.664, 1.664, 1.664, 1.663, 1.663, 1.663, 1.663, 1.663, 1.662, 1.662, 1.662, 1.662,
                        1.662, 1.661, 1.661, 1.661, 1.661, 1.661, 1.661, 1.660, 1.660, 1.645
                };
                break;
        }
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample, Flexibility flexibility) {
        setFlexibility(flexibility);
        return getMinimumOutlier(sample);
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample) {
        setValues(sample);
        int N = this.doubleValues.length;
        double criticalValue = TestUtils.tTest(this.getMinimumValue(), this.doubleValues) / 2;
        double testValue = (N - 1)/Math.sqrt(N)
                * Math.sqrt((criticalValue*criticalValue)/(N - 2 + criticalValue*criticalValue));
        double grubbsValue = calculateGrubbsTestCriterion(0);
        return grubbsValue > testValue ? (float) this.doubleValues[0] : Float.NaN;
    }

    @Override
    public TestType getType() {
        return TestType.GRUBBS;
    }

    @Override
    public void setValues(List<Float> values) {
        super.setValues(values);
        init();
    }
}
