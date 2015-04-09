package gr.ntua.cn.zannis.bargains.algorithm;

import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class OutlierFinder {

    private final static int DEFAULT_FILTER_STRENGTH = 10;

    private double[] observations;
    private int filterPercentage;
    private double q1;
    private double q3;
    private double median;
    private double IQR;

    public OutlierFinder(List<Float> prices, Integer filterPercentage) {
        // convert List<Float> to double[] to use with StatUtils
        this.observations = floatListToDoubleArrayConverter(prices);
        this.filterPercentage = filterPercentage;
        // sort the array todo check if its needed to be sorted
        Arrays.sort(observations);
        // compute the quantiles
        this.q1 = StatUtils.percentile(this.observations, 25);
        this.median = StatUtils.percentile(this.observations, 50);
        this.q3 = StatUtils.percentile(this.observations, 75);
        // compute the interquantile range
        IQR = Math.abs(q3 - q1);
    }

    /**
     * Method to extract the outliers that are lower than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    public List<Float> getLowOutliers() {
        // return
        return doubleArrayToFloatListConverter(observations).stream()
                .filter(aFloat -> aFloat < q1 - 1.5 * Math.abs(q3 - q1)).collect(Collectors.toList());
    }

    /**
     * Method to extract the outliers that are higher than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    public List<Float> getHighOutliers() {
        return doubleArrayToFloatListConverter(observations).stream()
                .filter(aFloat -> aFloat > q3 + 1.5 * IQR).collect(Collectors.toList());
    }

    /**
     * Method to extract the values that exist in the interquantile range.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    private List<Float> getPricesInRange() {
        return doubleArrayToFloatListConverter(observations).stream()
                .filter(aFloat -> (aFloat >= q1 - 1.5 * IQR && aFloat <= q3 + 1.5 * IQR)).collect(Collectors.toList());
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

    public double[] getObservations() {
        return observations;
    }

    public void setObservations(double[] observations) {
        this.observations = observations;
    }

    public Integer getFilterPercentage() {
        return filterPercentage;
    }

    public void setFilterPercentage(Integer filterPercentage) {
        this.filterPercentage = filterPercentage;
    }

    public double getQ1() {
        return q1;
    }

    public void setQ1(double q1) {
        this.q1 = q1;
    }

    public double getQ3() {
        return q3;
    }

    public void setQ3(double q3) {
        this.q3 = q3;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getIQR() {
        return IQR;
    }

    public void setIQR(double IQR) {
        this.IQR = IQR;
    }
// todo initialize values and check them before calculating them
    public Float getBargainPercentage() {
        if (!getLowOutliers().isEmpty()) {
            return (getNormalizedMean() - getLowOutliers().get(0))/getNormalizedMean()*100;
        } else {
            return Float.NaN;
        }
    }
}
