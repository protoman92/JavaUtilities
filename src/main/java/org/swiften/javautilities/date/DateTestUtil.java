package org.swiften.javautilities.date;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.number.NumberTestUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by haipham on 5/10/17.
 */
public final class DateTestUtil {
    /**
     * Produce a random {@link Calendar}.
     * @return A {@link Calendar} instance.
     * @see DateUtil#getCalendar(int, int, int, int, int, int, int)
     */
    @NotNull
    public static Calendar randomCalendar() {
        return DateUtil.getCalendar(
            NumberTestUtil.randomBetween(2000, 2016),
            NumberTestUtil.randomBetween(1, 11),
            NumberTestUtil.randomBetween(1, 31),
            NumberTestUtil.randomBetween(1, 23),
            NumberTestUtil.randomBetween(1, 59),
            NumberTestUtil.randomBetween(1, 59),
            NumberTestUtil.randomBetween(1, 999)
        );
    }

    /**
     * Produce a random {@link Date}.
     * @return A {@link Date} instance.
     * @see #randomCalendar()
     */
    @NotNull
    public static Date randomDate() {
        return randomCalendar().getTime();
    }

    private DateTestUtil() {}
}
