package org.swiften.javautilities.date;

import org.swiften.javautilities.util.HPLog;
import org.swiften.javautilities.number.HPNumbers;
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
public final class HPDatesTest {
    @Test
    public void test_dateTrimming_scrap() {
        // Setup
        Date date = Calendar.getInstance().getTime();

        // When & Then
        HPLog.println(HPDates.trimDate(date, Calendar.HOUR_OF_DAY));
        HPLog.println(HPDates.trimDate(date, Calendar.DAY_OF_MONTH));
        HPLog.println(HPDates.trimDate(date, Calendar.MONTH));
        HPLog.println(HPDates.trimDate(date, Calendar.YEAR));
    }

    @Test
    @SuppressWarnings("MagicConstant")
    public void test_dateTrimming_shouldWork() {
        // Setup
        Calendar calendar = Calendar.getInstance();
        List<Integer> components = HPDates.DATE_COMPONENTS_FIELDS;
        Random rand = new Random();

        for (int i = 0; i < 100000; i++) {
            Date date = HPDates.randomDate();
            calendar.setTime(date);
            int component = components.get(rand.nextInt(components.size()));

            // When
            Date trimmed = HPDates.trimDate(date, component);

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
        List<Integer> components = HPDates.DATE_COMPONENTS_FIELDS;
        Random rand = new Random();

        for (int i = 1; i < 100000; i++) {
            // When
            Date date = HPDates.randomDate();
            calendar.setTime(date);
            int component = components.get(rand.nextInt(components.size()));
            int random = HPNumbers.randomBetween(1, 10);

            calendar.add(component, random);
            Date endDate = calendar.getTime();

            calendar.setTime(date);
            calendar.add(component, -random);
            Date startDate = calendar.getTime();

            // Then
            Assert.assertTrue(HPDates.sameAs(date, date, component));
            Assert.assertTrue(HPDates.notLaterThan(date, date, component));
            Assert.assertTrue(HPDates.notEarlierThan(date, date, component));
            Assert.assertTrue(HPDates.notEarlierThan(date, startDate, component));
            Assert.assertTrue(HPDates.laterThan(date, startDate, component));
            Assert.assertTrue(HPDates.notLaterThan(date, endDate, component));
            Assert.assertTrue(HPDates.earlierThan(date, endDate, component));
        }
    }
}
