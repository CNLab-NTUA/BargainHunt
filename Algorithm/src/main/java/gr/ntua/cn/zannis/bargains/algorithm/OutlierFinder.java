package gr.ntua.cn.zannis.bargains.algorithm;

import org.apache.commons.math3.stat.StatUtils;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class OutlierFinder {
    private double[] observations;
    private Integer filterPercentage = 10;
    private double q1;
    private double q3;

    public OutlierFinder(List<Float> observations, Integer filterPercentage) {
        // convert List<Float> to double[] to use with StatUtils
        this.observations = floatListToDoubleArrayConverter(observations);
        this.filterPercentage = filterPercentage;
        this.q1 = StatUtils.percentile(this.observations, 25);
        this.q3 = StatUtils.percentile(this.observations, 75);
        double min = StatUtils.min(this.observations);
    }

    private double[] findLowOutliers() {
        return new double[]{};
    }

    private double[] getHighOutliers() {
        return new double[]{};
    }

    private double[] getPricesInRange() {
        return new double[]{};
    }

    private double getNormalizedAverage() {
        return StatUtils.mean(getPricesInRange());
    }

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
}
