package gr.ntua.cn.zannis.bargains.statistics.impl;

import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.Tester;

import java.util.Collections;
import java.util.List;

/**
 * The class that finds the outliers in a given float list using the interquantile range method.
 * The <code>k</code> in the formula is based on the {@link Flexibility} parameter.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Outlier">Wikipedia: Outlier</a>
 * @author zannis <zannis.kal@gmail.com>
 */
public abstract class BaseTester implements Tester {

    protected final static Flexibility DEFAULT_FILTER_STRENGTH = Flexibility.NORMAL;
    protected List<Float> values;
    protected double[] doubleValues;
    protected Flexibility flexibility;

    /**
     * Full constructor for <code>BaseTester</code> specifying the flexibility.
     * @param flexibility The amount of flexibility to use.
     */
    public BaseTester(Flexibility flexibility) {
        setFlexibility(flexibility);
    }

    /**
     * Constructor for the <code>BaseTester</code> using the default filter flexibility.
     */
    public BaseTester() {
        this(DEFAULT_FILTER_STRENGTH);
    }

    /**
     * Get the minimum value of the sample.
     * @return The minimum value.
     */
    protected Float getMin() {
        if (!this.values.isEmpty()) {
            return this.values.get(0);
        } else {
            return null;
        }
    }

    /**
     * A simple {@link List<Float>} to <code>double[]</code> converter.
     * @param list The list of floats to convert.
     * @return The <code>double[]</code> with the results.
     */
    private void setDoubleValues(List<Float> list) {
        this.doubleValues = new double[list.size()];
        int i = 0;
        for (Float f : list) {
            doubleValues[i++] = (f != null ? (double) f : 0);
        }
    }

    public List<Float> getValues() {
        return values;
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
}
