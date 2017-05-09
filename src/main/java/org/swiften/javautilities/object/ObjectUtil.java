package org.swiften.javautilities.object;

import org.jetbrains.annotations.Nullable;

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
     * Check if an {@link Object} is null.
     * @param object A {@link Nullable} {@link Object}.
     * @return A {@link Boolean} value.
     */
    public static boolean isNull(@Nullable Object object) {
        return object == null;
    }

    private ObjectUtil() {}
}
