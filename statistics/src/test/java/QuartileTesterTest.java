import gr.ntua.cn.zannis.bargains.statistics.Flexibility;
import gr.ntua.cn.zannis.bargains.statistics.impl.QuartileTester;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class QuartileTesterTest {

    @Test
    public void testGetMinimumOutlier() throws Exception {
        QuartileTester tester = new QuartileTester(Flexibility.RELAXED);
        List<Float> floats = new ArrayList<>();
        floats.add(1f);
        floats.add(12f);
        floats.add(11f);
        tester.setValues(floats);
        List<Float> newFloats = new ArrayList<>();
        newFloats.add(3f);
        newFloats.add(40f);
        newFloats.add(410f);
        System.out.println(tester.getMinimumOutlier(newFloats, Flexibility.DEFAULT));
        Assert.assertEquals(tester.getValues(), floats);
        Assert.assertEquals(tester.getFlexibility(), Flexibility.RELAXED);
    }

    @Test
    public void testGetMinimumOutlier1() throws Exception {
        QuartileTester tester = new QuartileTester(Flexibility.RELAXED);
        List<Float> floats = new ArrayList<>();
        floats.add(1f);
        floats.add(2f);
        tester.setValues(floats);
        List<Float> newFloats = new ArrayList<>();
        newFloats.add(3f);
        newFloats.add(4f);
        System.out.println(tester.getMinimumOutlier(newFloats));
        Assert.assertEquals(tester.getValues(), floats);
        Assert.assertEquals(tester.getFlexibility(), Flexibility.RELAXED);
    }
}