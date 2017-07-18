package org.swiften.javautilities.test;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.collection.HPIterables;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by haipham on 6/18/17.
 */
@SuppressWarnings("UndeclaredTests")
public final class TestUtilTest {
    @Test
    @SuppressWarnings({"MessageMissingOnTestNGAssertion", "unchecked"})
    public void test_oneFromEach() {
        // Setup
        List<Collection<?>> enums = HPIterables.<Collection<?>>asList(
            HPIterables.asList(Enum1.values()),
            HPIterables.asList(Enum2.values()),
            HPIterables.asList(Enum3.values())
        );

        int totalCount = 1;

        for (Collection<?> collection : enums) {
            totalCount *= collection.size();
        }

        // When
        List<Object[]> data = HPTestNGs.oneFromEach(enums);

        // Then
        for (Object[] object : data) {
            assertEquals(object.length, enums.size());
        }

        assertEquals(totalCount, data.size());

        Integer distinct = Flowable.fromIterable(data)
            .flatMap(new Function<Object[],Publisher<String>>() {
                @NotNull
                @Override
                public Publisher<String> apply(@NotNull Object[] objects) throws Exception {
                    return Flowable.fromArray(objects)
                        .cast(EnumProtocol.class)
                        .map(new Function<EnumProtocol,String>() {
                            @NotNull
                            @Override
                            public String apply(@NotNull EnumProtocol o) throws Exception {
                                return o.toString();
                            }
                        })
                        .reduce(new BiFunction<String,String,String>() {
                            @NotNull
                            @Override
                            public String apply(@NotNull String s1,
                                                @NotNull String s2) throws Exception {
                                return s1 + "-" + s2;
                            }
                        })
                        .toFlowable();
                }
            })
            .distinctUntilChanged()
            .count()
            .blockingGet()
            .intValue();

        assertEquals(distinct.intValue(), data.size());
    }

    private interface EnumProtocol {}

    private enum Enum1 implements EnumProtocol {
        E1_CASE1,
        E1_CASE2,
        E1_CASE3,
        E1_CASE4;
    }

    private enum Enum2 implements EnumProtocol {
        E2_CASE1,
        E2_CASE2,
        E2_CASE3;
    }

    private enum Enum3 implements EnumProtocol {
        E3_CASE1,
        E3_CASE2,
        E3_CASE3;
    }
}
