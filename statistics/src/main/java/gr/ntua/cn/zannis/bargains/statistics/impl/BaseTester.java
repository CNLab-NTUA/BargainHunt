package gr.ntua.cn.zannis.bargains.statistics.impl;

import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.Tester;

import java.util.Collections;
import java.util.List;

/**
 * The base class that defines the necessary attributes in order to perform outline detection.
 *
 * @author zannis zannis.kal@gmail.com
 */
public abstract class BaseTester implements Tester {

    protected final static Flexibility DEFAULT_FLEXIBILITY = Flexibility.NORMAL;
    protected List<Float> values;
    protected double[] doubleValues;
    protected Flexibility flexibility;
    protected double significanceValue;
    protected double[] criticalValues;


    /**
     * Full constructor for <code>BaseTester</code> specifying the flexibility.
     * @param flexibility The value of flexibility to use.
     */
    public BaseTester(Flexibility flexibility) {
        setFlexibility(flexibility);
    }

    /**
     * Constructor for the <code>BaseTester</code> using the default filter flexibility.
     */
    public BaseTester() {
        this(DEFAULT_FLEXIBILITY);
    }

    /**
     * Get the minimum value of the sample.
     * @return The minimum value.
     */
    protected Float getMinimumValue() {
        if (!this.values.isEmpty()) {
            return Collections.min(values);
        } else {
            return null;
        }
    }

    /**
     * A simple {@link List<Float>} to <code>double[]</code> converter. Required to work with
     * Apache Math3 library.
     * @param list The list of floats to convert.
     */
    private void setDoubleValues(List<Float> list) {
        this.doubleValues = new double[list.size()];
        int i = 0;
        for (Float f : list) {
            doubleValues[i++] = (f != null ? (double) f : 0);
        }
    }

    public List<Float> getValues() {
        return !this.values.isEmpty() ? values : Collections.emptyList();
    }

    public void setValues(List<Float> values) {
        this.values = values;
        Collections.sort(values);
        setDoubleValues(values);
    }

    public Flexibility getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(Flexibility flexibility) {
        this.flexibility = flexibility;
    }

    /**
     * Set the significance value <i>alpha</i> based on the given flexibility.
     * @param flexibility The specified flexibility.
     */
    void setSignificanceValue(Flexibility flexibility) {
        switch (flexibility) {
            case RELAXED:
                this.significanceValue = 0.1;
                break;
            case NORMAL:
                this.significanceValue = 0.05;
                break;
            case STRONG:
                this.significanceValue = 0.01;
                break;
            case DEFAULT:
                this.significanceValue = 0.05;
                break;
        }
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
    void setCriticalValues() {
        switch (flexibility) {
            case RELAXED:
                criticalValues = new double[] {
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
                criticalValues = new double[] {
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
                criticalValues = new double[] {
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
        }
    }
}
