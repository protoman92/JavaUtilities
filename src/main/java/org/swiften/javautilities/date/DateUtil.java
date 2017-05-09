package org.swiften.javautilities.date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by haipham on 5/10/17.
 */

public final class DateUtil {
    @NotNull
    public static final List<Integer> DATE_COMPONENTS_FIELDS = Arrays.asList(
        Calendar.MILLISECOND,
        Calendar.SECOND,
        Calendar.MINUTE,
        Calendar.HOUR_OF_DAY,
        Calendar.DAY_OF_MONTH,
        Calendar.MONTH,
        Calendar.YEAR
    );

    /**
     * Get a {@link Calendar} based on supplied properties.
     * @param year An {@link Integer} value.
     * @param month An {@link Integer} value.
     * @param day An {@link Integer} value.
     * @param hour An {@link Integer} value.
     * @param minute An {@link Integer} value.
     * @param second An {@link Integer} value.
     * @param millisecond An {@link Integer} value.
     * @return A {@link Calendar} instance.
     */
    @NotNull
    public static Calendar getCalendar(int year,
                                       int month,
                                       int day,
                                       int hour,
                                       int minute,
                                       int second,
                                       int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar;
    }

    /**
     * Get a {@link Date} based on supplied properties.
     * @param year An {@link Integer} value.
     * @param month An {@link Integer} value.
     * @param day An {@link Integer} value.
     * @param hour An {@link Integer} value.
     * @param minute An {@link Integer} value.
     * @param second An {@link Integer} value.
     * @param millisecond An {@link Integer} value.
     * @return A {@link Date} instance.
     */
    @NotNull
    public static Date getDate(int year,
                               int month,
                               int day,
                               int hour,
                               int minute,
                               int second,
                               int millisecond) {
        return getCalendar(
            year,
            month,
            day,
            hour,
            minute,
            second,
            millisecond
        ).getTime();
    }

    /**
     * Trim a {@link Date} by setting all properties after a certain
     * granularity level to 1.
     * @param dateToTrim A {@link Date} instance to be trimmed.
     * @param granularity The level of trimming to be applied.
     * @return A {@link Date} instance. This can be null if the granularity
     * level is not found within {@link #DATE_COMPONENTS_FIELDS}.
     */
    @Nullable
    @SuppressWarnings("MagicConstant")
    public static Date trimDateComponents(@NotNull Date dateToTrim,
                                          int granularity) {
        Calendar calendar = Calendar.getInstance();
        List<Integer> dateComponentFields = DATE_COMPONENTS_FIELDS;
        int index = dateComponentFields.indexOf(granularity);

        if (index > -1 && index < dateComponentFields.size()) {
            calendar.setTime(dateToTrim);

            for (int i = 0; i < index; i++) {
                Integer field;

                if ((field = dateComponentFields.get(i)) != null) {
                    calendar.set(field, 1);
                }
            }

            return calendar.getTime();
        } else {
            return null;
        }
    }

    /**
     * Check if a {@link Date} is earlier than, or the same as, another
     * {@link Date}, based on a granularity level.
     * @param firstDate A {@link Date} instance.
     * @param secondDate A {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. An {@link Integer} value.
     * @return A {@link Boolean} value.
     * @see #trimDateComponents(Date, int)
     */
    public static boolean notLaterThan(@NotNull Date firstDate,
                                       @NotNull Date secondDate,
                                       int granularity) {
        Date first = trimDateComponents(firstDate, granularity);
        Date second = trimDateComponents(secondDate, granularity);
        return first != null && second != null && !first.after(second);
    }

    /**
     * Check if a {@link Date} is earlier than another {@link Date}, based on
     * a granularity level.
     * @param firstDate A {@link Date} instance.
     * @param secondDate A {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. An {@link Integer} value.
     * @return A {@link Boolean} value.
     * @see #trimDateComponents(Date, int)
     */
    public static boolean earlierThan(@NotNull Date firstDate,
                                      @NotNull Date secondDate,
                                      int granularity) {
        Date first = trimDateComponents(firstDate, granularity);
        Date second = trimDateComponents(secondDate, granularity);
        return first != null && second != null && first.before(second);
    }

    /**
     * Check if a {@link Date} is later than, or the same as, another
     * {@link Date}, based on a granularity level.
     * @param firstDate A {@link Date} instance.
     * @param secondDate A {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. An {@link Integer} value.
     * @return A {@link Boolean} value.
     * @see #trimDateComponents(Date, int)
     */
    public static boolean notEarlierThan(@NotNull Date firstDate,
                                         @NotNull Date secondDate,
                                         int granularity) {
        Date first = trimDateComponents(firstDate, granularity);
        Date second = trimDateComponents(secondDate, granularity);
        return first != null && second != null && !first.before(second);
    }

    /**
     * Check if a {@link Date} is later than another {@link Date}, based on
     * a granularity level.
     * @param firstDate A {@link Date} instance.
     * @param secondDate A {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. An {@link Integer} value.
     * @return A {@link Boolean} value.
     * @see #trimDateComponents(Date, int)
     */
    public static boolean laterThan(@NotNull Date firstDate,
                                    @NotNull Date secondDate,
                                    int granularity) {
        Date first = trimDateComponents(firstDate, granularity);
        Date second = trimDateComponents(secondDate, granularity);
        return first != null && second != null && first.after(second);
    }

    /**
     * Check if a {@link Date} is the same as another {@link Date}, based on
     * a granularity level.
     * @param firstDate A {@link Date} instance.
     * @param secondDate A {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. An {@link Integer} value.
     * @return A {@link Boolean} value.
     * @see #trimDateComponents(Date, int)
     */
    public static boolean sameAs(@NotNull Date firstDate,
                                 @NotNull Date secondDate,
                                 int granularity) {
        Date first = trimDateComponents(firstDate, granularity);
        Date second = trimDateComponents(secondDate, granularity);
        return first != null && second != null && first.equals(second);
    }

    /**
     * Check if a {@link Date} is earlier than, or the same as, another
     * {@link Date}.
     * @param firstDate A {@link Date} instance.
     * @param secondDate A {@link Date} instance.
     * @return A {@link Boolean} value.
     */
    public static boolean notLaterThan(@NotNull Date firstDate,
                                       @NotNull Date secondDate) {
        return !firstDate.after(secondDate);
    }

    /**
     * Check if a {@link Date} is later than, or the same as, another
     * {@link Date}.
     * @param firstDate A {@link Date} instance.
     * @param secondDate A {@link Date} instance.
     * @return A {@link Boolean} value.
     */
    public static boolean notEarlierThan(@NotNull Date firstDate,
                                         @NotNull Date secondDate) {
        return !firstDate.before(secondDate);
    }

    private DateUtil() {}
}
