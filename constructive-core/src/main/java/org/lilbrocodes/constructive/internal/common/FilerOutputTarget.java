package org.lilbrocodes.constructive.internal.common;

import org.jetbrains.annotations.ApiStatus;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

@ApiStatus.Internal
@SuppressWarnings("ClassCanBeRecord")
public final class FilerOutputTarget implements OutputTarget {
    private final Filer filer;

    public FilerOutputTarget(Filer filer) {
        this.filer = filer;
    }

    @Override
    public void write(String pkg, String name, String src) throws IOException {
        JavaFileObject file = filer.createSourceFile(pkg + "." + name);
        try (Writer w = file.openWriter()) {
            w.write(src);
        }
    }
}
