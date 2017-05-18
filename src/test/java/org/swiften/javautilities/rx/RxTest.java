package org.swiften.javautilities.rx;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.collection.Pair;
import io.reactivex.subscribers.TestSubscriber;
import org.swiften.javautilities.log.LogUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/25/17.
 */
public final class RxTest {
    @Test
    @SuppressWarnings("unchecked")
    public void test_fromCollectionWithIndex_shouldWork() {
        // Setup
        TestSubscriber observer = CustomTestSubscriber.create();
        List<Integer> collection = Arrays.asList(1, 2, 3, 4);

        // When
        RxUtil.from(collection).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        List nextEvents = RxTestUtil.nextEvents(observer);
        Assert.assertEquals(collection.size(), nextEvents.size());

        for (int i = 0, length = collection.size(); i < length; i++) {
            Index index = (Index)nextEvents.get(i);
            Pair pair = new Pair(i, collection.get(i));
            Assert.assertEquals(index.toZipped(), pair);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_flatMap_switchMap() {
        // Setup
        PublishSubject<Integer> publishSubject = PublishSubject.<Integer>create();
        TestSubscriber<Integer> subscriber = CustomTestSubscriber.<Integer>create();

        // When
        publishSubject
            .observeOn(Schedulers.io())
            .switchMap(new Function<Integer,ObservableSource<Integer>>() {
                @Override
                public ObservableSource<Integer> apply(@NotNull Integer integer) throws Exception {
                    return Observable.range(0, integer);
                }
            })
            .map(new Function<Integer,Integer>() {
                @NotNull
                @Override
                public Integer apply(@NotNull Integer i) throws Exception {
                    return i * 2;
                }
            })
            .toFlowable(BackpressureStrategy.BUFFER)
            .delay(100, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(Schedulers.trampoline())
            .subscribe(subscriber);

        publishSubject.onNext(1);
        publishSubject.onNext(2);
        publishSubject.onNext(3);
        publishSubject.onComplete();
        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxTestUtil.nextEvents(subscriber));
    }
}
