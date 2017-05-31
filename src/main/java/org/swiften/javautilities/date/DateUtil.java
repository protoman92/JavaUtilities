package org.swiften.javautilities.date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by haipham on 5/10/17.
 */

public final class DateUtil {
    @NotNull
    static final List<Integer> DATE_COMPONENTS_FIELDS = Arrays.asList(
        Calendar.MILLISECOND,
        Calendar.SECOND,
        Calendar.MINUTE,
        Calendar.HOUR_OF_DAY,
        Calendar.DAY_OF_MONTH,
        Calendar.MONTH,
        Calendar.YEAR
    );

    /**
     * Get {@link Calendar} based on supplied properties.
     * @param year {@link Integer} value.
     * @param month {@link Integer} value.
     * @param day {@link Integer} value.
     * @param hour {@link Integer} value.
     * @param minute {@link Integer} value.
     * @param second {@link Integer} value.
     * @param millisecond {@link Integer} value.
     * @return {@link Calendar} instance.
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
     * Get {@link Date} based on supplied properties.
     * @param year {@link Integer} value.
     * @param month {@link Integer} value.
     * @param day {@link Integer} value.
     * @param hour {@link Integer} value.
     * @param minute {@link Integer} value.
     * @param second {@link Integer} value.
     * @param millisecond {@link Integer} value.
     * @return {@link Date} instance.
     */
    @NotNull
    public static Date getDate(int year, int month, int day,
                               int hour, int minute, int second,
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
     * Trim {@link Date} by setting all properties after a certain granularity
     * level to 1.
     * @param dateToTrim {@link Date} instance to be trimmed.
     * @param granularity The level of trimming to be applied.
     * @return {@link Date} instance. This can be null if the granularity
     * level is not found within {@link #DATE_COMPONENTS_FIELDS}.
     */
    @Nullable
    @SuppressWarnings("MagicConstant")
    public static Date trimDate(@NotNull Date dateToTrim, int granularity) {
        Calendar calendar = Calendar.getInstance();
        List<Integer> componentFields = DATE_COMPONENTS_FIELDS;
        int index = componentFields.indexOf(granularity);

        if (index > -1 && index < componentFields.size()) {
            calendar.setTime(dateToTrim);

            for (int i = 0; i < index; i++) {
                Integer field;

                if (ObjectUtil.nonNull(field = componentFields.get(i))) {
                    calendar.set(field, 1);
                }
            }

            return calendar.getTime();
        } else {
            return null;
        }
    }

    /**
     * Check if {@link Date} is earlier than, or the same as, another
     * {@link Date}, based on a granularity level.
     * @param firstDate {@link Date} instance.
     * @param secondDate {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. {@link Integer} value.
     * @return {@link Boolean} value.
     * @see #trimDate(Date, int)
     */
    public static boolean notLaterThan(@NotNull Date firstDate,
                                       @NotNull Date secondDate,
                                       int granularity) {
        Date first = trimDate(firstDate, granularity);
        Date second = trimDate(secondDate, granularity);
        return first != null && second != null && !first.after(second);
    }

    /**
     * Check if {@link Date} is earlier than another {@link Date}, based on
     * a granularity level.
     * @param firstDate {@link Date} instance.
     * @param secondDate {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. {@link Integer} value.
     * @return {@link Boolean} value.
     * @see #trimDate(Date, int)
     */
    public static boolean earlierThan(@NotNull Date firstDate,
                                      @NotNull Date secondDate,
                                      int granularity) {
        Date first = trimDate(firstDate, granularity);
        Date second = trimDate(secondDate, granularity);
        return first != null && second != null && first.before(second);
    }

    /**
     * Check if {@link Date} is later than, or the same as, another
     * {@link Date}, based on a granularity level.
     * @param firstDate {@link Date} instance.
     * @param secondDate {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. {@link Integer} value.
     * @return {@link Boolean} value.
     * @see #trimDate(Date, int)
     */
    public static boolean notEarlierThan(@NotNull Date firstDate,
                                         @NotNull Date secondDate,
                                         int granularity) {
        Date first = trimDate(firstDate, granularity);
        Date second = trimDate(secondDate, granularity);
        return first != null && second != null && !first.before(second);
    }

    /**
     * Check if {@link Date} is later than another {@link Date}, based on
     * a granularity level.
     * @param firstDate {@link Date} instance.
     * @param secondDate {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. {@link Integer} value.
     * @return {@link Boolean} value.
     * @see #trimDate(Date, int)
     */
    public static boolean laterThan(@NotNull Date firstDate,
                                    @NotNull Date secondDate,
                                    int granularity) {
        Date first = trimDate(firstDate, granularity);
        Date second = trimDate(secondDate, granularity);
        return first != null && second != null && first.after(second);
    }

    /**
     * Check if {@link Date} is the same as another {@link Date}, based on
     * a granularity level.
     * @param firstDate {@link Date} instance.
     * @param secondDate {@link Date} instance.
     * @param granularity The level of granularity to be applied when
     *                    comparing. {@link Integer} value.
     * @return {@link Boolean} value.
     * @see #trimDate(Date, int)
     */
    public static boolean sameAs(@NotNull Date firstDate,
                                 @NotNull Date secondDate,
                                 int granularity) {
        Date first = trimDate(firstDate, granularity);
        Date second = trimDate(secondDate, granularity);
        return first != null && second != null && first.equals(second);
    }

    /**
     * Check if {@link Date} is earlier than, or the same as, another
     * {@link Date}.
     * @param firstDate {@link Date} instance.
     * @param secondDate {@link Date} instance.
     * @return {@link Boolean} value.
     */
    public static boolean notLaterThan(@NotNull Date firstDate,
                                       @NotNull Date secondDate) {
        return !firstDate.after(secondDate);
    }

    /**
     * Check if {@link Date} is later than, or the same as, another
     * {@link Date}.
     * @param firstDate {@link Date} instance.
     * @param secondDate {@link Date} instance.
     * @return {@link Boolean} value.
     */
    public static boolean notEarlierThan(@NotNull Date firstDate,
                                         @NotNull Date secondDate) {
        return !firstDate.before(secondDate);
    }

    private DateUtil() {}
}
