package org.swiften.javautilities.localizer;

import org.swiften.javautilities.collection.Zipped;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import org.reactivestreams.Publisher;
import org.swiften.javautilities.rx.RxTestUtil;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/26/17.
 */
public final class MockLocalizerTest implements LocalizeErrorType {
    @NotNull private final Localizer LC;
    @NotNull private final String[] STR;
    @NotNull private final LocalizationFormat[] FMT;
    @NotNull private final List<ResourceBundle> BUNDLES;
    @NotNull private final List<Locale> LOCALES;

    {
        LC = spy(Localizer.builder().build());
        BUNDLES = new ArrayList<ResourceBundle>();
        LOCALES = new ArrayList<Locale>();
        STR = new String[] { "helloWorld", "goodbyeWorld" };

        FMT = new LocalizationFormat[] {
            mock(LocalizationFormat.class),
            mock(LocalizationFormat.class)
        };
    }

    @BeforeMethod
    public void beforeMethod() {
        ResourceBundle bundle = mock(ResourceBundle.class);

        for (int i = 0, count = 3; i < count; i++) {
            BUNDLES.add(bundle);
        }

        LOCALES.add(Locale.US);
        LOCALES.add(Locale.ENGLISH);
        doReturn(BUNDLES).when(LC).bundles();
        doReturn(LOCALES).when(LC).locales();
    }

    @AfterMethod
    public void afterMethod() {
        BUNDLES.clear();
        LOCALES.clear();
        reset(LC);
    }

    //region Simple localization
    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizationResources_shouldReturnCorrectPairs() {
        int count = BUNDLES.size();

        for (Locale locale : LOCALES) {
            // Setup
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            LC.rxResources(locale).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertEquals(RxTestUtil.nextEventsCount(subscriber), count);
            List<Zipped> next = RxTestUtil.nextEvents(subscriber);

            for (Zipped zipped : next) {
                assertNotNull(zipped.SECOND);
                assertEquals(zipped.SECOND, locale);
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeWithNoResult_shouldEmitOriginal() {
        // Setup
        int times = BUNDLES.size() * LOCALES.size() * STR.length;

        doThrow(new MissingResourceException("", "", "")).when(LC).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromArray(STR)
            .flatMap(new Function<String,Publisher<?>>() {
                @Override
                public Publisher<?> apply(@NonNull final String S1) throws Exception {
                    return LC.rxLocalize(S1, null).doOnNext(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String s2) throws Exception {
                            assertEquals(S1, s2);
                        }
                    });
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LC, times(STR.length)).bundles();
        verify(LC, times(STR.length)).locales();
        verify(LC, times(STR.length)).rxResources((Locale)isNull());

        verify(LC, times(times)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(times)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(STR.length)).rxLocalize(
            anyString(),
            (Locale)isNull()
        );

        verifyNoMoreInteractions(LC);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeWithResult_shouldEmitImmediately() {
        // Setup
        final String CORRECT = "Correct Result";

        doReturn(CORRECT).when(LC).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromArray(STR)
            .flatMap(new Function<String,Publisher<String>>() {
                @Override
                public Publisher<String> apply(@NonNull String s) throws Exception {
                    return LC.rxLocalize(s, null);
                }
            })
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(@NonNull String s) throws Exception {
                    assertEquals(s, CORRECT);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LC, times(STR.length)).bundles();
        verify(LC, times(STR.length)).locales();
        verify(LC, times(STR.length)).rxResources((Locale)isNull());

        verify(LC, times(STR.length)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(STR.length)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(STR.length)).rxLocalize(
            anyString(),
            (Locale)isNull()
        );

        verifyNoMoreInteractions(LC);
    }

    @Test
    public void test_localizeWithNoResult_shouldReturnOriginal() {
        // Setup
        int times = BUNDLES.size() * LOCALES.size() * STR.length;

        doThrow(new MissingResourceException("", "", "")).when(LC).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        // When
        for (String str : STR) {
            String localized = LC.localize(str, null);
            assertTrue(Arrays.asList(STR).contains(localized));
        }

        // Then
        verify(LC, times(STR.length)).bundles();
        verify(LC, times(STR.length)).locales();
        verify(LC, times(STR.length)).rxResources((Locale)isNull());

        verify(LC, times(times)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(times)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(STR.length)).localize(
            anyString(),
            (Locale)isNull()
        );

        verify(LC, times(STR.length)).rxLocalize(
            anyString(),
            (Locale)isNull()
        );

        verifyNoMoreInteractions(LC);
    }

    @Test
    public void test_localizeWithResult_shouldReturnImmediately() {
        // Setup
        final String CORRECT = "Correct Result";

        doReturn(CORRECT).when(LC).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        // When
        for (String str : STR) {
            String localized = LC.localize(str, null);
            assertEquals(localized, CORRECT);
        }

        // Then
        verify(LC, times(STR.length)).bundles();
        verify(LC, times(STR.length)).locales();
        verify(LC, times(STR.length)).rxResources((Locale)isNull());

        verify(LC, times(STR.length)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(STR.length)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LC, times(STR.length)).localize(
            anyString(),
            (Locale)isNull()
        );

        verify(LC, times(STR.length)).rxLocalize(
            anyString(),
            (Locale)isNull()
        );

        verifyNoMoreInteractions(LC);
    }
    //endregion

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeWithFormat_shouldSucceed() {
        // Setup

        // When

        // Then
    }
}
