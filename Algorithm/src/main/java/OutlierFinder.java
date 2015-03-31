import org.apache.commons.math3.stat.StatUtils;

import java.util.LinkedList;
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

    public static void main(String... args) {
        List<Float> floats = new LinkedList<>();
        floats.add(10.10f);
        floats.add(10.20f);
        floats.add(10.50f);
        floats.add(11.50f);
        floats.add(15.50f);
        floats.add(30.50f);
        floats.add(16.50f);
        floats.add(17.50f);
        floats.add(12.50f);
        floats.add(14.50f);
        floats.add(25.50f);
        floats.add(20.50f);
        floats.add(19.50f);
        new OutlierFinder(floats, 10);
    }
//
//    private Integer findMinOutlier() {
////        double q1 = StatUtils.percentile()
//    }

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
