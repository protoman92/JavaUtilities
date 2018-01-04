package org.swiften.javautilities.buildable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by haipham on 4/1/18.
 */
public final class BuildableTest {
    private final class Buildable implements BuildableType<Builder> {
        @NotNull private String text;
        private int number;
        private float flt;

        private Buildable() {
            text = "";
        }

        @NotNull
        @Override
        public Builder builder() {
            return new Builder();
        }

        @NotNull
        @Override
        public Builder cloneBuilder() {
            return builder().withBuildable(this);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o instanceof Buildable) {
                Buildable buildable = (Buildable)o;

                return text.equals(buildable.text)
                    && number == buildable.number
                    && flt == buildable.flt;
            } else {
                return false;
            }
        }
    }

    private final class Builder implements BuilderType<Builder,Buildable> {
        @NotNull private Buildable buildable;

        private Builder() {
            buildable = new Buildable();
        }

        @NotNull
        public Builder withText(@NotNull String text) {
            buildable.text = text;
            return this;
        }

        @NotNull
        public Builder withNumber(int number) {
            buildable.number = number;
            return this;
        }

        @NotNull
        public Builder withFlt(float flt) {
            buildable.flt = flt;
            return this;
        }

        @NotNull
        @Override
        public Builder withBuildable(@NotNull Buildable buildable) {
            return this
                .withText(buildable.text)
                .withNumber(buildable.number)
                .withFlt(buildable.flt);
        }

        @NotNull
        @Override
        public Buildable build() {
            return buildable;
        }
    }

    @Test
    public void test_cloneBuildables_shouldWork() {
        // Setup
        Buildable b1 = new Buildable().builder()
            .withFlt(1)
            .withNumber(1)
            .withText("123")
            .build();

        // When
        Buildable b2 = b1.cloneBuilder().build();
        Buildable b3 = b2.cloneBuilder().withNumber(10).build();

        // Then
        Assert.assertEquals(b1, b2);
        Assert.assertEquals(b3.number, 10);
    }
}
