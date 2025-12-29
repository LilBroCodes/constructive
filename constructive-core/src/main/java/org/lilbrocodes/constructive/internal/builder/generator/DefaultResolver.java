package org.lilbrocodes.constructive.internal.builder.generator;

import org.lilbrocodes.constructive.internal.builder.model.FieldModel;
import org.lilbrocodes.constructive.internal.builder.model.ImportModel;
import org.lilbrocodes.constructive.internal.builder.model.TypeModel;

import java.util.Collection;

public final class DefaultResolver {
    private DefaultResolver() {}

    public static String resolveBuilder(Collection<ImportModel> imports, FieldModel field, String modelClassName, String targetPackage, boolean clear) {
        if (clear) return "%s.clear()".formatted(field.name());
        else return "%s.of(this%s)".formatted(
                field.isListBuilder() ? "ListBuilder" : "MapBuilder",
                field.hasDefault()
                        ? ", " + DefaultResolver.resolve(imports, field, modelClassName, targetPackage)
                        : ""
        );
    }

    public static String resolve(Collection<ImportModel> imports, FieldModel field, String modelClassName, String targetPackage) {
        if (field.defaultValueExpr() != null && !field.defaultValueExpr().isBlank()) return field.defaultValueExpr();
        if (field.defaultMethod() != null && !field.defaultMethod().isBlank()) return importMethod(imports, modelClassName, targetPackage, field.defaultMethod());

        TypeModel.Kind kind = field.type().kind();

        return switch (kind) {
            case BOOLEAN -> "false";
            case NUM -> "0";
            case LONG -> "0L";
            case FLOAT -> "0.0f";
            case DOUBLE -> "0.0d";
            case CHAR -> "'\\0'";
            case OTHER -> "null";
        };
    }

    private static String importMethod(Collection<ImportModel> imports, String modelClassName, String targetPackage, String method) {
        String ownerClass;
        String methodName;

        int dot = method.lastIndexOf('.');
        if (dot == -1) {
            ownerClass = modelClassName;
            methodName = method;
        } else {
            ownerClass = method.substring(0, dot);
            methodName = method.substring(dot + 1);
        }

        if (ownerClass.contains(".")) {
            String fqcn = ownerClass;
            String simpleName = fqcn.substring(fqcn.lastIndexOf('.') + 1);
            String ownerPackage = fqcn.substring(0, fqcn.lastIndexOf('.'));

            if (!ownerPackage.equals(targetPackage) && !ownerPackage.equals("java.lang")) {
                imports.add(new ImportModel(fqcn));
            }

            ownerClass = simpleName;
        }

        return ownerClass + "." + methodName + "()";
    }
}
