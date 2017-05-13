package org.swiften.javautilities.localizer;

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
    @NotNull private final Localizer LOC;
    @NotNull private final String[] STR;
    @NotNull private final List<ResourceBundle> BUNDLES;
    @NotNull private final List<Locale> LOCALES;

    {
        LOC = spy(Localizer.builder().build());
        BUNDLES = new ArrayList<ResourceBundle>();
        LOCALES = new ArrayList<Locale>();

        STR = new String[] {
            "helloWorld",
            "goodbyeWorld"
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
        doReturn(BUNDLES).when(LOC).bundles();
        doReturn(LOCALES).when(LOC).locales();
    }

    @AfterMethod
    public void afterMethod() {
        BUNDLES.clear();
        LOCALES.clear();
        reset(LOC);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizationResources_shouldReturnCorrectPairs() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        int count = BUNDLES.size() * LOCALES.size();

        // When
        LOC.rxLocalizationResources().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(RxTestUtil.nextEventsCount(subscriber), count);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeWithNoResult_shouldEmitOriginal() {
        // Setup
        int times = BUNDLES.size() * LOCALES.size() * STR.length;

        doThrow(new MissingResourceException("", "", "")).when(LOC).getString(
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
                    return LOC.rxLocalizeText(S1).doOnNext(new Consumer<String>() {
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

        verify(LOC, times(STR.length)).rxLocalizationResources();

        verify(LOC, times(times)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(times)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(STR.length)).rxLocalizeText(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeWithResult_shouldEmitImmediately() {
        // Setup
        final String CORRECT = "Correct Result";

        doReturn(CORRECT).when(LOC).getString(
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
                    return LOC.rxLocalizeText(s);
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

        verify(LOC, times(STR.length)).rxLocalizationResources();

        verify(LOC, times(STR.length)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(STR.length)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(STR.length)).rxLocalizeText(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }

    @Test
    public void test_localizeWithNoResult_shouldReturnOriginal() {
        // Setup
        int times = BUNDLES.size() * LOCALES.size() * STR.length;

        doThrow(new MissingResourceException("", "", "")).when(LOC).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        // When
        for (String str : STR) {
            String localized = LOC.localizeText(str);
            assertTrue(Arrays.asList(STR).contains(localized));
        }

        // Then
        verify(LOC, times(STR.length)).rxLocalizationResources();

        verify(LOC, times(times)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(times)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(STR.length)).localizeText(anyString());
        verify(LOC, times(STR.length)).rxLocalizeText(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }

    @Test
    public void test_localizeWithResult_shouldReturnImmediately() {
        // Setup
        final String CORRECT = "Correct Result";

        doReturn(CORRECT).when(LOC).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        // When
        for (String str : STR) {
            String localized = LOC.localizeText(str);
            assertEquals(localized, CORRECT);
        }

        // Then
        verify(LOC, times(STR.length)).rxLocalizationResources();

        verify(LOC, times(STR.length)).getString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(STR.length)).rxGetString(
            any(ResourceBundle.class),
            any(Locale.class),
            anyString()
        );

        verify(LOC, times(STR.length)).localizeText(anyString());
        verify(LOC, times(STR.length)).rxLocalizeText(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }
}
