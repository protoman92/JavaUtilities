package org.swiften.javautilities.test;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.collection.HPIterables;

import java.util.*;

/**
 * Created by haipham on 6/18/17.
 */

/**
 * Utility methods to use with TestNG.
 */
public final class HPTestNGs {
    /**
     * Produce {@link List} of {@link Object} array from {@link List} of
     * {@link Collection}, taking an element from each {@link Collection} for
     * every inner {@link Object} array.
     * @param objects {@link List} of {@link Collection}.
     * @return {@link List} of {@link Object} array.
     */
    @NotNull
    public static List<Object[]> oneFromEach(@NotNull List<Collection<?>> objects) {
        List<Object[]> data = new LinkedList<Object[]>();
        int size = objects.size();

        if (size > 0) {
            Collection<?> first = objects.get(0);
            List<Collection<?>> second = HPIterables.subList(objects, 1, size);
            List<Object[]> secondResults = oneFromEach(second);

            if (secondResults.size() > 0) {
                for (Object[] items : secondResults) {
                    int length = items.length;

                    for (Object firstItem : first) {
                        Object[] newItems = new Object[length + 1];
                        newItems[0] = firstItem;
                        System.arraycopy(items, 0, newItems, 1, length);
                        data.add(newItems);
                    }
                }
            } else {
                for (Object item : first) {
                    data.add(new Object[] { item });
                }
            }
        }

        return data;
    }

    /**
     * Same as above, but uses varargs of {@link Collection}.
     * @param objects {@link Collection} varargs.
     * @return {@link List} of {@link Object} array.
     * @see #oneFromEach(List)
     */
    @NotNull
    public static List<Object[]> oneFromEach(@NotNull Collection<?>...objects) {
        return oneFromEach(HPIterables.asList(objects));
    }

    /**
     * Same as above, but uses {@link Collection} of {@link T} array.
     * @param objects {@link Collection} of {@link T} array.
     * @param <T> Generics parameter.
     * @return {@link List} of {@link Object} array.
     * @see #oneFromEach(List)
     */
    @NotNull
    public static <T> List<Object[]> oneFromEach(@NotNull Collection<T[]> objects) {
        List<Collection<?>> copy = new LinkedList<Collection<?>>();

        for (Object[] object : objects) {
            copy.add(HPIterables.asList(object));
        }

        return oneFromEach(copy);
    }

    /**
     * Same as above, but uses varargs of {@link Object} array.
     * @param objects {@link Object} array varargs.
     * @param <T> Generics parameter.
     * @return {@link List} of {@link Object} array.
     * @see #oneFromEach(Collection)
     */
    @NotNull
    public static <T> List<Object[]> oneFromEach(@NotNull T[]...objects) {
        return oneFromEach(HPIterables.asList(objects));
    }

    /**
     * Same as above, but uses {@link Object} varargs. Each {@link Object} is
     * then wrapped in an {@link Object} array.
     * @param objects {@link Object} array.
     * @return {@link List} of {@link Object} array.
     * @see #oneFromEach(Collection[])
     */
    @NotNull
    public static List<Object[]> oneFromEach(@NotNull Object...objects) {
        return oneFromEach(HPIterables.asList(objects));
    }

    private HPTestNGs() {}
}
