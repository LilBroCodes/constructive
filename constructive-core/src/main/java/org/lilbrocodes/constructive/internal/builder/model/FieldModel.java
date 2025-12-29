package org.lilbrocodes.constructive.internal.builder.model;


import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public record FieldModel(
    String name,
    TypeModel type,
    boolean required,
    boolean hardRequire,
    boolean builder,
    boolean transientField,
    boolean composite,
    String description,
    String defaultMethod,
    String defaultValueExpr,
    TypeModel.Declared builderType,
    Map<String, TypeModel> hardRequired
) {
    public boolean hasJavadoc() {
        return description != null || hardRequire || !required || defaultValueExpr != null;
    }

    public boolean isPrimitive() {
        return type instanceof TypeModel.Primitive;
    }

    public boolean isNullable() {
        return !isPrimitive() && !builder;
    }

    public boolean hasDefault() {
        return (defaultMethod != null && !defaultMethod.isBlank()) || (defaultValueExpr != null && !defaultValueExpr.isBlank());
    }

    public boolean isListBuilder() {
        return fcqn() != null && fcqn().equals("java.util.List");
    }

    public boolean isMapBuilder() {
        return fcqn() != null && fcqn().equals("java.util.Map");
    }

    public String fcqn() {
        return type instanceof TypeModel.Declared d ? d.qualified() : null;
    }

    public String make() {
        return builder || composite
                ? name + ".build()"
                : name;
    }

    @Override
    public String toString() {
        return "FieldModel{" +
                "name='" + name + '\'' +
                ", qualified=" + type +
                ", hardRequire=" + hardRequire +
                ", required=" + required +
                ", transientField=" + transientField +
                ", defaultMethod='" + defaultMethod + '\'' +
                ", defaultValueExpr='" + defaultValueExpr + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
