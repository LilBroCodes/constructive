package org.lilbrocodes.constructive.internal.common;

import java.io.IOException;

public interface OutputTarget {
    void write(String packageName, String className, String source) throws IOException;
}
