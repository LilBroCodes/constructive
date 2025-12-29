package org.lilbrocodes.constructive.internal.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileOutputTarget implements OutputTarget {
    private final File root;

    public FileOutputTarget(File root) {
        this.root = root;
    }

    @Override
    public void write(String pkg, String name, String source) throws IOException {
        File dir = pkg.isEmpty()
                ? root
                : new File(root, pkg.replace('.', '/'));

        dir.mkdirs();

        File file = new File(dir, name + ".java");
        Files.writeString(file.toPath(), source);
    }
}
