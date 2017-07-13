package org.swiften.javautilities.functional;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.HPReactives;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * Created by haipham on 11/7/17.
 */
public final class TryTest {
    @Test
    public void test_tryMonad_shouldWork() {
        // Setup
        Try<Integer> t1 = Try.success(1);
        Try<Integer> t2 = Try.failure(new Exception("Error"));
        Try<Integer> t1a = t1.map(a -> a * 2).flatMap(a -> Try.success(100));
        Try<Integer> t2a = t2.map(a -> a * 2).flatMap(a -> t1);

        // When & Then
        assertTrue(t1.isSuccess());
        assertTrue(t2.isFailure());
        assertTrue(t1a.isSuccess());
        assertTrue(t2a.isFailure());
        assertTrue(t1.asOption().isPresent());
        assertTrue(t2.asOption().isNothing());
        assertTrue(t1a.asOption().isPresent());
        assertTrue(t2a.asOption().isNothing());

        try {
            assertEquals(t1.getOrThrow(), Integer.valueOf(1));
            assertEquals(t1a.getOrThrow(), Integer.valueOf(100));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_tryMonadWithRx_shouldWork() {
        // Setup
        TestSubscriber<Try<Integer>> subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(Try.<Integer>failure(new Exception("Error")))
            .map(Tries.<Integer>getFn())
            .map(Tries.<Integer>successFn())
            .onErrorReturn(Tries.failureFn())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        Try first = HPReactives.firstNextEvent(subscriber);
        assertTrue(first.isFailure());
    }
}
