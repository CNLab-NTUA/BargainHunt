package gr.ntua.cn.zannis.bargains.webapp.algorithm;

import org.apache.commons.math3.stat.StatUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class ChauvenetOutlierFinder {

    private final static FilterStrength DEFAULT_FILTER_STRENGTH = FilterStrength.NORMAL;

    private final List<Float> values;
    private final double stdDev;
    private final double mean;

    public ChauvenetOutlierFinder(List<Float> values) {

        this.values = values;
        Collections.sort(values);
        double[] temp = Utils.floatListToDoubleArrayConverter(values);
        this.mean = StatUtils.mean(temp);
        this.stdDev = Math.sqrt(StatUtils.variance(temp));


    }
}
