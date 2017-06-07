package org.swiften.javautilities.runner;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.log.LogUtil;
import org.testng.annotations.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by haipham on 5/27/17.
 */
public final class RunnerTest extends BaseRunnerTest {
    @NotNull
    @DataProvider(parallel = false)
    public static Iterator<Object[]> factory() {
        List<Object[]> data = new LinkedList<Object[]>();

        for (int i = 0; i < 2; i++) {
            data.add(new Object[] { i });
        }

        return data.iterator();
    }

    @Factory(dataProviderClass = RunnerTest.class, dataProvider = "factory")
    public RunnerTest(int index) {
        super(index);
    }

    @DataProvider(parallel = false)
    public Iterator<Object[]> dataProvider() {
        List<Object[]> data = new LinkedList<Object[]>();

        for (int i = 0; i < 3; i++) {
            data.add(new Object[] { (char)(97 + i) });
        }

        return data.iterator();
    }

    @Test
    public void test_factory1() {
        LogUtil.printft("Testing factory 1 - %d", INDEX);
    }

    @Test(dataProvider = "dataProvider")
    public void test_dataProvider(char a) {
        LogUtil.printft("Testing data provider 1 - %s %d", a, INDEX);
    }
}
