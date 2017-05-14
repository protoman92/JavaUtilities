package org.swiften.javautilities.date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.swiften.javautilities.number.NumberTestUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by haipham on 5/10/17.
 */
public final class DateUtilTest {
    @Test
    @SuppressWarnings("MagicConstant")
    public void test_dateTrimming_shouldWork() {
        // Setup
        Calendar calendar = Calendar.getInstance();
        List<Integer> components = DateUtil.DATE_COMPONENTS_FIELDS;
        Random rand = new Random();

        for (int i = 0; i < 100000; i++) {
            Date date = DateTestUtil.randomDate();
            calendar.setTime(date);
            int component = components.get(rand.nextInt(components.size()));

            // When
            Date trimmed = DateUtil.trimDateComponents(date, component);

            // Then
            Assert.assertNotNull(trimmed);
            calendar.setTime(trimmed);
            Assert.assertNotEquals(calendar.get(component), 0);
        }
    }

    @Test
    @SuppressWarnings("MagicConstant")
    public void test_dateComparison_shouldWork() {
        // Setup
        Calendar calendar = Calendar.getInstance();
        List<Integer> components = DateUtil.DATE_COMPONENTS_FIELDS;
        Random rand = new Random();

        for (int i = 1; i < 100000; i++) {
            // When
            Date date = DateTestUtil.randomDate();
            calendar.setTime(date);
            int component = components.get(rand.nextInt(components.size()));
            int random = NumberTestUtil.randomBetween(1, 10);

            calendar.add(component, random);
            Date endDate = calendar.getTime();

            calendar.setTime(date);
            calendar.add(component, -random);
            Date startDate = calendar.getTime();

            // Then
            Assert.assertTrue(DateUtil.sameAs(date, date, component));
            Assert.assertTrue(DateUtil.notLaterThan(date, date, component));
            Assert.assertTrue(DateUtil.notEarlierThan(date, date, component));
            Assert.assertTrue(DateUtil.notEarlierThan(date, startDate, component));
            Assert.assertTrue(DateUtil.laterThan(date, startDate, component));
            Assert.assertTrue(DateUtil.notLaterThan(date, endDate, component));
            Assert.assertTrue(DateUtil.earlierThan(date, endDate, component));
        }
    }
}