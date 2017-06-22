package org.swiften.javautilities.runner;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.util.LogUtil;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Created by haipham on 5/27/17.
 */
public class BaseRunnerTest {
    final int INDEX;

    BaseRunnerTest(int index) {
        LogUtil.printft("Runner test %s: %d", this, index);
        INDEX = index;
    }

    @NotNull
    @Override
    public String toString() {
        return String.valueOf(getClass().getSimpleName());
    }

    @BeforeSuite
    public void beforeSuite() {
        LogUtil.printft("Before suite %d", INDEX);
    }

    @AfterSuite
    public void afterSuite() {
        LogUtil.printft("After suite %d", INDEX);
    }
}
