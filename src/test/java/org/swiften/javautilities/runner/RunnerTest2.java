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
public final class RunnerTest2 extends BaseRunnerTest {
    @Factory(dataProviderClass = RunnerTest.class, dataProvider = "factory")
    public RunnerTest2(int index) {
        super(index);
    }

    @BeforeSuite
    public void beforeSuite() {
        LogUtil.printfThread("Before suite 2 %d", INDEX);
    }

    @AfterSuite
    public void afterSuite() {
        LogUtil.printfThread("After suite 2 %d", INDEX);
    }

    @DataProvider(parallel = false)
    public Iterator<Object[]> dataProvider() {
        List<Object[]> data = new LinkedList<Object[]>();

        for (int i = 0; i < 2; i++) {
            data.add(new Object[] { (char)(97 + i) });
        }

        return data.iterator();
    }

    @Test
    public void test_factory1() {
        LogUtil.printfThread("Testing factory 2 - %d", INDEX);
    }

    @Test(dataProvider = "dataProvider")
    public void test_dataProvider(char a) {
        LogUtil.printfThread("Testing data provider 2 - %s %d", a, INDEX);
    }
}
