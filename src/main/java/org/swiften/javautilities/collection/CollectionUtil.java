package org.swiften.javautilities.collection;

import org.swiften.javautilities.object.ObjectUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by haipham on 3/20/17.
 */
public class CollectionUtil {
    /**
     * Concatenate all {@link T} with generics {@link E} into one {@link T}.
     * The first {@link T} in the varargs will be appended to and changed.
     * @param cls A varargs of {@link T}.
     * @param <E> {@link T}'s generics.
     * @param <T> Generics that extends {@link Collection}
     * @return A unified {@link T} instance.
     * @throws Exception Throws an {@link Exception} if the varargs is
     * empty. We can simply throw a {@link RuntimeException} here.
     */
    public static <E,T extends Collection<E>> T unify(@NotNull T...cls) throws Exception {
        int length = cls.length;

        if (length > 0 && ObjectUtil.nonNull(cls[0])) {
            T first = cls[0];

            for (int i = 1; i < length; i++) {
                if (i < length) {
                    if (ObjectUtil.nonNull(cls[i])) {
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
     * Zip two {@link Collection} and produce a {@link List} of {@link Zipped}.
     * @param a An {@link A} object.
     * @param b A {@link B} object.
     * @param <A> Generics parameter.
     * @param <B> Generics parameter.
     * @return A {@link Collection} of {@link Zipped}.
     */
    @NotNull
    public static <A,B> List<Zipped> zip(@NotNull List<A> a, @NotNull List<B> b) {
        List<Zipped> zList = new LinkedList<Zipped>();
        int aLength = a.size();
        int bLength = b.size();
        int zLength = Math.min(aLength, bLength);

        for (int i = 0; i < zLength; i++) {
            A aItem = a.get(i);
            B bItem = b.get(i);
            zList.add(new Zipped<A,B>(aItem, bItem));
        }

        return zList;
    }

    /**
     * Produce a {@link List} from a varargs of {@link T}. If there is only
     * one element in the varargs, use {@link Collections#singletonList(Object)},
     * otherwise use {@link Arrays#asList(Object[])}.
     * @param objects A varargs of {@link T}.
     * @param <T> Generics parameter.
     * @return A {@link List} of {@link T}.
     * @see Collections#singletonList(Object)
     * @see Arrays#asList(Object[])
     */
    @NotNull
    public static <T> List<T> asList(@NotNull T...objects) {
        if (objects.length == 1) {
            return Collections.singletonList(objects[0]);
        } else {
            return Arrays.asList(objects);
        }
    }

    private CollectionUtil() {}
}
