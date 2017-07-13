package org.swiften.javautilities.collection;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.functional.Tuple;
import org.swiften.javautilities.number.HPNumbers;
import org.swiften.javautilities.object.HPObjects;

import java.util.*;

/**
 * Created by haipham on 3/20/17.
 */
public final class HPIterables {
    /**
     * Check if a {@link Collection} is empty.
     * @param collection {@link Collection} instance.
     * @return {@link Boolean} value.
     * @see Collection#isEmpty()
     */
    public static boolean isEmpty(@NotNull Collection<?> collection) {
        return collection.isEmpty();
    }

    /**
     * Check if a {@link Collection} is not empty.
     * @param collection {@link Collection} instance.
     * @return {@link Boolean} value.
     * @see #isEmpty(Collection)
     */
    public static boolean isNotEmpty(@NotNull Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Concatenate all {@link T} with generics {@link E} into one {@link T}.
     * The first {@link T} in the varargs will be appended to and changed.
     * @param cls A varargs of {@link T}.
     * @param <E> {@link T}'s generics.
     * @param <T> Generics that extends {@link Collection}
     * @return A unified {@link T} instance.
     * @throws Exception Throws {@link Exception} if the varargs is
     * empty. We can simply throw {@link RuntimeException} here.
     */
    public static <E,T extends Collection<E>> T unify(@NotNull T...cls) throws Exception {
        int length = cls.length;

        if (length > 0 && HPObjects.nonNull(cls[0])) {
            T first = cls[0];

            for (int i = 1; i < length; i++) {
                if (i < length) {
                    if (HPObjects.nonNull(cls[i])) {
                        first.addAll(cls[i]);
                    }
                } else {
                    break;
                }
            }

            return first;
        }

        throw new RuntimeException("Varargs cannot be empty");
    }

    /**
     * Zip two {@link Collection} and produce {@link List} of {@link Tuple}.
     * @param a {@link A} object.
     * @param b {@link B} object.
     * @param <A> Generics parameter.
     * @param <B> Generics parameter.
     * @return {@link Collection} of {@link Tuple}.
     */
    @NotNull
    public static <A,B> List<Tuple<A,B>> zip(@NotNull List<A> a, @NotNull List<B> b) {
        List<Tuple<A,B>> zList = new LinkedList<Tuple<A,B>>();
        int aLength = a.size();
        int bLength = b.size();
        int zLength = Math.min(aLength, bLength);

        for (int i = 0; i < zLength; i++) {
            A aItem = a.get(i);
            B bItem = b.get(i);

            if (HPObjects.nonNull(aItem, bItem)) {
                zList.add(Tuple.of(aItem, bItem));
            }
        }

        return zList;
    }

    /**
     * Produce {@link List} from a varargs of {@link T}. If there is only
     * one element in the varargs, use {@link Collections#singletonList(Object)},
     * otherwise use {@link Arrays#asList(Object[])}.
     * @param objects A varargs of {@link T}.
     * @param <T> Generics parameter.
     * @return {@link List} of {@link T}.
     * @see Collections#singletonList(Object)
     * @see Arrays#asList(Object[])
     */
    @NotNull
    public static <T> List<T> asList(@NotNull T...objects) {
        if (objects.length == 0) {
            return Collections.emptyList();
        } else if (objects.length == 1) {
            return Collections.singletonList(objects[0]);
        } else {
            return Arrays.asList(objects);
        }
    }

    /**
     * Create {@link Object} Array from {@link Collection}.
     * @param collection {@link Collection} of {@link Object}
     * @return {@link Object} Array.
     */
    @NotNull
    public static Object[] toArray(@NotNull Collection<?> collection) {
        int length = collection.size();
        return collection.toArray(new Object[length]);
    }

    /**
     * Get a sublist that do not throw {@link IndexOutOfBoundsException}.
     * @param original The original {@link List}.
     * @param from Inclusive starting index.
     * @param to Exclusive ending index.
     * @param <T> Generics parameter.
     * @return {@link List} of {@link T}.
     * @see List#subList(int, int)
     * @see Collections#emptyList()
     */
    @NotNull
    public static <T> List<T> subList(@NotNull List<T> original, int from, int to) {
        int size = original.size();

        if (from > -1 && to < size) {
            return original.subList(from, to);
        } else if (from <= -1) {
            return Collections.emptyList();
        } else if (to >= size) {
            return original.subList(from, size);
        } else {
            return original.subList(0, size);
        }
    }

    /**
     * Convert {@link Iterable} into {@link List}.
     * @param iterable {@link Iterable} instance.
     * @param <T> Generics parameter.
     * @return {@link List} of {@link T}.
     */
    @NotNull
    public static <T> List<T> toList(@NotNull Iterable<T> iterable) {
        List<T> list = new LinkedList<T>();

        for (T t : iterable) {
            list.add(t);
        }

        return list;
    }

    /**
     * Convert {@link Iterable} into {@link Collection}.
     * @param iterable {@link Iterable} instance.
     * @param <T> Generics parameter.
     * @return {@link Collection} of {@link T}.
     * @see #toList(Iterable)
     */
    @NotNull
    public static <T> Collection<T> toCollection(@NotNull Iterable<T> iterable) {
        return toList(iterable);
    }

    /**
     * Produce a random {@link T} from an Array of {@link T}.
     * @param elements Varargs of {@link T} from which the element will be
     *                 produced.
     * @param <T> Generics.
     * @return {@link T} element.
     * @throws RuntimeException If the produced element is null, or the
     * Array is empty.
     * @see HPIterables#randomElement(List)
     */
    @NotNull
    public static <T> T randomElement(@NotNull T...elements) {
        return randomElement(Arrays.asList(elements));
    }

    /**
     * Produce a random {@link T} from {@link List} of {@link T}.
     * @param elements The {@link List} of {@link T} from which the element
     *                 will be produce.
     * @param <T> Generics.
     * @return {@link T} element.
     * @throws RuntimeException If the produced element is null, or the
     * {@link List} is empty.
     */
    @NotNull
    public static <T> T randomElement(@NotNull List<T> elements) {
        if (!elements.isEmpty()) {
            int index = HPNumbers.randomBetween(0, elements.size());
            T element = elements.get(index);

            if (element != null) {
                return element;
            } else {
                throw new RuntimeException("Element cannot be null");
            }
        } else {
            throw new RuntimeException("List/Array cannot be empty");
        }
    }

    private HPIterables() {}
}
