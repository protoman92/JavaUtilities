package org.swiften.javautilities.rx;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.collection.Zip;
import org.swiften.javautilities.log.LogUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/25/17.
 */
public final class RxTest {
    @Test
    @SuppressWarnings("unchecked")
    public void test_fromCollectionWithIndex() {
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
            Zip zip = new Zip(i, collection.get(i));
            Assert.assertEquals(index.toZipped(), zip);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_flatMapAndSwitchMap() {
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

    @Test
    @SuppressWarnings("unchecked")
    public void test_concatMap() {
        // Setup
        final Random RAND = new Random();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(1, 100)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .concatMap(new Function<Integer,Publisher<Integer>>() {
                @NotNull
                @Override
                public Publisher<Integer> apply(@NotNull final Integer I) throws Exception {
                    return Flowable
                        .timer(RAND.nextInt(10), TimeUnit.MILLISECONDS)
                        .map(new Function<Long,Integer>() {
                            @NotNull
                            @Override
                            public Integer apply(@NotNull Long l) throws Exception {
                                return I;
                            }
                        });
                }
            })
            .flatMap(new Function<Integer, Publisher<Integer>>() {
                @NotNull
                @Override
                public Publisher<Integer> apply(@NotNull Integer i) throws Exception {
                    return Flowable.just(i);
                }
            })
            .doOnNext(new Consumer<Integer>() {
                @Override
                public void accept(@NotNull Integer i) throws Exception {
                    LogUtil.printfThread("Number %d", i);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxTestUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_serializedSubject() {
        // Setup
        final PublishSubject SUBJECT = PublishSubject.create();
        final Random RAND = new Random();
        List<Thread> threads = new LinkedList<Thread>();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        SUBJECT.toFlowable(BackpressureStrategy.ERROR)
            .serialize()
            .subscribe(subscriber);

        for (int i = 0; i < 10000; i++) {
            final int INDEX = i;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long duration = RAND.nextInt(200);
                        TimeUnit.MILLISECONDS.sleep(duration);
                        SUBJECT.onNext(INDEX);
                    } catch (InterruptedException e) {
                        LogUtil.println(e);
                    }
                }
            });

            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            TimeUnit.SECONDS.sleep(3);
            SUBJECT.onComplete();
            subscriber.awaitTerminalEvent();

            // Then
            LogUtil.println(RxTestUtil.nextEvents(subscriber));
        } catch (InterruptedException e) {
            LogUtil.println(e);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_flatMapComplex() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(0, 10)
            .flatMap(new Function<Integer,Publisher<?>>() {
                @Override
                public Publisher<?> apply(@NotNull Integer i) throws Exception {
                    return Flowable.just(i * 2);
                }
            })
            .flatMap(
                new Function<Object, Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> apply(@NotNull Object o) throws Exception {
                        return Flowable.just(o);
                    }
                },
                new Function<Throwable, Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> apply(@NotNull Throwable t) throws Exception {
                        return Flowable.just(1);
                    }
                },
                new Callable<Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> call() throws Exception {
                        return Flowable.just(2);
                    }
                }
            )
            .doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    LogUtil.println(o);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxTestUtil.nextEvents(subscriber));
    }
}
