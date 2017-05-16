package org.swiften.javautilities.bool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by haipham on 4/7/17.
 */
public final class BooleanUtil {
    /**
     * Check if a {@link Boolean} value is true.
     * @param value The {@link Boolean} value to be checked.
     * @return A {@link Boolean} value.
     */
    public static boolean isTrue(boolean value) {
        return value;
    }

    /**
     * Check if an {@link Object} is true.
     * @param object An {@link Object} instance.
     * @return A {@link Boolean} value.
     */
    public static boolean isTrue(@NotNull Object object) {
        return object instanceof Boolean && Boolean.class.cast(object);
    }

    /**
     * Check if a {@link Boolean} value is false.
     * @param value The {@link Boolean} to be checked.
     * @return A {@link Boolean} value.
     */
    public static boolean isFalse(boolean value) {
        return !value;
    }

    /**
     * Check if an {@link Object} is false.
     * @param object An {@link Object} instance.
     * @return A {@link Boolean} value.
     */
    public static boolean isFalse(@NotNull Object object) {
        return object instanceof Boolean && !Boolean.class.cast(object);
    }

    /**
     * Return {@link Boolean#TRUE}, no matter what parameter is used.
     * @param object An {@link Object} instance.
     * @return {@link Boolean#TRUE}
     */
    public static boolean toTrue(@Nullable Object object) {
        return true;
    }

    /**
     * Return {@link Boolean#FALSE}, no matter what parameter is used.
     * @param object An {@link Object} instance
     * @return {@link Boolean#FALSE}
     */
    public static boolean toFalse(@Nullable Object object) {
        return false;
    }

    private BooleanUtil() {}
}
