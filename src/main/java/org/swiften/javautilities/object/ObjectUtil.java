package org.swiften.javautilities.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Created by haipham on 5/5/17.
 */
public final class ObjectUtil {
    /**
     * Check if an {@link Object} is not null.
     * @param object A {@link Nullable} {@link Object}.
     * @return A {@link Boolean} value.
     */
    public static boolean nonNull(@Nullable Object object) {
        return object != null;
    }

    /**
     * Check if all {@link Object}s within an {@link Iterable} is not null.
     * @param objects An {@link Iterable} of {@link Object}.
     * @param <T> Generics parameter.
     * @return A {@link Boolean} value.
     * @see #isNull(Object)
     */
    public static <T> boolean nonNull(@NotNull Iterable<T> objects) {
        for (Object object : objects) {
            if (isNull(object)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if all {@link Object}s within a varargs Array is not null.
     * @param objects A varargs of {@link Object}.
     * @return A {@link Boolean} value.
     * @see #nonNull(Iterable)
     */
    public static boolean nonNull(@NotNull Object...objects) {
        return nonNull(Arrays.asList(objects));
    }

    /**
     * Check if an {@link Object} is null.
     * @param object A {@link Nullable} {@link Object}.
     * @return A {@link Boolean} value.
     */
    public static boolean isNull(@Nullable Object object) {
        return object == null;
    }

    /**
     * Check if any {@link Object}s within an {@link Iterable} is null.
     * @param objects An {@link Iterable} of {@link T}.
     * @param <T> Generics parameter.
     * @return A {@link Boolean} value.
     * @see #nonNull(Object)
     */
    public static <T> boolean isNull(@NotNull Iterable<T> objects) {
        for (Object object : objects) {
            if (isNull(object)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if any {@link Object}s within a varargs Array is null.
     * @param objects A varargs of {@link Object}.
     * @return A {@link Boolean} value.
     * @see #isNull(Iterable)
     */
    public static boolean isNull(@NotNull Object...objects) {
        return isNull(Arrays.asList(objects));
    }

    /**
     * Cast an {@link Object} to {@link Object}.
     * @param object An {@link Object}.
     * @return An {@link Object}.
     */
    @Nullable
    public static Object toObject(@Nullable Object object) {
        return object;
    }

    private ObjectUtil() {}
}
