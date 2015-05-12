package gr.ntua.cn.zannis.bargains.webapp.algorithm;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class Utils {
    /**
     * A simple {@link List <Float>} to <code>double[]</code> converter.
     *
     * @param list The list of floats to convert.
     * @return The <code>double[]</code> with the results.
     */
    public static double[] floatListToDoubleArrayConverter(List<Float> list) {
        double[] doubles = new double[list.size()];
        int i = 0;
        for (Float f : list) {
            doubles[i++] = (f != null ? (double) f : 0);
        }
        return doubles;
    }
}
