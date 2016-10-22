package gr.ntua.cn.zannis.bargains.statistics;

import java.util.List;

/**
 * The interface that the implemented Outlier Testers should
 * provide.
 * @author zannis <zannis.kal@gmail.com>
 */
public interface Tester {

    /**
    * Get the minimum outlier from a list of values, given the specified flexibility.
    * Does not change the state of the <code>gr.ntua.cn.zannis.bargains.statistics.Tester</code>.
    * @param sample The {@link List<Float>} with the values to calculate outliers.
    * @param strength The flexibility of the filtering function, determines the <code>k</code> parameter
    *                in the calculation formula.
    */
    Float getMinimumOutlier(List<Float> sample, Flexibility strength);

    /**
     * Get the minimum outlier from a list of values, using the default flexibility.
     * Does not change the state of the <code>gr.ntua.cn.zannis.bargains.statistics.Tester</code>.
     * @param sample The {@link List<Float>} with the values to calculate outliers.
     */
    Float getMinimumOutlier(List<Float> sample);

    /**
     * Method to set the current flexibility of the test.
     * @param strength The flexibility of the filtering function, determines the <code>k</code> parameter
     *                in the calculation formula.
     */
    void setFlexibility(Flexibility strength);

    /**
     * Method to get the current flexibility of the test.
     */
    Flexibility getFlexibility();

}
