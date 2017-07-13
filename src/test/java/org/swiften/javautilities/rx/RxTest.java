package org.swiften.javautilities.rx;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.functional.Tuple;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.util.HPLog;
import org.swiften.javautilities.number.HPNumbers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/25/17.
 */
@SuppressWarnings("WeakerAccess")
public final class RxTest {
    @NotNull
    public Flowable<Boolean> predicateFlowable() {
        final Random RAND = new Random();

        return Flowable.just(1)
            .map(a -> RAND.nextInt(100))
            .map(a -> {
                HPLog.printft("Current predicate: %s", a);
                return a > 50;
            });
    }

    @NotNull
    public RxParam defaultRPParam() {
        return RxParam.builder()
            .withDelay(500)
            .withTimeUnit(TimeUnit.MILLISECONDS)
            .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_fromCollectionWithIndex() {
        // Setup
        TestSubscriber observer = CustomTestSubscriber.create();
        List<Integer> collection = Arrays.asList(1, 2, 3, 4);

        // When
        HPReactives.from(collection).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        List nextEvents = HPReactives.nextEvents(observer);
        Assert.assertEquals(collection.size(), nextEvents.size());

        for (int i = 0, length = collection.size(); i < length; i++) {
            Index index = (Index)nextEvents.get(i);
            Tuple tuple = Tuple.of(i, collection.get(i));
            Assert.assertEquals(index.toZipped(), tuple);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_flatMapAndSwitchMap() {
        // Setup
        PublishSubject<Integer> publishSubject = PublishSubject.create();
        TestSubscriber<Integer> subscriber = CustomTestSubscriber.create();

        // When
        publishSubject
            .observeOn(Schedulers.io())
            .switchMap(integer -> Observable.range(0, integer))
            .map(a -> a * 2)
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
        HPLog.println(HPReactives.nextEvents(subscriber));
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
            .concatMap(a -> Flowable
                .timer(RAND.nextInt(10), TimeUnit.MILLISECONDS)
                .map(b -> a))
            .flatMap(Flowable::just)
            .doOnNext(a -> HPLog.printft("Number %d", a))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.println(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_flatMapComplex() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(0, 10)
            .flatMap(a -> Flowable.just(a * 2))
            .flatMap(Flowable::just, t -> Flowable.just(1), () -> Flowable.just(2))
            .doOnNext(HPLog::println)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.println(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_concatAsync() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        final Flowable<Object> f1 = Flowable
            .just("Starting first stream")
            .doOnNext(HPLog::printlnt)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map(s -> 1);

        Flowable<Object> f2 = Flowable
            .just("Starting second stream")
            .doOnNext(HPLog::printlnt)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map(s -> 2);

        // When
        HPReactives.concatDelayEach(1000, f1, f2)
            .doOnNext(a -> HPLog.printft("Post-delay: %s", a))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.printlnt(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_concatMap_flatMap() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(0, 10)
            .concatMap((Function<Integer, Publisher<?>>) integer -> {
                long delay = HPNumbers.randomBetween(100, 500);
                TimeUnit unit = TimeUnit.MILLISECONDS;
                return Flowable.just(integer).delay(delay, unit);
            })
            .flatMap(a -> Flowable.just(((Integer)a) * 2))
            .doOnNext(HPLog::printlnt)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.printlnt(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatWhen() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(true)
            .repeatWhen(a -> a
                .doOnNext(HPLog::println)
                .flatMap(b -> HPReactives.error())
                .onErrorReturnItem(true))
            .repeat(3)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.println(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatWhile() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(1)
            .compose(HPReactives.repeatWhile(predicate, param))
            .doOnNext(HPLog::printlnt)
            .count().toFlowable()
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.println(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatUntil() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(1)
            .compose(HPReactives.repeatUntil(predicate, param))
            .doOnNext(HPLog::printlnt)
            .count().toFlowable()
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.println(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_delayRetry() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(1)
            .flatMap(i -> HPReactives.error("Error!"))
            .doOnError(a -> HPLog.println(a.getMessage()))
            .compose(HPReactives.delayRetry(3, i -> i * 500L, TimeUnit.MILLISECONDS))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.printlnt(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_removeFromString() {
        // Setup
        LocalizerType localizer = Localizer.builder().build();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just("ABC DEF GHJ")
            .compose(HPReactives.removeFromString(localizer, "A", "E", "H"))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.printlnt(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_retryWhile() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        HPReactives.error("Error!")
            .doOnError(a -> HPLog.printlnt(a.getMessage()))
            .compose(HPReactives.retryWhile(predicate, param))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.printlnt(HPReactives.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_doWhile_doUntil() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.concatArray(
            HPReactives.<Integer, RxParam>doWhile(Flowable.just(1), predicate, param),
            HPReactives.<Integer, RxParam>doUntil(Flowable.just(2), predicate, param)
        ).doOnNext(i -> {
            if (i == 1) {
                HPLog.printlnt("doWhile running");
            } else if (i == 2) {
                HPLog.printlnt("doUntil running");
            }
        }).count().toFlowable().subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        HPLog.printlnt(HPReactives.nextEvents(subscriber));
    }
}
