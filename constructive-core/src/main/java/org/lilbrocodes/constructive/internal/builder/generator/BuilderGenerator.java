package org.lilbrocodes.constructive.internal.builder.generator;

import org.jetbrains.annotations.ApiStatus;
import org.lilbrocodes.constructive.internal.builder.model.ConstructiveClass;
import org.lilbrocodes.constructive.internal.builder.model.ImportModel;
import org.lilbrocodes.constructive.internal.common.OutputTarget;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lilbrocodes.constructive.internal.builder.generator.BuilderEmitter.*;

@ApiStatus.Internal
public final class BuilderGenerator {
    public static void generate(ConstructiveClass model, OutputTarget out) throws IOException {
        Set<ImportModel> imports = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        String builtType = ImportEmitter.resolveBuilder(
                imports,
                model
        );

        AtomicInteger importOffset = new AtomicInteger();
        emitHeader(sb, importOffset, model, builtType);
        emitFields(sb, imports, model);
        emitConstructor(sb, model);
        emitCreateMethod(sb, imports, model);
        emitSetters(sb, imports, model);
        emitBuildMethod(sb, imports, model);
        emitResetMethod(sb, imports, model);
        emitEndMethod(sb, model);

        ImportEmitter.inject(sb, imports, importOffset);

        out.write(model.builderPackage(), model.builderName(), sb.toString());
    }
}
