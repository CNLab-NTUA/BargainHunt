package gr.ntua.cn.zannis.bargains.algorithm;

import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    private double[] values;
    private float kappa;
    private double q1;
    private double q3;
    private double IQR;


    /**
     * Main constructor that computes all the needed values to perform the calculations.
     * @param floatValues The {@link List<Float>} with the values to calculate outliers.
     * @param strength The strength of the filtering function, determines the <code>k</code> parameter
     *                 in the calculation formula.
     */
    public OutlierFinder(List<Float> floatValues, FilterStrength strength) {
        // convert List<Float> to double[] to use with StatUtils
        this.values = floatListToDoubleArrayConverter(floatValues);
        setFilterStrength(strength);
        // sort the array to compute the quantiles
        Arrays.sort(this.values);
        // compute the quantiles
        this.q1 = StatUtils.percentile(values, 25);
        this.q3 = StatUtils.percentile(values, 75);
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

    /**
     * Method to extract the outliers that are lower than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    public List<Float> getLowOutliers() {
        // return
        return doubleArrayToFloatListConverter(values).stream()
                .filter(aFloat -> aFloat < q1 - kappa * Math.abs(q3 - q1)).collect(Collectors.toList());
    }

    /**
     * Method to extract the outliers that are higher than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    public List<Float> getHighOutliers() {
        return doubleArrayToFloatListConverter(values).stream()
                .filter(aFloat -> aFloat > q3 + kappa * IQR).collect(Collectors.toList());
    }

    /**
     * Method to extract the values that exist in the interquantile range.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    private List<Float> getPricesInRange() {
        return doubleArrayToFloatListConverter(values).stream()
                .filter(aFloat -> (aFloat >= q1 - kappa * IQR && aFloat <= q3 + kappa * IQR))
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
     * A simple <code>double[]</code> to {@link List<Float>} converter.
     * @param observations The array of doubles to convert.
     * @return The {@link List<Float>} with the results.
     */
    private List<Float> doubleArrayToFloatListConverter(double[] observations) {
        List<Float> floats = new ArrayList<>();
        for (double d : observations) {
            floats.add(new Float(d));
        }
        return floats;
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
