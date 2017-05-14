package org.swiften.javautilities.localizer;

import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.spy;

/**
 * Created by haipham on 3/26/17.
 */
public final class ActualLocalizerTest implements LocalizeErrorType {
    @NotNull private final Localizer LOCALIZER;
    @NotNull private final String[] STRINGS;
    @NotNull private final LocalizationFormat[] FORMATS;

    {
        LOCALIZER = spy(Localizer.builder()
            .addBundle("Strings", Locale.US)
            .addBundle("Strings", new Locale("vi_VN"))
            .build());

        STRINGS = new String[] {
            "auth_title_email",
            "auth_title_password",
            "auth_title_signInOrRegister",
            "non_localizable_text"
        };

        FORMATS = new LocalizationFormat[] {
            LocalizationFormat.builder()
                .withPattern("format_pattern_1")
                .addArgument(2)
                .addArgument("localizable_cake")
                .addArgument("localizable_table")
                .build(),

            LocalizationFormat.builder()
                .withPattern("format_pattern_2")
                .addArgument("localizable_game")
                .addArgument(LocalizationFormat.builder()
                    .withPattern("nested_format_pattern")
                    .addArgument("localizable_addition")
                    .addArgument("localizable_collection")
                    .build())
                .build()
        };
    }

    @NotNull
    @DataProvider
    public Iterator<Object[]> localeProvider() {
        List<Locale> locales = LOCALIZER.locales();
        List<Object[]> data = new LinkedList<Object[]>();

        for (Locale locale : locales) {
            data.add(new Object[] { locale });
        }

        return data.iterator();
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "localeProvider")
    public void test_rxLocalizeText_shouldSucceed(@Nullable final Locale LOCALE) {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromArray(STRINGS)
            .flatMap(new Function<String,Publisher<String>>() {
                @Override
                public Publisher<String> apply(@NonNull String s) throws Exception {
                    return LOCALIZER.rxLocalize(s, LOCALE);
                }
            })
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(@NonNull String s) throws Exception {
                    LogUtil.println(s);
                }
            })
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(@NonNull String s) throws Exception {
                    Assert.assertTrue(StringUtil.isNotNullOrEmpty(s));
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "localeProvider")
    public void test_rxLocalizeWithFormat_shouldSucceed(@Nullable final Locale LOCALE) {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromArray(FORMATS)
            .flatMap(new Function<LocalizationFormat,Publisher<String>>() {
                @NonNull
                @Override
                public Publisher<String> apply(
                    @NonNull LocalizationFormat format
                ) throws Exception {
                    return LOCALIZER.rxLocalize(format, LOCALE);
                }
            })
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(@NonNull String s) throws Exception {
                    LogUtil.println(s);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
}