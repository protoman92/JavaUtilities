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
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.javautilities.object.ObjectUtil;
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
                    LogUtil.printft("Number %d", i);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
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
            LogUtil.println(RxUtil.nextEvents(subscriber));
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
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_concatAsync() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        final Flowable<Object> f1 = Flowable
            .just("Starting first stream")
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    LogUtil.printlnt(s);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map(new Function<String,Object>() {
                @NotNull
                @Override
                public Object apply(String s) throws Exception {
                    return 1;
                }
            });

        Flowable<Object> f2 = Flowable
            .just("Starting second stream")
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    LogUtil.printlnt(s);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map(new Function<String,Object>() {
                @NotNull
                @Override
                public Object apply(String s) throws Exception {
                    return 2;
                }
            });

        // When
        RxUtil.concatDelayEach(1000, f1, f2)
            .doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    LogUtil.printft("Post-delay: %s", o);
                }
            })
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
            .concatMap(new Function<Integer,Publisher<?>>() {
                @Override
                public Publisher<?> apply(Integer integer) throws Exception {
                    long delay = NumberUtil.randomBetween(100, 500);
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    return Flowable.just(integer).delay(delay, unit);
                }
            })
            .flatMap(new Function<Object,Publisher<?>>() {
                @Override
                public Publisher<?> apply(Object o) throws Exception {
                    return Flowable.just(((Integer)o) * 2);
                }
            })
            .doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    LogUtil.printlnt(o);
                }
            })
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
            .repeatWhen(new Function<Flowable<Object>,Publisher<?>>() {
                @NotNull
                @Override
                public Publisher<?> apply(@NotNull Flowable<Object> flowable) throws Exception {
                    return flowable
                        .doOnNext(new Consumer<Object>() {
                            @Override
                            public void accept(@NonNull Object o) throws Exception {
                                LogUtil.println(o);
                            }
                        })
                        .flatMap(new Function<Object,Publisher<?>>() {
                            @Override
                            public Publisher<?> apply(@NonNull Object o) throws Exception {
                                return RxUtil.error();
                            }
                        })
                        .onErrorReturnItem(true);
                }
            })
            .repeat(3)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatWhenWithBooleanFlowable() {
        // Setup
        final Random RAND = new Random();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable<Boolean> pred = Flowable.range(0, 100)
            .observeOn(Schedulers.io())
            .map(new Function<Integer,Boolean>() {
                @NotNull
                @Override
                public Boolean apply(@NotNull Integer integer) throws Exception {
                    return RAND.nextBoolean();
                }
            })
            .firstElement()
            .toFlowable()
            .delay(RAND.nextInt(200), TimeUnit.MILLISECONDS)
            .repeat();

        Flowable.just(1)
            .compose(RxUtil.repeatWhile(pred))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_repeatWhenWithConcat() {
        // Setup
        final Random RAND = new Random();
        final int LENGTH = 10;
        TestSubscriber subscriber = CustomTestSubscriber.create();

        Flowable<Boolean> pred = Flowable.just(1)
            .map(new Function<Integer,Integer>() {
                @NotNull
                @Override
                public Integer apply(@NotNull Integer integer) throws Exception {
                    return RAND.nextInt(100);
                }
            })
            .map(new Function<Integer,Boolean>() {
                @NotNull
                @Override
                public Boolean apply(@NotNull Integer integer) throws Exception {
                    return integer > LENGTH;
                }
            })
            .firstElement()
            .toFlowable()
            .repeat();

        Flowable<?> source = Flowable.defer(new Callable<Publisher<?>>() {
            @Override
            public Publisher<?> call() throws Exception {
                return Flowable
                    .concatArray(
                        Flowable.just(1).delay(100, TimeUnit.MILLISECONDS),
                        Flowable.just(2).delay(200, TimeUnit.MILLISECONDS),
                        Flowable.just(3).delay(300, TimeUnit.MILLISECONDS)
                    )
                    .doOnNext(new Consumer<Integer>() {
                        @Override
                        public void accept(@NotNull Integer integer) throws Exception {
                            LogUtil.printlnt(integer);
                        }
                    })
                    .all(new Predicate<Integer>() {
                        @Override
                        public boolean test(@NotNull Integer integer) throws Exception {
                            return ObjectUtil.nonNull(integer);
                        }
                    })
                    .toFlowable();
            }
        });

        // When
        source.compose(RxUtil.repeatWhile(pred))
            .all(new Predicate<Object>() {
                @Override
                public boolean test(@NotNull Object integer) throws Exception {
                    return ObjectUtil.nonNull(integer);
                }
            })
            .toFlowable()
            .subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.println(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_multipleConcat() {
        // Setup
        final Random RAND = new Random();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.concatArray(
            Flowable.range(0, 3)
                .observeOn(Schedulers.io())
                .flatMap(new Function<Integer,Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> apply(@NotNull Integer integer) throws Exception {
                        return Flowable.range(0, 2).delay(RAND.nextInt(200), TimeUnit.MILLISECONDS);
                    }
                }),

            Flowable.range(0, 5)
                .observeOn(Schedulers.io())
                .flatMap(new Function<Integer,Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> apply(@NotNull Integer integer) throws Exception {
                        return Flowable.range(1, 3).delay(RAND.nextInt(200), TimeUnit.MILLISECONDS);
                    }
                })
        ).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                LogUtil.println(o);
            }
        }).subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_delayRetry() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.just(1)
            .flatMap(new Function<Integer,Publisher<?>>() {
                @NotNull
                @Override
                public Publisher<?> apply(@NotNull Integer i) throws Exception {
                    return RxUtil.error("Error!");
                }
            })
            .doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(@NotNull Throwable t) throws Exception {
                    LogUtil.println(t.getMessage());
                }
            })
            .compose(RxUtil.delayRetry(3, new Function<Integer,Long>() {
                @NotNull
                @Override
                public Long apply(@NotNull Integer i) throws Exception {
                    return i * 500L;
                }
            }, TimeUnit.MILLISECONDS))
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
        final Random RAND = new Random();

        Flowable<Boolean> predicate = Flowable.just(1)
            .map(new Function<Integer,Integer>() {
                @NotNull
                @Override
                public Integer apply(@NotNull Integer integer) throws Exception {
                    return RAND.nextInt(100);
                }
            })
            .map(new Function<Integer,Boolean>() {
                @NotNull
                @Override
                public Boolean apply(@NotNull Integer i) throws Exception {
                    LogUtil.printlnt(i);
                    return i > 20;
                }
            });

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        RxUtil.error("Error!")
            .doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(@NotNull Throwable t) throws Exception {
                    LogUtil.printlnt(t.getMessage());
                }
            })
            .compose(RxUtil.retryWhile(predicate))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_doWhile() {
        // Setup
        final Random RAND = new Random();

        Flowable<Boolean> predicate = Flowable.just(1)
            .map(new Function<Integer,Integer>() {
                @NotNull
                @Override
                public Integer apply(@NotNull Integer i) throws Exception {
                    return RAND.nextInt(100);
                }
            })
            .map(new Function<Integer,Boolean>() {
                @NotNull
                @Override
                public Boolean apply(@NotNull Integer i) throws Exception {
                    LogUtil.printft("Current predicate: %s", i);
                    return i > 50;
                }
            });

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        RxUtil.doWhile(Flowable.just(1), predicate, 2)
            .doOnNext(new Consumer<Integer>() {
                @Override
                public void accept(@NotNull Integer i) throws Exception {
                    LogUtil.printlnt(i);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        LogUtil.printlnt(RxUtil.nextEvents(subscriber));
    }
}
