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

    final static Flexibility DEFAULT_FLEXIBILITY = Flexibility.NORMAL;
    protected List<Float> values;
    double[] doubleValues;
    protected Flexibility flexibility;
    private double significanceValue;
    double[] criticalValues;


    /**
     * Full constructor for <code>BaseTester</code> specifying the flexibility.
     * @param flexibility The value of flexibility to use.
     */
    BaseTester(Flexibility flexibility) {
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
    Float getMinimumValue() {
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

    public abstract Float getBargainPercentage();

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
        }
    }

}
