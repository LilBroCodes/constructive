package org.lilbrocodes.constructive.internal.common;

import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

@ApiStatus.Internal
public interface OutputTarget {
    void write(String packageName, String className, String source) throws IOException;
}
