package org.swiften.javautilities.date;

import org.swiften.javautilities.util.HLogs;
import org.swiften.javautilities.number.HNumbers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by haipham on 5/10/17.
 */
@SuppressWarnings("MessageMissingOnTestNGAssertion")
public final class DatesTest {
    @Test
    public void test_dateTrimming_scrap() {
        // Setup
        Date date = Calendar.getInstance().getTime();

        // When & Then
        HLogs.println(HDates.trimDate(date, Calendar.HOUR_OF_DAY));
        HLogs.println(HDates.trimDate(date, Calendar.DAY_OF_MONTH));
        HLogs.println(HDates.trimDate(date, Calendar.MONTH));
        HLogs.println(HDates.trimDate(date, Calendar.YEAR));
    }

    @Test
    @SuppressWarnings("MagicConstant")
    public void test_dateTrimming_shouldWork() {
        // Setup
        Calendar calendar = Calendar.getInstance();
        List<Integer> components = HDates.DATE_COMPONENTS_FIELDS;
        Random rand = new Random();

        for (int i = 0; i < 100000; i++) {
            Date date = HDates.randomDate();
            calendar.setTime(date);
            int component = components.get(rand.nextInt(components.size()));

            // When
            Date trimmed = HDates.trimDate(date, component);

            // Then
            assertNotNull(trimmed);
            calendar.setTime(trimmed);
            assertNotEquals(calendar.get(component), 0);
        }
    }

    @Test
    @SuppressWarnings("MagicConstant")
    public void test_dateComparison_shouldWork() {
        // Setup
        Calendar calendar = Calendar.getInstance();
        List<Integer> components = HDates.DATE_COMPONENTS_FIELDS;
        Random rand = new Random();

        for (int i = 1; i < 100000; i++) {
            // When
            Date date = HDates.randomDate();
            calendar.setTime(date);
            int component = components.get(rand.nextInt(components.size()));
            int random = HNumbers.randomBetween(1, 10);

            calendar.add(component, random);
            Date endDate = calendar.getTime();

            calendar.setTime(date);
            calendar.add(component, -random);
            Date startDate = calendar.getTime();

            // Then
            Assert.assertTrue(HDates.sameAs(date, date, component));
            Assert.assertTrue(HDates.notLaterThan(date, date, component));
            Assert.assertTrue(HDates.notEarlierThan(date, date, component));
            Assert.assertTrue(HDates.notEarlierThan(date, startDate, component));
            Assert.assertTrue(HDates.laterThan(date, startDate, component));
            Assert.assertTrue(HDates.notLaterThan(date, endDate, component));
            Assert.assertTrue(HDates.earlierThan(date, endDate, component));
        }
    }
}
