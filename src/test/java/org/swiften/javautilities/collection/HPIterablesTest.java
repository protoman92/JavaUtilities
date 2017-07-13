package org.swiften.javautilities.collection;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by haipham on 5/6/17.
 */
public final class HPIterablesTest {
    @Test
    @SuppressWarnings("unchecked")
    public void test_zipList_shouldSucceed() {
        // Setup
        List list1 = Arrays.asList("a", "b", "c", "d", "e");
        List list2 = Arrays.asList(1, 2, 3, 4, 5, 6);

        // When
        List list3 = HPIterables.zip(list1, list2);

        // Then
        Assert.assertEquals(list3.size(), list1.size());
    }
}
