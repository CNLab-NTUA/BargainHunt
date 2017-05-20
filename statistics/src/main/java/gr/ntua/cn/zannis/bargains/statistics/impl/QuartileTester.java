package gr.ntua.cn.zannis.bargains.statistics.impl;

import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.TestType;
import org.apache.commons.math3.stat.StatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The class that finds the outliers in a given float list using the interquantile range method.
 * The <code>k</code> in the formula is based on the {@link Flexibility} parameter.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Outlier">Wikipedia: Outlier</a>
 * @author zannis zannis.kal@gmail.com
 */
public class QuartileTester extends BaseTester {

    private static final Logger log = LoggerFactory.getLogger(QuartileTester.class);
    
    private float kappa;
    private double q1;
    private double q3;
    private double IQR;


    /**
     * Constructor for a Tester using the interquartile method with a custom flexibility
     * parameter.
     * @param flexibility The flexibility value to use.
     */
    public QuartileTester(Flexibility flexibility) {
        super(flexibility);
        log.info("Created new QuartileTester with " + flexibility.name() + " flexibility.");
    }

    /**
     * Constructor for a Tester using the interquartile method using the default flexibility
     * parameter of 1.5.
     */
    public QuartileTester() {
        this(DEFAULT_FLEXIBILITY);
    }

    /**
     * Method to init required values, gets called by <code>setValues</code>}
     */
    private void init() {
        setKappa(flexibility);
        this.q1 = StatUtils.percentile(doubleValues, 25);
        this.q3 = StatUtils.percentile(doubleValues, 75);
        this.IQR = Math.abs(q3 - q1);
    }

    /**
     * Method to extract the outliers that are lower than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    private List<Float> getLowOutliers() {
        return values.stream().filter(aFloat -> aFloat < q1 - kappa * Math.abs(q3 - q1)).collect(Collectors.toList());
    }

    /**
     * Method to extract the outliers that are higher than the median value.
     * @return The {@link List<Float>} containing the results or empty list.
     */
    private List<Float> getHighOutliers() {
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
     * Method to get the percentage that the minimum outlier differs from the normalized
     * mean value of the sample.
     * @return The bargain percentage. A number between 0 - 100 or NaN if there are no low outliers.
     */
    @Override
    public Float getBargainPercentage() {
        if (!getLowOutliers().isEmpty()) {
            return (getNormalizedMean() - getLowOutliers().get(0))/getNormalizedMean()*100;
        } else {
            return Float.NaN;
        }
    }

    /**
     * Method to set the kappa paramether based on the {@link Flexibility} parameter.
     * @param strength The {@link Flexibility} parameter to use.
     */
    private void setKappa(Flexibility strength) {
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
        }
    }

    /**
     * Method to retrieve the mean value from the interquantile values in the collection.
     * @return The {@link Float} mean value.
     */
    private Float getNormalizedMean() {
        Float sum = (float) 0;
        List<Float> floats = getPricesInRange();
        for (Float f : floats) {
            sum += f;
        }
        return sum / floats.size();
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample, Flexibility strength) {
        setFlexibility(strength);
        return getMinimumOutlier(sample);
    }

    @Override
    public Float getMinimumOutlier(List<Float> sample) {
        setValues(sample);
        if (StatUtils.variance(this.doubleValues) == 0) return Float.NaN;
        List<Float> outliers = getLowOutliers();
        return !outliers.isEmpty() ? outliers.get(0) : Float.NaN;

    }

    @Override
    public TestType getType() {
        return TestType.QUARTILE;
    }

    @Override
    public void setValues(List<Float> values) {
        super.setValues(values);
        init();
    }
}
