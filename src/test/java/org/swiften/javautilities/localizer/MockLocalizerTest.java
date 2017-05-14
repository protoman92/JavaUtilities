package org.swiften.javautilities.localizer;

import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.collection.CollectionTestUtil;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.collection.Zipped;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import org.reactivestreams.Publisher;
import org.swiften.javautilities.rx.RxTestUtil;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/26/17.
 */
public final class MockLocalizerTest implements LocalizeErrorType {
    @NotNull private final Localizer LC;
    @NotNull private final List<String> STR;
    @NotNull private final List<LocalizationFormat> FMT;
    @NotNull private final List<ResourceBundle> BUNDLES;
    @NotNull private final List<Locale> LOCALES;

    {
        LC = spy(Localizer.builder().build());
        BUNDLES = new ArrayList<ResourceBundle>();

        LOCALES = new ArrayList<Locale>();
        LOCALES.add(Locale.US);
        LOCALES.add(Locale.ENGLISH);
        STR = new LinkedList<String>();
        FMT = new LinkedList<LocalizationFormat>();
    }

    @BeforeMethod
    public void beforeMethod() {
        Collections.addAll(STR, "helloworld");
        Collections.addAll(FMT, mock(LocalizationFormat.class));

        for (int i = 0, count = 2; i < count; i++) {
            BUNDLES.add(mock(ResourceBundle.class));
        }

        for (ResourceBundle bundle : BUNDLES) {
            Locale locale = CollectionTestUtil.randomElement(LOCALES);
            doReturn(locale).when(bundle).getLocale();
        }

        for (LocalizationFormat format : FMT) {
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
        List<Object[]> data = new LinkedList<Object[]>();
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
     * @param locale A {@link Locale} instance.
     * @return A {@link Integer} value.
     * @see ResourceBundle#getLocale()
     */
    private int bundleCount(@Nullable Locale locale) {
        if (ObjectUtil.isNull(locale)) {
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
            LC.rxResources(locale).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            assertEquals(RxTestUtil.nextEventsCount(subscriber), included);
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
            .flatMap(new Function<String,Publisher<?>>() {
                @Override
                public Publisher<?> apply(@NotNull final String S1) throws Exception {
                    return LC.rxLocalize(S1, LOCALE).doOnNext(new Consumer<String>() {
                        @Override
                        public void accept(@NotNull String s2) throws Exception {
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
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxResources(eq(LOCALE));
        verify(LC, times(times)).getString(any(ResourceBundle.class), anyString());
        verify(LC, times(times)).rxGetString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).rxLocalize(anyString(), eq(LOCALE));
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
            .flatMap(new Function<String,Publisher<String>>() {
                @NotNull
                @Override
                public Publisher<String> apply(@NotNull String s) throws Exception {
                    return LC.rxLocalize(s, LOCALE);
                }
            })
            .doOnNext(new Consumer<String>() {
                @Override
                public void accept(@NotNull String s) throws Exception {
                    assertEquals(s, CORRECT);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxResources(eq(LOCALE));
        verify(LC, times(STR.size())).getString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).rxGetString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).rxLocalize(anyString(), eq(LOCALE));
        verifyNoMoreInteractions(LC);
    }

    @Test(dataProvider = "localeProvider")
    public void test_localizeWithNoResult_shouldReturnOriginal(@Nullable Locale locale) {
        // Setup
        int times = bundleCount(locale) * STR.size();
        doThrow(mre()).when(LC).getString(any(ResourceBundle.class), anyString());

        // When
        for (String str : STR) {
            String localized = LC.localize(str, locale);
            assertTrue(STR.contains(localized));
        }

        // Then
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxResources(eq(locale));

        verify(LC, times(times)).getString(any(ResourceBundle.class), anyString());
        verify(LC, times(times)).rxGetString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).localize(anyString(), eq(locale));
        verify(LC, times(STR.size())).rxLocalize(anyString(), eq(locale));
        verifyNoMoreInteractions(LC);
    }

    @Test(dataProvider = "localeProvider")
    public void test_localizeWithResult_shouldReturnImmediately(@Nullable Locale locale) {
        // Setup
        final String CORRECT = "Correct Result";
        doReturn(CORRECT).when(LC).getString(any(ResourceBundle.class), anyString());

        // When
        for (String str : STR) {
            String localized = LC.localize(str, locale);
            assertEquals(localized, CORRECT);
        }

        // Then
        verify(LC, times(STR.size())).bundles();
        verify(LC, times(STR.size())).rxResources(eq(locale));
        verify(LC, times(STR.size())).getString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).rxGetString(any(ResourceBundle.class), anyString());
        verify(LC, times(STR.size())).localize(anyString(), eq(locale));
        verify(LC, times(STR.size())).rxLocalize(anyString(), eq(locale));
        verifyNoMoreInteractions(LC);
    }
    //endregion

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "localeProvider")
    public void test_rxLocalizeFormatWithNullTemplate_shouldEmitOriginal(@Nullable final Locale LOCALE) {
        // Setup
        int times = bundleCount(LOCALE) * FMT.size();
        doReturn(Flowable.empty()).when(LC).rxGetTemplate(any(ResourceBundle.class), anyString());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromIterable(FMT)
            .flatMap(new Function<LocalizationFormat,Publisher<String>>() {
                @Override
                public Publisher<String> apply(@NotNull LocalizationFormat format) throws Exception {
                    return LC.rxLocalize(format, LOCALE);
                }
            })
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LC, times(FMT.size())).bundles();
        verify(LC, times(FMT.size())).rxResources(eq(LOCALE));
        verify(LC, times(FMT.size())).rxLocalize(any(LocalizationFormat.class), eq(LOCALE));
        verify(LC, times(times)).rxGetString(any(ResourceBundle.class), any(LocalizationFormat.class));
        verify(LC, times(times)).rxGetTemplate(any(ResourceBundle.class), anyString());
        verify(LC, times(times)).rxFormatArguments(any(Locale.class), any(LocalizationFormat.class));
        verifyNoMoreInteractions(LC);
    }

//    @SuppressWarnings("unchecked")
//    @Test(dataProvider = "localeProvider")
//    public void test_rxLocalizeWithNestedFormat_shouldSucceed(@Nullable final Locale LOCALE) {
//        // Setup
//        int bundleCount = bundleCount(LOCALE);
//        LogUtil.println("Current locale", LOCALE);
//        LogUtil.println("Bundle count", bundleCount);
//        int fmtCount = FMT.size();
//        int nestedFormatCount = 2;
//        int nestedStringCount = 1;
//        int totalNestedFormatCount = ((nestedFormatCount + 1) * fmtCount) * bundleCount;
//        int totalNestedCount = ((nestedFormatCount + nestedStringCount) * fmtCount) * bundleCount + fmtCount;
//        int totalGetStringCount = (nestedStringCount * 2 * fmtCount) * bundleCount;
//        doReturn(Flowable.just("Template")).when(LC).rxGetTemplate(any(ResourceBundle.class), anyString());
//        doThrow(mre()).when(LC).getString(any(MessageFormat.class), any(Object[].class));
//
//        for (LocalizationFormat format : FMT) {
//            List<Object> arguments = new ArrayList<Object>();
//
//            for (int i = 0; i < nestedFormatCount; i++) {
//                LocalizationFormat nested = mock(LocalizationFormat.class);
//                doReturn("template2").when(nested).pattern();
//                doReturn(new Object[0]).when(nested).arguments();
//                arguments.add(nested);
//            }
//
////            for (int i = 0; i < nestedStringCount; i++) {
////                arguments.add("localizable string");
////            }
//
//            LogUtil.println("Args for fmt", format, arguments);
//
//            doReturn(CollectionUtil.toArray(arguments)).when(format).arguments();
//        }
//
//        TestSubscriber subscriber = CustomTestSubscriber.create();
//
//        // When
//        Flowable.fromIterable(FMT)
//            .flatMap(new Function<LocalizationFormat,Publisher<String>>() {
//                @Override
//                public Publisher<String> apply(@NotNull LocalizationFormat format) throws Exception {
//                    return LC.rxLocalize(format, LOCALE);
//                }
//            })
//            .subscribe(subscriber);
//
//        subscriber.awaitTerminalEvent();
//
//        // Then
//        subscriber.assertSubscribed();
//        subscriber.assertNoErrors();
//        subscriber.assertComplete();
//        LogUtil.println(RxTestUtil.firstNextEvent(subscriber));
//        verify(LC, times(totalNestedCount)).bundles();
//        verify(LC, times(FMT.size())).rxResources(eq(LOCALE));
//        verify(LC, times(FMT.size())).rxLocalize(any(LocalizationFormat.class), eq(LOCALE));
//        verify(LC, times(totalNestedFormatCount)).rxGetTemplate(any(ResourceBundle.class), anyString());
//        verify(LC, times(totalNestedFormatCount)).rxGetString(any(ResourceBundle.class), any(LocalizationFormat.class));
//        verify(LC, times(totalGetStringCount)).rxGetString(any(ResourceBundle.class), anyString());
//        verify(LC, times(totalGetStringCount)).getString(any(ResourceBundle.class), anyString());
//        verify(LC, times(totalNestedFormatCount)).rxFormatArguments(any(Locale.class), any(LocalizationFormat.class));
//        verify(LC, times(totalNestedFormatCount)).rxPrepareArgument(any(Locale.class), any());
//        verifyNoMoreInteractions(LC);
//    }
}