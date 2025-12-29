package org.lilbrocodes.constructive.internal.builder.generator;

import org.lilbrocodes.constructive.internal.builder.model.ConstructiveClass;
import org.lilbrocodes.constructive.internal.builder.model.FieldModel;
import org.lilbrocodes.constructive.internal.builder.model.ImportModel;
import org.lilbrocodes.constructive.internal.builder.model.TypeModel;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.lilbrocodes.constructive.internal.builder.input.ElementModelExtractor.getPackageName;

public class ImportEmitter {
    public static String resolveType(Collection<ImportModel> imports, ConstructiveClass model, TypeModel type, boolean includeGenerics) {
        if (type instanceof TypeModel.Primitive primitive) {
            return primitive.name();
        }

        if (!(type instanceof TypeModel.Declared declared)) {
            if (type == null) return "SEX";
            return type.kind().name().toLowerCase(Locale.ROOT);
        }

        if (declared.qualified().startsWith("java.lang.")) {
            return declared.simple();
        }

        if (model.builderPackage().equals(model.packageName())) {
            return declared.simple();
        }

        imports.add(new ImportModel(declared.qualified()));

        if (!declared.generics().isEmpty() && includeGenerics) {
            String generics = declared.generics().stream().
                    map(arg -> resolveType(imports, model, arg))
                    .collect(Collectors.joining(", "));
            return declared.simple() + "<" + generics + ">";
        }

        return declared.simple();
    }

    public static String resolveType(Collection<ImportModel> imports, ConstructiveClass model, TypeModel type) {
        return resolveType(imports, model, type, true);
    }

    public static String resolveField(Set<ImportModel> imports, ConstructiveClass model, FieldModel field, boolean includeGenerics) {
        TypeModel type = field.type();

        if (field.builder()) {
            if (!(type instanceof TypeModel.Declared declared)) {
                throw new IllegalArgumentException("@Builder fields must be declared types");
            }

            if (field.isListBuilder()) {
                imports.add(new ImportModel("org.lilbrocodes.constructive.api.v1.utils.ListBuilder"));
                imports.add(new ImportModel("java.util.List"));

                TypeModel valueType = declared.generics().get(0);
                String value = resolveType(imports, model, valueType);

                return "ListBuilder<%s, %s>".formatted(model.builderName(), value);
            }

            if (field.isMapBuilder()) {
                imports.add(new ImportModel("org.lilbrocodes.constructive.api.v1.utils.MapBuilder"));
                imports.add(new ImportModel("java.util.Map"));

                TypeModel keyType = declared.generics().get(0);
                TypeModel valueType = declared.generics().get(1);

                String key = resolveType(imports, model, keyType);
                String value = resolveType(imports, model, valueType);

                return "MapBuilder<%s, %s, %s>".formatted(model.builderName(), key, value);
            }

            throw new IllegalArgumentException("@Builder is only supported for List and Map fields");
        }

        if (field.composite()) {
            return resolveType(imports, model,  field.builderType(), includeGenerics);
        }

        if (type instanceof TypeModel.Primitive primitive) {
            return primitive.name();
        }

        if (type instanceof TypeModel.Array array) {
            String component = resolveType(imports, model, array.component());
            return component + "[]";
        }

        return resolveType(imports, model, type, includeGenerics);
    }

    public static String resolveField(Set<ImportModel> imports, ConstructiveClass model, FieldModel field) {
        return resolveField(imports, model, field, true);
    }

    public static String resolveBuilder(Collection<ImportModel> imports, ConstructiveClass model) {
        if (!model.builderPackage().equals(model.packageName())) imports.add(new ImportModel(model.packageName() + "." + model.className()));
        return model.className();
    }

    public static void inject(StringBuilder sb, Set<ImportModel> imports, AtomicInteger importOffset) {
        if (imports.isEmpty()) return;

        int o = 0;

        List<ImportModel> sortedImports = imports.stream().sorted(ImportModel.COMPARATOR).toList();
        for (ImportModel info : sortedImports) {
            String line = info.make();
            sb.insert(importOffset.get(), line);
            o += line.length();
        }
        if (!sortedImports.isEmpty()) sb.insert(importOffset.addAndGet(o), "\n");
    }
}
