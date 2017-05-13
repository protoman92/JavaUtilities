package org.swiften.javautilities.rx;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by haipham on 5/6/17.
 */
public final class RxTestUtil {
    /**
     * Get next events from a {@link List} of {@link Object}.
     * @param events A {@link List} of all rx events.
     * @param <T> Generics parameter.
     * @return A {@link List} of {@link T}.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> List<T> nextEvents(@NotNull List<Object> events) {
        return (List)events.get(0);
    }

    /**
     * Get next events from a {@link TestSubscriber}.
     * @param subscriber A {@link TestSubscriber} instance.
     * @return A {@link List} instance.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static List nextEvents(@NotNull TestSubscriber subscriber) {
        return (List)subscriber.getEvents().get(0);
    }

    /**
     * Get the number of next events from a {@link List} of {@link Object}.
     * @param events A {@link List} of {@link Object}.
     * @return An {@link Integer} value.
     * @see #nextEvents(List)
     */
    public static int nextEventsCount(@NotNull List<Object> events) {
        return nextEvents(events).size();
    }

    /**
     * Get the number of next events from a {@link TestSubscriber}.
     * @param subscriber A {@link TestSubscriber} instance.
     * @return An {@link Integer} value.
     * @see #nextEvents(TestSubscriber)
     */
    public static int nextEventsCount(@NotNull TestSubscriber subscriber) {
        return nextEvents(subscriber).size();
    }

    /**
     * Get the first next event from a {@link List} of {@link Object}. This
     * can throw a {@link IndexOutOfBoundsException}.
     * @param events A {@link List} of all rx events.
     * @param <T> Generics parameter.
     * @return A {@link T} instance.
     * @see #nextEvents(List)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T firstNextEvent(@NotNull List<Object> events) {
        return (T) nextEvents(events).get(0);
    }

    /**
     * Get the first next event from a {@link TestSubscriber}. This can throw
     * a {@link IndexOutOfBoundsException}.
     * @param subscriber A {@link TestSubscriber} instance.
     * @param <T> Generics parameter.
     * @return A {@link T} instance.
     * @see #nextEvents(TestSubscriber)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T firstNextEvent(@NotNull TestSubscriber subscriber) {
        return (T) firstNextEvent(subscriber.getEvents());
    }

    private RxTestUtil() {}
}
