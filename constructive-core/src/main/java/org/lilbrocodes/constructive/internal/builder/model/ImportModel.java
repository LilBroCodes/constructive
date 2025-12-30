package org.lilbrocodes.constructive.internal.builder.model;

import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;

@ApiStatus.Internal
public record ImportModel(String qualified, boolean isStatic) {
    public ImportModel(String qualified) {
        this(qualified, false);
    }

    public String make() {
        return "import " + (isStatic ? "static " : "") + qualified + ";\n";
    }

    public static final Comparator<ImportModel> COMPARATOR = Comparator.comparing(ImportModel::qualified);
}
