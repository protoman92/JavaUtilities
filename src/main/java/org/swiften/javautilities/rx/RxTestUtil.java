package org.swiften.javautilities.rx;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by haipham on 5/6/17.
 */
public final class RxTestUtil {
    /**
     * Get next events from {@link List} of {@link Object}.
     * @param events {@link List} of all rx events.
     * @param <T> Generics parameter.
     * @return {@link List} of {@link T}.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> List<T> nextEvents(@NotNull List<Object> events) {
        return (List)events.get(0);
    }

    /**
     * Get next events from {@link TestSubscriber}.
     * @param subscriber {@link TestSubscriber} instance.
     * @return {@link List} instance.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static List nextEvents(@NotNull TestSubscriber subscriber) {
        return (List)subscriber.getEvents().get(0);
    }

    /**
     * Get the number of next events from {@link List} of {@link Object}.
     * @param events {@link List} of {@link Object}.
     * @return {@link Integer} value.
     * @see #nextEvents(List)
     */
    public static int nextEventsCount(@NotNull List<Object> events) {
        return nextEvents(events).size();
    }

    /**
     * Get the number of next events from {@link TestSubscriber}.
     * @param subscriber {@link TestSubscriber} instance.
     * @return {@link Integer} value.
     * @see #nextEvents(TestSubscriber)
     */
    public static int nextEventsCount(@NotNull TestSubscriber subscriber) {
        return nextEvents(subscriber).size();
    }

    /**
     * Get the first next event from {@link List} of {@link Object}. This
     * can throw {@link IndexOutOfBoundsException}.
     * @param events {@link List} of all rx events.
     * @param <T> Generics parameter.
     * @return {@link T} instance.
     * @see #nextEvents(List)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T firstNextEvent(@NotNull List<Object> events) {
        return (T) nextEvents(events).get(0);
    }

    /**
     * Get the first next event from {@link TestSubscriber}. This can throw
     * {@link IndexOutOfBoundsException}.
     * @param subscriber {@link TestSubscriber} instance.
     * @param <T> Generics parameter.
     * @return {@link T} instance.
     * @see #nextEvents(TestSubscriber)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T firstNextEvent(@NotNull TestSubscriber subscriber) {
        return (T) firstNextEvent(subscriber.getEvents());
    }

    private RxTestUtil() {}
}
