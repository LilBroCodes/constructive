package org.lilbrocodes.constructive.internal.builder.model;

import java.util.List;

public record ConstructiveClass(
    String packageName,
    String className,
    String builderName,
    String builderPackage,
    List<FieldModel> fields,
    boolean needsWrapping
) {}
