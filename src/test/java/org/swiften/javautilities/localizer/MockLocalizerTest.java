package org.swiften.javautilities.localizer;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.collection.HPIterables;
import org.swiften.javautilities.object.HPObjects;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.HPReactives;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 3/26/17.
 */
public final class MockLocalizerTest implements LocalizeErrorType {
    @NotNull private final Localizer LC;
    @NotNull private final List<String> STR;
    @NotNull private final List<LCFormat> FMT;
    @NotNull private final List<ResourceBundle> BUNDLES;
    @NotNull private final List<Locale> LOCALES;

    {
        LC = spy(Localizer.builder().build());
        BUNDLES = new ArrayList<>();

        LOCALES = new ArrayList<>();
        LOCALES.add(Locale.US);
        LOCALES.add(Locale.ENGLISH);
        STR = new LinkedList<>();
        FMT = new LinkedList<>();
    }

    @BeforeMethod
    public void beforeMethod() {
        Collections.addAll(STR, "helloworld");
        Collections.addAll(FMT, mock(LCFormat.class));

        for (int i = 0, count = 2; i < count; i++) {
            BUNDLES.add(mock(ResourceBundle.class));
        }

        for (ResourceBundle bundle : BUNDLES) {
            Locale locale = HPIterables.randomElement(LOCALES);
            doReturn(locale).when(bundle).getLocale();
        }

        for (LCFormat format : FMT) {
            doReturn("pattern1").when(format).pattern();
            doReturn(new Object[0]).when(format).arguments();
        }

        doReturn(BUNDLES).when(LC).bundles();
    }

    @AfterMethod
    public void afterMethod() {
        BUNDLES.clear();
        STR.clear();
        FMT.clear();
        reset(LC);
    }

    @NotNull
    @DataProvider
    public Iterator<Object[]> localeProvider() {
        List<Object[]> data = new LinkedList<>();
        data.add(new Object[] { null });

//        for (Object locale : LOCALES) {
//            data.add(new Object[] { locale });
//        }

        return data.iterator();
    }

    /**
     * Get the number of {@link ResourceBundle} that has a particular
     * {@link Locale}. If no {@link Locale} is provided, return the number
     * of items in {@link #BUNDLES}.
     * @param locale {@link Locale} instance.
     * @return {@link Integer} value.
     * @see ResourceBundle#getLocale()
     */
    private int bundleCount(@Nullable Locale locale) {
        if (HPObjects.isNull(locale)) {
            return BUNDLES.size();
        } else {
            int included = 0;

            for (ResourceBundle bundle : BUNDLES) {
                if (bundle.getLocale().equals(locale)) {
                    included += 1;
                }
            }

            return included;
        }
    }

    @NotNull
    private MissingResourceException mre() {
        return new MissingResourceException("", "", "");
    }

    //region Simple localization
    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizationResources_shouldReturnCorrectPairs() {
        for (Locale locale : LOCALES) {
            // Setup
            int included = bundleCount(locale);
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            LC.rxe_resources(locale).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertEquals(HPReactives.nextEventsCount(subscriber), included);
        }
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "localeProvider")
    public void test_rxLocalizeWithNoResult_shouldEmitOriginal(@Nullable final Locale LOCALE) {
        // Setup
        int times = bundleCount(LOCALE) * STR.size();
        doThrow(mre()).when(LC).getString(any(ResourceBundle.class), anyString());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromIterable(STR)
            .flatMap(a -> LC.rxa_localize(a, LOCALE).doOnNext(b -> assertEquals(a, b)))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxe_resources(eq(LOCALE));
        verify(LC, times(times)).getString(any(ResourceBundle.class), anyString());
        verify(LC, times(times)).rxa_getString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).rxa_localize(anyString(), eq(LOCALE));
        verifyNoMoreInteractions(LC);
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "localeProvider")
    public void test_rxLocalizeWithResult_shouldEmitImmediately(@Nullable final Locale LOCALE) {
        // Setup
        final String CORRECT = "Correct Result";
        doReturn(CORRECT).when(LC).getString(any(ResourceBundle.class), anyString());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromIterable(STR)
            .flatMap(a -> LC.rxa_localize(a, LOCALE))
            .doOnNext(a -> assertEquals(a, CORRECT))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxe_resources(eq(LOCALE));
        verify(LC, times(STR.size())).getString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).rxa_getString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).rxa_localize(anyString(), eq(LOCALE));
        verifyNoMoreInteractions(LC);
    }

    @Test(dataProvider = "localeProvider")
    public void test_localizeWithNoResult_shouldReturnOriginal(@Nullable Locale locale) {
        // Setup
        int times = bundleCount(locale) * STR.size();
        doThrow(mre()).when(LC).getString(any(ResourceBundle.class), any());

        // When
        for (String str : STR) {
            String localized = LC.localize(str, locale);
            assertTrue(STR.contains(localized));
        }

        // Then
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxe_resources(eq(locale));

        verify(LC, times(times)).getString(any(ResourceBundle.class), any());
        verify(LC, times(times)).rxa_getString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).localize(anyString(), eq(locale));
        verify(LC, times(STR.size())).rxa_localize(anyString(), eq(locale));
        verifyNoMoreInteractions(LC);
    }

    @Test(dataProvider = "localeProvider")
    public void test_localizeWithResult_shouldReturnImmediately(@Nullable Locale locale) {
        // Setup
        final String CORRECT = "Correct Result";
        doReturn(CORRECT).when(LC).getString(any(ResourceBundle.class), any());

        // When
        for (String str : STR) {
            String localized = LC.localize(str, locale);
            assertEquals(localized, CORRECT);
        }

        // Then
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxe_resources(eq(locale));
        verify(LC, times(STR.size())).getString(any(ResourceBundle.class), any());
        verify(LC, times(STR.size())).rxa_getString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).localize(anyString(), eq(locale));
        verify(LC, times(STR.size())).rxa_localize(anyString(), eq(locale));
        verifyNoMoreInteractions(LC);
    }
    //endregion

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "localeProvider")
    public void test_rxLocalizeFormatWithNullTemplate_shouldEmitOriginal(@Nullable final Locale LOCALE) {
        // Setup
        int times = bundleCount(LOCALE) * FMT.size();
        doReturn(Flowable.empty()).when(LC).rxa_getTemplate(any(ResourceBundle.class), any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromIterable(FMT)
            .flatMap(a -> LC.rxa_localize(a, LOCALE))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LC, times(FMT.size() * 2)).bundles();
        verify(LC, times(FMT.size() * 2)).rxe_resources(eq(LOCALE));
        verify(LC, times(FMT.size())).rxa_localize(any(LCFormat.class), eq(LOCALE));
        verify(LC, times(FMT.size())).rxa_localize(anyString(), eq(LOCALE));
        verify(LC, times(FMT.size() * 2)).rxa_getString(any(ResourceBundle.class), anyString());
        verify(LC, times(FMT.size() * 2)).getString(any(ResourceBundle.class), anyString());
        verify(LC, times(times)).rxa_getString(any(ResourceBundle.class), any(LCFormat.class));
        verify(LC, times(times)).rxa_getTemplate(any(ResourceBundle.class), anyString());
        verify(LC, times(times)).rxa_formatArguments(any(Locale.class), any(LCFormat.class));
        verifyNoMoreInteractions(LC);
    }
}
