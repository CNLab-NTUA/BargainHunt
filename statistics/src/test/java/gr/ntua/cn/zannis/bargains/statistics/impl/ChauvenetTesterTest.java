package gr.ntua.cn.zannis.bargains.statistics.impl;

import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class ChauvenetTesterTest {
    @Test
    public void getMinimumOutlier() throws Exception {

        List<Float> sample1 = new ArrayList<>();
        sample1.add((float) (FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));
        sample1.add((float) (50 + FastMath.random() * 5));

        List<Float> sample2 = new ArrayList<>();
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));
        sample2.add((float) (50 + FastMath.random() * 5));

        for (Flexibility f :
                Flexibility.values()) {
            ChauvenetTester tester = new ChauvenetTester(f);
            Assert.assertEquals(sample1.get(0), tester.getMinimumOutlier(sample1));
            Assert.assertEquals(Float.valueOf(Float.NaN), tester.getMinimumOutlier(sample2));
        }
    }

}