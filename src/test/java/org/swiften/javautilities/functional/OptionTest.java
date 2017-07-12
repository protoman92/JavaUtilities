package org.swiften.javautilities.functional;

import org.swiften.javautilities.functional.Option;
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
}
