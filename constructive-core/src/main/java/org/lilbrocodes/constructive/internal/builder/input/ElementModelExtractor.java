package org.lilbrocodes.constructive.internal.builder.input;

import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import org.lilbrocodes.constructive.api.v1.anno.Constructive;
import org.lilbrocodes.constructive.api.v1.anno.builder.*;
import org.lilbrocodes.constructive.internal.builder.model.ConstructiveClass;
import org.lilbrocodes.constructive.internal.builder.model.FieldModel;
import org.lilbrocodes.constructive.internal.builder.model.TypeModel;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.logging.Logger;

public class ElementModelExtractor {
    private static final Logger logger = Logger.getLogger("Constructive");

    public static ConstructiveClass model(TypeElement clazz, RoundEnvironment rEnv, ProcessingEnvironment env) {
        String packageName = getPackageName(clazz);
        Target targetAnno = clazz.getAnnotation(Target.class);
        String name = (targetAnno != null && !targetAnno.builder().isBlank())
                ? targetAnno.builder()
                : clazz.getSimpleName() + "Builder";
        final String pkg = targetAnno == null || targetAnno.builderPackage() == null || targetAnno.builderPackage().isBlank() ? packageName : resolve(packageName, targetAnno.builderPackage(), targetAnno.rel());

        return new ConstructiveClass(
                packageName,
                getQualifiedClassName(clazz),
                name,
                pkg,
                extractFields(clazz, env, name, pkg),
                rEnv.getElementsAnnotatedWith(Constructive.class)
                        .stream()
                        .anyMatch(e -> searchFieldsForComposite(e, clazz.asType(), env.getTypeUtils()))
        );
    }

    public static boolean searchFieldsForComposite(Element element, TypeMirror targetType, Types typeUtils) {
        if (element.getKind().isField()) {
            VariableElement var = (VariableElement) element;
            TypeMirror fieldType = var.asType();

            if (typeUtils.isSameType(fieldType, targetType) && var.getAnnotation(Composite.class) != null) {
                return true;
            }
        }

        return element.getEnclosedElements().stream()
                .anyMatch(e -> searchFieldsForComposite(e, targetType, typeUtils));
    }

    public static List<FieldModel> extractFields(TypeElement typeElement, ProcessingEnvironment processingEnv, String builderName, String builderPkg) {
        List<FieldModel> fields = new ArrayList<>();

        Trees trees = Trees.instance(processingEnv);

        for (Element enclosed : typeElement.getEnclosedElements()) {
            if (!(enclosed instanceof VariableElement variable)) continue;

            String name = variable.getSimpleName().toString();
            TypeMirror type = variable.asType();

            boolean hardRequire = variable.getAnnotation(HardRequire.class) != null;
            HardRequire hrAnno = variable.getAnnotation(HardRequire.class);

            boolean required = true; // default
            Required requiredAnno = variable.getAnnotation(Required.class);
            if (requiredAnno != null) {
                required = requiredAnno.required();
            }

            boolean transientField = variable.getAnnotation(Transient.class) != null;
            boolean isBuilder = variable.getAnnotation(Builder.class) != null;
            boolean composite = variable.getAnnotation(Composite.class) != null;
            TypeModel.Declared builderType = null;
            Map<String, TypeModel> hardRequired = new HashMap<>();

            if (composite) {
                if (!(type instanceof DeclaredType declared)) {
                    throw new IllegalArgumentException("@Composite only supports declared types");
                }

                TypeElement fieldType = (TypeElement) declared.asElement();

                if (fieldType.getAnnotation(Constructive.class) == null) {
                    throw new IllegalArgumentException("@Composite field type must also be @Constructive");
                }

                fieldType.getEnclosedElements().forEach(element -> {
                    if (element.getKind().isField() && element.getAnnotation(HardRequire.class) != null) {
                        hardRequired.put(element.getSimpleName().toString(), model(element.asType()));
                    }
                });

                Target target = fieldType.getAnnotation(Target.class);

                String fieldBuilderName = (target != null && target.builder() != null && !target.builder().isBlank())
                        ? target.builder()
                        : fieldType.getSimpleName() + "Builder";

                String fieldBuilderPkg = (target != null && target.builderPackage() != null && !target.builderPackage().isBlank())
                        ? target.builderPackage()
                        : getPackageName(fieldType);

                String fqcn = fieldBuilderPkg + "." + fieldBuilderName;

                TypeModel enclosingBuilderType = new TypeModel.Declared(
                        builderPkg + "." + builderName,
                        builderName,
                        List.of(),
                        TypeModel.Kind.OTHER
                );

                builderType = new TypeModel.Declared(
                        fqcn,
                        fieldBuilderName,
                        List.of(enclosingBuilderType),
                        TypeModel.Kind.OTHER
                );
            }

            Default defaultAnno = variable.getAnnotation(Default.class);
            String defaultMethod = (defaultAnno != null) ? defaultAnno.method().trim() : "";

            Description descriptionAnno = variable.getAnnotation(Description.class);
            String description = (descriptionAnno != null) ? descriptionAnno.value() : null;

            String defaultValueExpr = null;
            try {
                TreePath path = trees.getPath(variable);
                if (path != null) {
                    VariableTree varTree = (VariableTree) path.getLeaf();
                    if (varTree.getInitializer() != null) {
                        defaultValueExpr = varTree.getInitializer().toString();
                    }
                }
            } catch (Exception e) {
                logger.warning("Your compiler doesn't support trees! Any field that is using a value initializer as it's default value will have it set to null! Error: " + e);
            }

            fields.add(new FieldModel(
                    name,
                    model(type),
                    required,
                    hardRequire && (hrAnno == null || hrAnno.require()),
                    isBuilder,
                    transientField,
                    composite,
                    description,
                    defaultMethod,
                    defaultValueExpr,
                    builderType,
                    hardRequired
            ));
        }

        return fields;
    }

