package gr.ntua.cn.zannis.bargains.statistics.impl;

import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class ChauvenetTesterTest {
    @Test
    public void getMinimumOutlier() throws Exception {

        final Logger log = LoggerFactory.getLogger(ChauvenetTester.class);


        // test 3 different sample sizes
        for (int i = 1; i < 4; i++) {
            List<Float> sample1 = new ArrayList<>();
            List<Float> sample2 = new ArrayList<>();

            sample1.add((float) (FastMath.random() * 5));
            for (int j = 0; j < 10 * i - 1; j++) sample1.add((float) (50 + FastMath.random() * 5));
            for (int j = 0; j < 10 * i; j++) sample2.add((float) (50 + FastMath.random() * 5));

            log.info(sample1.toString());
            log.info(sample2.toString());

            for (Flexibility f :
                    Flexibility.values()) {
                ChauvenetTester tester = new ChauvenetTester(f);
                Assert.assertEquals(sample1.get(0), tester.getMinimumOutlier(sample1));
                Assert.assertEquals(Float.valueOf(Float.NaN), tester.getMinimumOutlier(sample2));
            }
        }

    }

}