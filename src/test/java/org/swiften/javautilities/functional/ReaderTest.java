package org.swiften.javautilities.functional;

import org.swiften.javautilities.collection.HPIterables;
import org.swiften.javautilities.number.HPNumbers;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.fail;

/**
 * Created by haipham on 7/13/17.
 */
public final class ReaderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void test_readerMonad_shouldWork() {
        // Setup
        Reader<Integer, Integer> r1 = Reader.from(a -> a * 2);
        Reader<Integer, Integer> r2 = Reader.from(a -> a * 3);
        Reader<Integer, Integer> r2_fm_r1 = r2.flatMap(a -> r1);

        Reader<Integer, Integer> r3 = Reader.from(a -> {
            throw new RuntimeException("Error");
        });

        Reader<Integer, Integer> r1_zw_r2 = r1.zipWith(r2, (a, b) -> a + b);
        Reader<Integer, Double> z_r1_r2 = Reader.zip(HPIterables.asList(r1, r2), HPNumbers::sum);

        // When & Then
        try {
             assertEquals(r1.run(1), Integer.valueOf(2));
             assertEquals(r2.run(2), Integer.valueOf(6));
             assertEquals(r2_fm_r1.run(4), Integer.valueOf(8));
             assertEquals(r1_zw_r2.run(3), Integer.valueOf(15));
             assertEquals(z_r1_r2.run(3), 15d);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            r3.map(a -> a + 3).flatMap(a -> r1).run(3);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Error");
        }
    }
}
