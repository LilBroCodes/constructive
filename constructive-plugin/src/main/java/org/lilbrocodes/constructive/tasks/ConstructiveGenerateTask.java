package org.lilbrocodes.constructive.tasks;

import com.sun.source.util.JavacTask;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.lilbrocodes.constructive.api.v1.anno.Constructive;
import org.lilbrocodes.constructive.internal.builder.generator.BuilderGenerator;
import org.lilbrocodes.constructive.internal.builder.input.ElementModelExtractor;
import org.lilbrocodes.constructive.internal.builder.model.ConstructiveClass;
import org.lilbrocodes.constructive.internal.common.FileOutputTarget;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ConstructiveGenerateTask extends DefaultTask {
    @InputDirectory
    public abstract DirectoryProperty getSourceDir();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    public void generate() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("No system Java compiler found. Make sure you are using a JDK, not a JRE.");
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<java.io.File> sourceFiles = new ArrayList<>(getProject().fileTree(getSourceDir()).getFiles());
        Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjectsFromFiles(sourceFiles);
        List<ConstructiveClass> models = new ArrayList<>();

        JavacTask task = (JavacTask) compiler.getTask(
                null,
                fileManager,
                null,
                List.of(),
                null,
                sources
        );

        task.setProcessors(List.of(new AbstractProcessor() {
            @Override
            public Set<String> getSupportedAnnotationTypes() {
                return Set.of("org.lilbrocodes.constructive.api.v1.anno.Constructive");
            }

            @Override
            public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
                ProcessingEnvironment env = processingEnv;

                for (Element e : roundEnv.getElementsAnnotatedWith(Constructive.class)) {
                    if (e instanceof TypeElement type) {
                        ConstructiveClass model = ElementModelExtractor.model(type, roundEnv, env);
                        models.add(model);
                    }
                }
                return false;
            }
        }));

        task.call();

        for (ConstructiveClass model : models) {
            BuilderGenerator.generate(
                    model,
                    new FileOutputTarget(getOutputDir().get().getAsFile())
            );
        }
    }
}
