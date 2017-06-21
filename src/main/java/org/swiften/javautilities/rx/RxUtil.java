package org.swiften.javautilities.rx;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.string.StringUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/31/17.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class RxUtil {
    /**
     * Get next events from {@link List} of {@link Object}.
     * @param events {@link List} of all rx events.
     * @param <T> Generics parameter.
     * @return {@link List} of {@link T}.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> List<T> nextEvents(@NotNull List<Object> events) {
        return (List)events.get(0);
    }

    /**
     * Get next events from {@link TestSubscriber}.
     * @param subscriber {@link TestSubscriber} instance.
     * @return {@link List} instance.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static List nextEvents(@NotNull TestSubscriber subscriber) {
        return (List)subscriber.getEvents().get(0);
    }

    /**
     * Get the number of next events from {@link List} of {@link Object}.
     * @param events {@link List} of {@link Object}.
     * @return {@link Integer} value.
     * @see #nextEvents(List)
     */
    public static int nextEventsCount(@NotNull List<Object> events) {
        return nextEvents(events).size();
    }

    /**
     * Get the number of next events from {@link TestSubscriber}.
     * @param subscriber {@link TestSubscriber} instance.
     * @return {@link Integer} value.
     * @see #nextEvents(TestSubscriber)
     */
    public static int nextEventsCount(@NotNull TestSubscriber subscriber) {
        return nextEvents(subscriber).size();
    }

    /**
     * Get the first next event from {@link List} of {@link Object}. This
     * can throw {@link IndexOutOfBoundsException}.
     * @param events {@link List} of all rx events.
     * @param <T> Generics parameter.
     * @return {@link T} instance.
     * @see #nextEvents(List)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T firstNextEvent(@NotNull List<Object> events) {
        return (T) nextEvents(events).get(0);
    }

    /**
     * Get the first next event from {@link TestSubscriber}. This can throw
     * {@link IndexOutOfBoundsException}.
     * @param subscriber {@link TestSubscriber} instance.
     * @param <T> Generics parameter.
     * @return {@link T} instance.
     * @see #nextEvents(TestSubscriber)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T firstNextEvent(@NotNull TestSubscriber subscriber) {
        return (T)firstNextEvent(subscriber.getEvents());
    }

    /**
     * Apply {@link FlowableTransformer} to an existing {@link Flowable}.
     * Applicable to {@link Flowable#compose(FlowableTransformer)}.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see Flowable#compose(FlowableTransformer)
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> withCommonSchedulers() {
        return new FlowableTransformer<T, T>() {
            @NonNull
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.trampoline());
            }
        };
    }

    /**
     * Create {@link Flowable} from {@link Collection} of {@link T}. This
     * {@link Flowable} emits {@link Index}, allowing us to access the original
     * {@link T} object, as well as its index in the {@link Collection}.
     * @param collection The {@link Collection} from which {@link Flowable}
     *                   will be constructed.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see Flowable#fromIterable(Iterable)
     * @see Flowable#range(int, int)
     * @see Flowable#zip(Publisher, Publisher, BiFunction)
     */
    @NotNull
    public static <T> Flowable<Index<T>> from(@NotNull Collection<T> collection) {
        return Flowable.zip(
            Flowable.fromIterable(collection),
            Flowable.range(0, collection.size()),

            new BiFunction<T, Integer, Index<T>>() {
                @NonNull
                public Index<T> apply(@NonNull T t, @NonNull Integer integer)
                    throws Exception
                {
                    return new Index<T>(t, integer);
                }
            }
        );
    }

    /**
     * Create {@link Flowable} from a varargs of {@link T}. This
     * {@link Flowable} emits {@link Index}, allowing use to access the
     * original {@link T} object, as well as its index in the varargs.
     * @param objects A varargs of {@link T}.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #from(Collection)
     */
    @NotNull
    public static <T> Flowable<Index<T>> from(@NotNull T...objects) {
        return from(Arrays.asList(objects));
    }

    /**
     * Produce an error {@link Flowable} with an error message.
     * @param error {@link String} value.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see Flowable#error(Throwable)
     * @see #error()
     */
    @NotNull
    public static <T> Flowable<T> error(@Nullable String error) {
        if (ObjectUtil.nonNull(error)) {
            return Flowable.error(new RuntimeException(error));
        } else {
            return error();
        }
    }

    /**
     * Produce an error with a formatted {@link String} message.
     * @param format {@link String} value.
     * @param args A varargs of {@link Object}.
     * @param <T> Generics paramter.
     * @return {@link Flowable} instance.
     * @see #error(String)
     */
    @NotNull
    public static <T> Flowable<T> errorF(@NotNull String format,
                                         @NotNull Object...args) {
        return error(String.format(format, args));
    }

    /**
     * Produce an error {@link Flowable} with a blank message.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #error(String)
     */
    @NotNull
    public static <T> Flowable<T> error() {
        return error("");
    }

    /**
     * Periodically emit items and delay the execution of each subsequent
     * {@link Flowable} by a fixed interval.
     * @param DURATION {@link Long} value.
     * @param UNIT {@link TimeUnit} instance.
     * @param flowables {@link Iterable} of {@link Flowable}.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public static <T> Flowable<T> concatDelayEach(
        final long DURATION,
        @NotNull final TimeUnit UNIT,
        @NotNull Iterable<Flowable<T>> flowables
    ) {
        return Flowable.fromIterable(flowables)
            .concatMap(new Function<Flowable<T>,Publisher<T>>() {
                @NotNull
                @Override
                public Publisher<T> apply(@NotNull Flowable<T> flowable) throws Exception {
                    return flowable.delay(DURATION, UNIT);
                }
            });
    }

    /**
     * Same as above, but uses a default {@link TimeUnit}.
     * @param duration {@link Long} value.
     * @param flowables {@link Iterable} of {@link Flowable}.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see TimeUnit#MILLISECONDS
     * @see #concatDelayEach(long, TimeUnit, Iterable)
     */
    @NotNull
    public static <T> Flowable<T> concatDelayEach(
        long duration,
        @NotNull Iterable<Flowable<T>> flowables
    ) {
        return concatDelayEach(duration, TimeUnit.MILLISECONDS, flowables);
    }

    /**
     * Same as above, but uses varargs of {@link Flowable}.
     * @param duration {@link Long} value.
     * @param unit {@link TimeUnit} instance.
     * @param flowables {@link Flowable} varargs.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public static <T> Flowable<T> concatDelayEach(
        long duration,
        @NotNull TimeUnit unit,
        @NotNull Flowable<T>...flowables
    ) {
        Iterable<Flowable<T>> iterable = CollectionUtil.asList(flowables);
        return concatDelayEach(duration, unit, iterable);
    }

    /**
     * Same as above, but uses a default {@link TimeUnit}.
     * @param duration {@link Long} value.
     * @param flowables {@link Flowable} varargs.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see TimeUnit#MILLISECONDS
     * @see #concatDelayEach(long, TimeUnit, Flowable[])
     */
    @NotNull
    public static <T> Flowable<T> concatDelayEach(
        long duration, @NotNull Flowable<T>...flowables
    ) {
        return concatDelayEach(duration, TimeUnit.MILLISECONDS, flowables);
    }

    /**
     * Repeat {@link Flowable} while a {@link Boolean} {@link Flowable} is
     * emitting true.
     * @param WHEN_FLOWABLE {@link Flowable} instance.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see BooleanUtil#isFalse(boolean)
     * @see RxUtil#error()
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> repeatWhile(
        @NotNull final Flowable<Boolean> WHEN_FLOWABLE
    ) {
        return new FlowableTransformer<T,T>() {
            @NotNull
            @Override
            public Publisher<T> apply(@NotNull Flowable<T> upstream) {
                return upstream.repeatWhen(new Function<Flowable<Object>,Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> apply(@NotNull Flowable<Object> flowable) throws Exception {
                        return flowable
                            .flatMap(new Function<Object,Publisher<Boolean>>() {
                                @Override
                                public Publisher<Boolean> apply(@NotNull Object o) throws Exception {
                                    return WHEN_FLOWABLE;
                                }
                            })
                            .flatMap(new Function<Boolean,Publisher<?>>() {
                                @NotNull
                                @Override
                                public Publisher<?> apply(@NotNull Boolean b) throws Exception {
                                    if (BooleanUtil.isFalse(b)) {
                                        return error();
                                    } else {
                                        return Flowable.just(b);
                                    }
                                }
                            })
                            .onErrorReturnItem(false);
                    }
                });
            }
        };
    }

    /**
     * Repeat {@link Flowable} until a {@link Boolean} {@link Flowable}
     * emits false.
     * @param whenFlowable {@link Flowable} instance.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see BooleanUtil#isFalse(boolean)
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> repeatUntil(
        @NotNull Flowable<Boolean> whenFlowable
    ) {
        return repeatWhile(whenFlowable.map(new Function<Boolean,Boolean>() {
            @NotNull
            @Override
            public Boolean apply(@NotNull Boolean b) throws Exception {
                return BooleanUtil.isFalse(b);
            }
        }));
    }

    /**
     * Emit {@link T} while another {@link Flowable} is emitting true.
     * @param SOURCE {@link Flowable} instance.
     * @param WHEN_FL {@link Flowable} instance.
     * @param DEFAULT {@link Publisher} instance for the initial check.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#isTrue(boolean)
     * @see #repeatWhile(Flowable)
     */
    @NotNull
    public static <T> Flowable<T> doWhile(@NotNull final Flowable<T> SOURCE,
                                          @NotNull final Flowable<Boolean> WHEN_FL,
                                          @NotNull final Publisher<T> DEFAULT) {
        return WHEN_FL.flatMap(new Function<Boolean,Publisher<T>>() {
            @NotNull
            @Override
            public Publisher<T> apply(@NotNull Boolean b) throws Exception {
                if (BooleanUtil.isTrue(b)) {
                    return SOURCE.compose(RxUtil.<T>repeatWhile(WHEN_FL));
                } else {
                    return DEFAULT;
                }
            }
        });
    }

    /**
     * Same as above, but use {@link Flowable#empty()} as the default
     * {@link Publisher}.
     * @param source {@link Flowable} instance.
     * @param whenFl {@link Flowable} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #doWhile(Flowable, Flowable, Publisher)
     */
    @NotNull
    public static <T> Flowable<T> doWhile(@NotNull Flowable<T> source,
                                          @NotNull Flowable<Boolean> whenFl) {
        return doWhile(source, whenFl, Flowable.<T>empty());
    }

    /**
     * Same as above, but uses a default {@link T} instance.
     * @param source {@link Flowable} instance.
     * @param whenFl {@link Flowable} instance.
     * @param defValue {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #doWhile(Flowable, Flowable, Publisher)
     */
    @NotNull
    public static <T> Flowable<T> doWhile(@NotNull Flowable<T> source,
                                          @NotNull Flowable<Boolean> whenFl,
                                          @NotNull T defValue) {
        return doWhile(source, whenFl, Flowable.just(defValue));
    }

    /**
     * Emit {@link T} until a {@link Boolean} {@link Flowable} emits true.
     * @param source {@link Flowable} instance.
     * @param untilFl {@link Flowable} instance.
     * @param defPublisher {@link Publisher} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #doWhile(Flowable, Flowable, Publisher)
     */
    @NotNull
    public static <T> Flowable<T> doUntil(@NotNull Flowable<T> source,
                                          @NotNull Flowable<Boolean> untilFl,
                                          @NotNull Publisher<T> defPublisher) {
        return doWhile(source, untilFl.map(new Function<Boolean,Boolean>() {
            @NotNull
            @Override
            public Boolean apply(@NotNull Boolean b) throws Exception {
                return BooleanUtil.isFalse(b);
            }
        }), defPublisher);
    }

    /**
     * Same as above, but uses {@link Flowable#empty()} for the default
     * {@link Publisher}.
     * @param source {@link Flowable} instance.
     * @param untilFl {@link Flowable} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #doUntil(Flowable, Flowable, Publisher)
     */
    @NotNull
    public static <T> Flowable<T> doUntil(@NotNull Flowable<T> source,
                                          @NotNull Flowable<Boolean> untilFl) {
        return doUntil(source, untilFl, Flowable.<T>empty());
    }

    /**
     * Same as above, but uses a default {@link T} instance.
     * @param source {@link Flowable} instance.
     * @param untilFl {@link Flowable} instance.
     * @param defValue {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #doUntil(Flowable, Flowable, Publisher)
     */
    @NotNull
    public static <T> Flowable<T> doUntil(@NotNull Flowable<T> source,
                                          @NotNull Flowable<Boolean> untilFl,
                                          @NotNull T defValue) {
        return doUntil(source, untilFl, Flowable.just(defValue));
    }

    /**
     * {@link Flowable#retryWhen(Function)} while a {@link Boolean}
     * {@link Flowable} is emitting true.
     * @param WHEN_FN {@link Function} instance that produces a {@link Boolean}
     *                {@link Flowable}.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see BooleanUtil#isFalse(boolean)
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> retryWhile(
        @NotNull final Function<Throwable,Flowable<Boolean>> WHEN_FN
    ) {
        return new FlowableTransformer<T,T>() {
            @NotNull
            @Override
            public Publisher<T> apply(@NotNull Flowable<T> source) {
                return source.retryWhen(new Function<Flowable<Throwable>,Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> apply(@NotNull Flowable<Throwable> tf) throws Exception {
                        return tf.flatMap(new Function<Throwable,Publisher<?>>() {
                            @NotNull
                            @Override
                            public Publisher<?> apply(@NotNull Throwable t) throws Exception {
                                return WHEN_FN.apply(t)
                                    .flatMap(new Function<Boolean,Publisher<?>>() {
                                        @NotNull
                                        @Override
                                        public Publisher<?> apply(@NotNull Boolean b) throws Exception {
                                            if (BooleanUtil.isFalse(b)) {
                                                return error();
                                            } else {
                                                return Flowable.just(b);
                                            }
                                        }
                                    });
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * Same as above, but uses a default {@link Flowable} that emits
     * {@link Boolean}.
     * @param WHEN_FL {@link Flowable} instance.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> retryWhile(
        @NotNull final Flowable<Boolean> WHEN_FL
    ) {
        return retryWhile(new Function<Throwable,Flowable<Boolean>>() {
            @NotNull
            @Override
            public Flowable<Boolean> apply(@NotNull Throwable t) throws Exception {
                return WHEN_FL;
            }
        });
    }

    /**
     * {@link Flowable#timeout(long, TimeUnit, Publisher)} with default
     * {@link T} instance.
     * @param DURATION {@link Long} value.
     * @param UNIT {@link TimeUnit} instance.
     * @param VALUE {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> timeout(
        final long DURATION,
        @NotNull final TimeUnit UNIT,
        @NotNull final T VALUE
    ) {
        return new FlowableTransformer<T,T>() {
            @NotNull
            @Override
            public Publisher<T> apply(@NotNull Flowable<T> source) {
                return source.timeout(DURATION, UNIT, Flowable.just(VALUE));
            }
        };
    }

    /**
     * {@link Flowable#retryWhen(Function)}, but with a delay each iteration.
     * @param TIMES {@link Integer} value.
     * @param DELAY_FN {@link Function} instance to produce delay duration.
     * @param UNIT {@link TimeUnit} instance.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> delayRetry(
        final int TIMES,
        @NotNull final Function<Integer,Long> DELAY_FN,
        @NotNull final TimeUnit UNIT
    ) {
        return new FlowableTransformer<T,T>() {
            @NotNull
            @Override
            public Publisher<T> apply(@NotNull Flowable<T> source) {
                return source.retryWhen(new Function<Flowable<Throwable>,Publisher<?>>() {
                    @NotNull
                    @Override
                    public Publisher<?> apply(@NotNull Flowable<Throwable> tf) throws Exception {
                        /* We add one to the retry count, or else we won't be
                         * able to catch when the count is exceeded */
                        return Flowable.zip(
                            tf, Flowable.range(1, TIMES + 1),
                            new BiFunction<Throwable,Integer,Integer>() {
                                @Override
                                public Integer apply(@NotNull Throwable e,
                                                     @NotNull Integer i) throws Exception {
                                    if (i > TIMES) {
                                        throw Exceptions.propagate(e);
                                    } else {
                                        return i;
                                    }
                                }
                            }
                        ).flatMap(new Function<Integer,Publisher<?>>() {
                            @NotNull
                            @Override
                            public Publisher<?> apply(@NotNull Integer o) throws Exception {
                                long delay = DELAY_FN.apply(o);
                                return Flowable.timer(delay, UNIT);
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * Same as above, but uses a default {@link Function}.
     * @param times {@link Integer} value.
     * @param DELAY {@link Long} value.
     * @param unit {@link TimeUnit} instance.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see #delayRetry(int, Function, TimeUnit)
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> delayRetry(
        int times,
        final long DELAY,
        @NotNull TimeUnit unit
    ) {
        Function<Integer,Long> fn = new Function<Integer,Long>() {
            @NotNull
            @Override
            public Long apply(@NotNull Integer integer) throws Exception {
                return DELAY;
            }
        };

        return delayRetry(times, fn, unit);
    }

    /**
     * Same as above, but uses {@link TimeUnit#MILLISECONDS}.
     * @param times {@link Integer} value.
     * @param delay {@link Long} value.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see TimeUnit#MILLISECONDS
     * @see #delayRetry(int, Function, TimeUnit)
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> delayRetry(int times, long delay) {
        return delayRetry(times, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Same as above, but uses {@link TimeUnit#MILLISECONDS}.
     * @param duration {@link Long} value.
     * @param value {@link T} instance.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     * @see TimeUnit#MILLISECONDS
     * @see #timeout(long, TimeUnit, Object)
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> timeout(long duration, @NotNull T value) {
        return timeout(duration, TimeUnit.MILLISECONDS, value);
    }

    /**
     * Reactively remove all instances of {@link String} values from another
     * {@link String}. Before the removal, we localize all {@link String}
     * values to be removed.
     * @param LOCALIZER {@link LocalizerType} instance.
     * @param REMOVABLES {@link String} varargs.
     * @return {@link FlowableTransformer} instance.
     * @see LocalizerType#rxa_localize(String)
     * @see StringUtil#removeAll(String, String)
     */
    @NotNull
    public static FlowableTransformer<String,String> removeFromString(
        @NotNull final LocalizerType LOCALIZER,
        @NotNull final String...REMOVABLES
    ) {
        return new FlowableTransformer<String,String>() {
            @Override
            public Publisher<String> apply(@NonNull Flowable<String> source) {
                return source.flatMap(new Function<String,Publisher<String>>() {
                    @NotNull
                    @Override
                    public Publisher<String> apply(@NotNull String s) throws Exception {
                        return Flowable.fromArray(REMOVABLES)
                            .flatMap(new Function<String,Publisher<String>>() {
                                @NotNull
                                @Override
                                public Publisher<String> apply(@NotNull String s) throws Exception {
                                    return LOCALIZER.rxa_localize(s);
                                }
                            })
                            .reduce(s, new BiFunction<String,String,String>() {
                                @NotNull
                                @Override
                                public String apply(@NotNull String s,
                                                    @NotNull String s2) throws Exception {
                                    return StringUtil.removeAll(s, s2);
                                }
                            })
                            .toFlowable();
                    }
                });
            }
        };
    }

    private RxUtil() {}
}
