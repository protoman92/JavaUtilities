package org.swiften.javautilities.object;

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Created by haipham on 5/5/17.
 */
public final class HPObjects {
    /**
     * Check if {@link Object} is not null.
     * @param object {@link Nullable} {@link Object}.
     * @return {@link Boolean} value.
     */
    public static boolean nonNull(@Nullable Object object) {
        return object != null;
    }

    /**
     * Check if all {@link Object}s within {@link Iterable} is not null.
     * @param objects {@link Iterable} of {@link Object}.
     * @param <T> Generics parameter.
     * @return {@link Boolean} value.
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
     * @return {@link Boolean} value.
     * @see #nonNull(Iterable)
     */
    public static boolean nonNull(@NotNull Object...objects) {
        return nonNull(Arrays.asList(objects));
    }

    /**
     * Check if {@link Object} is null.
     * @param object {@link Nullable} {@link Object}.
     * @return {@link Boolean} value.
     */
    public static boolean isNull(@Nullable Object object) {
        return object == null;
    }

    /**
     * Check if any {@link Object}s within {@link Iterable} is null.
     * @param objects {@link Iterable} of {@link T}.
     * @param <T> Generics parameter.
     * @return {@link Boolean} value.
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
     * @return {@link Boolean} value.
     * @see #isNull(Iterable)
     */
    public static boolean isNull(@NotNull Object...objects) {
        return isNull(Arrays.asList(objects));
    }

    /**
     * Cast {@link Object} to {@link Object}.
     * @param object {@link Object}.
     * @return {@link Object}.
     */
    @Nullable
    public static Object toObject(@Nullable Object object) {
        return object;
    }

    /**
     * Check if an {@link Object} is not null, and throw {@link RuntimeException}
     * otherwise.
     * @param object {@link Object} instance.
     * @param message {@link String} value.
     */
    public static void requireNotNull(@Nullable Object object,
                                      @NotNull String message) {
        if (object == null) {
            throw new RuntimeException(message);
        }
    }

    /**
     * Same as above, but uses a default message.
     * @param object {@link Object} instance.
     * @see #requireNotNull(Object, String)
     */
    public static void requireNotNull(@Nullable Object object) {
        requireNotNull(object, "");
    }

    /**
     * Return the same {@link T} instance.
     * @param object {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link T} instance.
     */
    @Nullable
    public static <T> T eq(@Nullable T object) {
        return object;
    }

    /**
     * {@link Function} to return the same value passed in.
     * @param <T> Generics parameter.
     * @return {@link Function} instance.
     */
    @NotNull
    public static <T> Function<? super T, ? extends T> eqFn() {
        return new Function<T, T>() {
            @NotNull
            @Override
            public T apply(@NotNull T t) throws Exception {
                return t;
            }
        };
    }

    private HPObjects() {}
}
