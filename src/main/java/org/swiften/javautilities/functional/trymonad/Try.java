package org.swiften.javautilities.functional.trymonad;

/**
 * Created by haipham on 11/7/17.
 */
public abstract class Try<Val> {
    private static final class Success extends Try {

    }

    private static final class Failure extends Try {

    }
}