    public static TypeModel model(TypeMirror type) {
        return model(type, false);
    }

    public static TypeModel model(TypeMirror type, boolean bArray) {
        TypeModel typeModel;
        TypeKind kind = type.getKind();
        if (kind.isPrimitive()) typeModel = new TypeModel.Primitive(type.getKind().name().toLowerCase(Locale.ROOT), TypeModel.Kind.map(kind));
        else if (kind == TypeKind.ARRAY && !bArray) {
            TypeMirror aType = ((ArrayType) type).getComponentType();
            return new TypeModel.Array(model(aType, true), TypeModel.Kind.map(aType.getKind()));
        }
        else if (type instanceof DeclaredType declared) {
            TypeElement typeElement = (TypeElement) declared.asElement();

            String pkg = getPackageName(typeElement);
            String nestedName = getQualifiedClassName(typeElement);

            typeModel = new TypeModel.Declared(
                    pkg.isEmpty() ? nestedName : pkg + "." + nestedName,
                    nestedName,
                    declared.getTypeArguments().stream().map(ElementModelExtractor::model).toList(),
                    TypeModel.Kind.map(declared.getKind())
            );

        }
        else {
            if (bArray) throw new IllegalStateException("Component type of annotated array field is neither a primitive or declared type.");
            else throw new IllegalStateException("Annotated field is neither a primitive, array or declared type.");
        }

        return typeModel;
    }

    private static String getQualifiedClassName(TypeElement type) {
        StringBuilder sb = new StringBuilder(type.getSimpleName());
        Element enclosing = type.getEnclosingElement();

        while (enclosing instanceof TypeElement enclosingType) {
            sb.insert(0, enclosingType.getSimpleName() + ".");
            enclosing = enclosingType.getEnclosingElement();
        }

        return sb.toString();
    }

    public static String getPackageName(Element element) {
        Element current = element;
        while (current != null && !(current instanceof PackageElement)) {
            current = current.getEnclosingElement();
        }
        if (current instanceof PackageElement pkg) {
            String pkgName = pkg.getQualifiedName().toString();
            return pkgName != null ? pkgName : "";
        }
        return "";
    }

    public static String resolve(String current, String target, boolean rel) {
        if (!rel) return target;

        if (!target.contains("--")) {
            return current.isEmpty() ? target : current + "." + target;
        }

        String[] currentParts = current.isEmpty()
                ? new String[0]
                : current.split("\\.");

        String[] targetParts = target.split("\\.");

        int upCount = 0;
        int index = 0;

        while (index < targetParts.length && targetParts[index].equals("--")) {
            upCount++;
            index++;
        }

        if (upCount > currentParts.length) {
            throw new IllegalArgumentException(
                    "Cannot resolve package '" + target + "' from '" + current + "'"
            );
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < currentParts.length - upCount; i++) {
            if (!result.isEmpty()) result.append(".");
            result.append(currentParts[i]);
        }

        for (; index < targetParts.length; index++) {
            if (targetParts[index].isEmpty()) continue;
            if (!result.isEmpty()) result.append(".");
            result.append(targetParts[index]);
        }

        return result.toString();
    }
}
