package org.lilbrocodes.constructive.internal.builder.model;

import java.util.Comparator;

public record ImportModel(String qualified) {
    public String make() {
        return "import " + qualified + ";\n";
    }

    public static final Comparator<ImportModel> COMPARATOR = Comparator.comparing(ImportModel::qualified);
}
