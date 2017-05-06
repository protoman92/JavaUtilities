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
    public static <T> List<T> getNextEvents(@NotNull List<Object> events) {
        return (List)events.get(0);
    }

    /**
     * Get next events from a {@link TestSubscriber}.
     * @param subscriber A {@link TestSubscriber} instance.
     * @return A {@link List} instance.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static List getNextEvents(@NotNull TestSubscriber subscriber) {
        return (List)subscriber.getEvents().get(0);
    }

    /**
     * Get the first next event from a {@link List} of {@link Object}. This
     * can throw a {@link IndexOutOfBoundsException}.
     * @param events A {@link List} of all rx events.
     * @param <T> Generics parameter.
     * @return A {@link T} instance.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T getFirstNextEvent(@NotNull List<Object> events) {
        return (T)getNextEvents(events).get(0);
    }

    /**
     * Get the first next event from a {@link TestSubscriber}. This can throw
     * a {@link IndexOutOfBoundsException}.
     * @param subscriber A {@link TestSubscriber} instance.
     * @param <T> Generics parameter.
     * @return A {@link T} instance.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T getFirstNextEvent(@NotNull TestSubscriber subscriber) {
        return (T)getFirstNextEvent(subscriber.getEvents());
    }
}
