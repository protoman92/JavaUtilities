package org.swiften.javautilities.functional;

import io.reactivex.functions.BiFunction;
import org.swiften.javautilities.object.HObjects;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 7/12/17.
 */
public final class OptionTest {
    @Test
    public void test_optionMonad_shouldWork() {
        // Setup
        Option<Integer> o1 = Option.some(1);
        Option<Integer> o2 = Option.nothing();
        Option<Integer> o1a = o1.map(a -> a * 2).flatMap(a -> Option.some(100));
        Option<Integer> o2a = o2.map(a -> a * 2).flatMap(a -> o1);

        // When & Then
        assertTrue(o1.isPresent());
        assertTrue(o2.isNothing());
        assertTrue(o1a.isPresent());
        assertTrue(o2a.isNothing());
        assertTrue(o1.asTry().isSuccess());
        assertTrue(o2.asTry().isFailure());
        assertTrue(o1a.asTry().isSuccess());
        assertTrue(o2a.asTry().isFailure());
        assertEquals(o1.get(), Integer.valueOf(1));
        assertEquals(o1a.get(), Integer.valueOf(100));
    }

    @Test
    public void test_optionZipWith_shouldWork() {
        // Setup
        Option<Integer> o1 = Option.nothing();
        Option<Integer> o2 = Option.some(1);
        Option<Integer> o3 = Option.some(2);
        BiFunction<Integer,Integer,Integer> transform = (a, b) -> a + b;

        // When
        Option<Integer> o12 = o1.zipWith(o2, transform);
        Option<Integer> o23 = o2.zipWith(o3, transform);

        // Then
        assertTrue(o12.isNothing());
        assertEquals(HObjects.requireNotNull(o23.get()).intValue(), 3);
    }
}
