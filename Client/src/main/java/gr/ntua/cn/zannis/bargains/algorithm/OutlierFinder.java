package gr.ntua.cn.zannis.bargains.algorithm;

import org.apache.commons.math3.stat.StatUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class that finds the outliers in a given float list using the interquantile range method.
 * The <code>k</code> in the formula is based on the {@link FilterStrength} parameter.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Outlier">Wikipedia: Outlier</a>
 * @author zannis <zannis.kal@gmail.com>
 */
public class OutlierFinder {

    private final static FilterStrength DEFAULT_FILTER_STRENGTH = FilterStrength.NORMAL;

    private final List<Float> values;
    private float kappa;
    private final double q1;
    private final double q3;
    private final double IQR;


    /**
     * Main constructor that computes all the needed values to perform the calculations.
     * @param floatValues The {@link List<Float>} with the values to calculate outliers.
     * @param strength The strength of the filtering function, determines the <code>k</code> parameter
     *                 in the calculation formula.
     */
    public OutlierFinder(List<Float> floatValues, FilterStrength strength) {
        setFilterStrength(strength);
        this.values = floatValues;
        // sort the list to compute the quantiles
        Collections.sort(values);
        // compute the quantiles
        this.q1 = StatUtils.percentile(floatListToDoubleArrayConverter(values), 25);
        this.q3 = StatUtils.percentile(floatListToDoubleArrayConverter(values), 75);
        // compute the interquantile range
        IQR = Math.abs(q3 - q1);
    }

    /**
     * Constructor for the <code>OutlierFinder</code> using the default filter strength.
     * @param floatValues The {@link List<Float>} to perform the calculations on.
     */
    public OutlierFinder(List<Float> floatValues) {
        this(floatValues, DEFAULT_FILTER_STRENGTH);
    }

    public Float getMin() {
        return Collections.min(this.values);
    }

    /**
     * Method to extract the outliers that are lower than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    public List<Float> getLowOutliers() {
        // return
        return values.stream().filter(aFloat -> aFloat < q1 - kappa * Math.abs(q3 - q1)).collect(Collectors.toList());
    }

    /**
     * Method to extract the outliers that are higher than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    public List<Float> getHighOutliers() {
        return values.stream().filter(aFloat -> aFloat > q3 + kappa * IQR).collect(Collectors.toList());
    }

    /**
     * Method to extract the values that exist in the interquantile range.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    private List<Float> getPricesInRange() {
        return values.stream().filter(aFloat -> (aFloat >= q1 - kappa * IQR && aFloat <= q3 + kappa * IQR))
                .collect(Collectors.toList());
    }

    /**
     * Method to retrieve the mean value from the interquantile values in the collection.
     * @return The {@link Float} mean value.
     */
    public Float getNormalizedMean() {
        Float sum = (float) 0;
        List<Float> floats = getPricesInRange();
        for (Float f : floats) {
            sum += f;
        }
        return sum / floats.size();
    }

    /**
     * A simple {@link List<Float>} to <code>double[]</code> converter.
     * @param list The list of floats to convert.
     * @return The <code>double[]</code> with the results.
     */
    private double[] floatListToDoubleArrayConverter(List<Float> list) {
        double[] doubles = new double[list.size()];
        int i = 0;
        for (Float f : list) {
            doubles[i++] = (f != null ? (double) f : 0);
        }
        return doubles;
    }
// todo initialize values and check them before calculating them
    public Float getBargainPercentage() {
        if (!getLowOutliers().isEmpty()) {
            return (getNormalizedMean() - getLowOutliers().get(0))/getNormalizedMean()*100;
        } else {
            return Float.NaN;
        }
    }

    /**
     * Method to set the kappa paramether based on the {@link FilterStrength} parameter.
     * @param strength The {@link FilterStrength} parameter to use.
     */
    public void setFilterStrength(FilterStrength strength) {
        switch (strength) {
            case NORMAL:
                this.kappa = 1.5f;
                break;
            case RELAXED:
                this.kappa = 1.2f;
                break;
            case STRONG:
                this.kappa = 1.8f;
                break;
            case DEFAULT:
                this.kappa = 1.5f;
                break;
        }
    }
}
