package org.lilbrocodes.constructive.processor;

import com.google.auto.service.AutoService;
import org.lilbrocodes.constructive.api.v1.anno.Constructive;
import org.lilbrocodes.constructive.internal.builder.generator.BuilderGenerator;
import org.lilbrocodes.constructive.internal.builder.input.ElementModelExtractor;
import org.lilbrocodes.constructive.internal.builder.model.ConstructiveClass;
import org.lilbrocodes.constructive.internal.common.FilerOutputTarget;
import org.lilbrocodes.constructive.internal.common.OutputTarget;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("org.lilbrocodes.constructive.api.v1.anno.Constructive")
public class ConstructiveBuilderProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedClass : roundEnv.getElementsAnnotatedWith(Constructive.class)) {
            if (annotatedClass.getKind() != ElementKind.CLASS || !(annotatedClass instanceof TypeElement typeElement))
                continue;

            ConstructiveClass model = ElementModelExtractor.model(typeElement, roundEnv, processingEnv);
            OutputTarget target = new FilerOutputTarget(processingEnv.getFiler());

            try {
                BuilderGenerator.generate(model, target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
