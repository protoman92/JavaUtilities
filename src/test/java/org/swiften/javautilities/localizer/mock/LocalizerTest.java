package org.swiften.javautilities.localizer.mock;

import org.swiften.javautilities.localizer.LocalizeErrorProtocol;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import org.reactivestreams.Publisher;
import org.testng.Assert;
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
public class LocalizerTest implements LocalizeErrorProtocol {
    @NotNull private final Localizer LOC;
    @NotNull private final String[] STR;

    private int BUNDLE_COUNT = 3;

    {
        LOC = spy(Localizer.builder().build());

        STR = new String[] {
            "helloWorld",
            "goodbyeWorld"
        };
    }

    @BeforeMethod
    public void beforeMethod() {
        ResourceBundle bundle = mock(ResourceBundle.class);
        Locale locale = Locale.US;
        List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        List<Locale> locales = new ArrayList<Locale>();

        for (int i = 0, count = BUNDLE_COUNT; i < count; i++) {
            bundles.add(bundle);
            locales.add(locale);
        }

        doReturn(bundles).when(LOC).bundles();
        doReturn(locales).when(LOC).locales();
    }

    @AfterMethod
    public void afterMethod() {
        reset(LOC);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeWithNoResult_shouldEmitOriginal() {
        // Setup
        int times = (int)(Math.pow(BUNDLE_COUNT, 2) * STR.length);

        doReturn("")
            .when(LOC)
            .getString(any(ResourceBundle.class), anyString());

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromArray(STR)
            .flatMap(new Function<String,Publisher<?>>() {
                @Override
                public Publisher<?> apply(@NonNull final String S1) throws Exception {
                    return LOC.rxLocalize(S1).doOnNext(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String s2) throws Exception {
                            Assert.assertEquals(S1, s2);
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
        verify(LOC, times(times)).getString(any(ResourceBundle.class), anyString());
        verify(LOC, times(STR.length)).rxLocalize(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeWithResult_shouldEmitImmediately() {
        // Setup
        final String CORRECT = "Correct Result";

        doReturn(CORRECT)
            .when(LOC)
            .getString(any(ResourceBundle.class), anyString());

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromArray(STR)
            .flatMap(new Function<String,Publisher<String>>() {
                @Override
                public Publisher<String> apply(@NonNull String s) throws Exception {
                    return LOC.rxLocalize(s);
                }
            })
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(@NonNull String s) throws Exception {
                    Assert.assertEquals(s, CORRECT);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOC, times(STR.length)).getString(any(ResourceBundle.class), anyString());
        verify(LOC, times(STR.length)).rxLocalize(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }

    @Test
    public void test_localizeWithNoResult_shouldReturnOriginal() {
        // Setup
        int times = (int)(Math.pow(BUNDLE_COUNT, 2) * STR.length);

        doReturn("")
            .when(LOC)
            .getString(any(ResourceBundle.class), anyString());

        // When
        for (String str : STR) {
            String localized = LOC.localize(str);
            Assert.assertTrue(Arrays.asList(STR).contains(localized));
        }

        // Then
        verify(LOC, times(times)).getString(any(ResourceBundle.class), anyString());
        verify(LOC, times(STR.length)).localize(anyString());
        verify(LOC, times(STR.length)).rxLocalize(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }

    @Test
    public void test_localizeWithResult_shouldReturnImmediately() {
        // Setup
        final String CORRECT = "Correct Result";

        doReturn(CORRECT)
            .when(LOC)
            .getString(any(ResourceBundle.class), anyString());

        // When
        for (String str : STR) {
            String localized = LOC.localize(str);
            Assert.assertEquals(localized, CORRECT);
        }

        // Then
        verify(LOC, times(STR.length)).getString(any(ResourceBundle.class), anyString());
        verify(LOC, times(STR.length)).localize(anyString());
        verify(LOC, times(STR.length)).rxLocalize(anyString());
        verify(LOC, times(STR.length)).bundles();
        verify(LOC, times(STR.length)).locales();
        verifyNoMoreInteractions(LOC);
    }
}
