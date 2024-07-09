/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.sdkv2.client;

import java.util.Iterator;
import java.util.function.Function;

class Transform {

    private Transform() {

    }

    static <I, O> Iterator<O> iterator(final Iterator<I> delegate, final Function<I, O> transform) {
        return new Iterator<O>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public O next() {
                return transform.apply(delegate.next());
            }
        };
    }
}
