package org.lilbrocodes.constructive.internal.builder.model;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public record ConstructiveClass(
    String packageName,
    String className,
    String builderName,
    String builderPackage,
    List<FieldModel> fields,
    boolean needsWrapping
) {}
