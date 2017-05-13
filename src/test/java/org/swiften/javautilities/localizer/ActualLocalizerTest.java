package org.swiften.javautilities.localizer;

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
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.spy;

/**
 * Created by haipham on 3/26/17.
 */
public final class ActualLocalizerTest implements LocalizeErrorType {
    @NotNull private final Localizer LOCALIZER;
    @NotNull private final String[] STRINGS;

    private int BUNDLE_COUNT = 3;

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
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxLocalizeText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.fromArray(STRINGS)
            .flatMap(new Function<String,Publisher<String>>() {
                @Override
                public Publisher<String> apply(@NonNull String s) throws Exception {
                    return LOCALIZER.rxLocalizeText(s, null);
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
    }
}
