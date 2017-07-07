package org.swiften.javautilities.rx;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.collection.Zip;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.javautilities.number.NumberUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
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
                LogUtil.printft("Current predicate: %s", a);
                return a > 50;
            });
    }

    @NotNull
    public RxUtilParam defaultRPParam() {
        return RxUtilParam.builder()
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
        RxUtil.from(collection).subscribe(observer);
        observer.awaitTerminalEvent();

        // Then
        List nextEvents = RxUtil.nextEvents(observer);
        Assert.assertEquals(collection.size(), nextEvents.size());

        for (int i = 0, length = collection.size(); i < length; i++) {
            Index index = (Index)nextEvents.get(i);
            Zip zip = Zip.of(i, collection.get(i));
            Assert.assertEquals(index.toZipped(), zip);
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
        LogUtil.println(RxUtil.nextEvents(subscriber));
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
            .doOnNext(a -> LogUtil.printft("Number %d", a))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
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
            .doOnNext(LogUtil::println)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_concatAsync() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        final Flowable<Object> f1 = Flowable
            .just("Starting first stream")
            .doOnNext(LogUtil::printlnt)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map(s -> 1);

        Flowable<Object> f2 = Flowable
            .just("Starting second stream")
            .doOnNext(LogUtil::printlnt)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map(s -> 2);

        // When
        RxUtil.concatDelayEach(1000, f1, f2)
            .doOnNext(a -> LogUtil.printft("Post-delay: %s", a))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_concatMap_flatMap() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(0, 10)
            .concatMap((Function<Integer, Publisher<?>>) integer -> {
                long delay = NumberUtil.randomBetween(100, 500);
                TimeUnit unit = TimeUnit.MILLISECONDS;
                return Flowable.just(integer).delay(delay, unit);
            })
            .flatMap(a -> Flowable.just(((Integer)a) * 2))
            .doOnNext(LogUtil::printlnt)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatWhen() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(true)
            .repeatWhen(a -> a
                .doOnNext(LogUtil::println)
                .flatMap(b -> RxUtil.error())
                .onErrorReturnItem(true))
            .repeat(3)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatWhile() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxUtilParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(1)
            .compose(RxUtil.repeatWhile(predicate, param))
            .doOnNext(LogUtil::printlnt)
            .count().toFlowable()
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatUntil() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxUtilParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(1)
            .compose(RxUtil.repeatUntil(predicate, param))
            .doOnNext(LogUtil::printlnt)
            .count().toFlowable()
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_delayRetry() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(1)
            .flatMap(i -> RxUtil.error("Error!"))
            .doOnError(a -> LogUtil.println(a.getMessage()))
            .compose(RxUtil.delayRetry(3, i -> i * 500L, TimeUnit.MILLISECONDS))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_removeFromString() {
        // Setup
        LocalizerType localizer = Localizer.builder().build();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just("ABC DEF GHJ")
            .compose(RxUtil.removeFromString(localizer, "A", "E", "H"))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_retryWhile() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxUtilParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        RxUtil.error("Error!")
            .doOnError(a -> LogUtil.printlnt(a.getMessage()))
            .compose(RxUtil.retryWhile(predicate, param))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_doWhile_doUntil() {
        // Setup
        Flowable<Boolean> predicate = predicateFlowable();
        RxUtilParam param = defaultRPParam();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.concatArray(
            RxUtil.<Integer, RxUtilParam>doWhile(Flowable.just(1), predicate, param),
            RxUtil.<Integer, RxUtilParam>doUntil(Flowable.just(2), predicate, param)
        ).doOnNext(i -> {
            if (i == 1) {
                LogUtil.printlnt("doWhile running");
            } else if (i == 2) {
                LogUtil.printlnt("doUntil running");
            }
        }).count().toFlowable().subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }
}
