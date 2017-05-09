package org.swiften.javautilities.collection;

import org.swiften.javautilities.number.NumberTestUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Created by haipham on 5/6/17.
 */
public final class CollectionTestUtil {
    /**
     * Produce a random {@link T} from an Array of {@link T}.
     * @param elements The Array of {@link T} from which the element will be
     *                 produced.
     * @param <T> Generics.
     * @return A {@link T} element.
     * @throws RuntimeException If the produced element is null, or the
     * Array is empty.
     * @see CollectionTestUtil#randomElement(List)
     */
    @NotNull
    public static <T> T randomElement(@NotNull T[] elements) {
        return randomElement(Arrays.asList(elements));
    }

    /**
     * Produce a random {@link T} from a {@link List} of {@link T}.
     * @param elements The {@link List} of {@link T} from which the element
     *                 will be produce.
     * @param <T> Generics.
     * @return A {@link T} element.
     * @throws RuntimeException If the produced element is null, or the
     * {@link List} is empty.
     */
    @NotNull
    public static <T> T randomElement(@NotNull List<T> elements) {
        if (!elements.isEmpty()) {
            int index = NumberTestUtil.randomBetween(0, elements.size());
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

    private CollectionTestUtil() {}
}
