package org.lilbrocodes.constructive;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSetContainer;
import org.jetbrains.annotations.NotNull;
import org.lilbrocodes.constructive.tasks.ConstructiveGenerateTask;

public class ConstructiveGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        var task = project.getTasks().register(
                "generateConstructiveBuilders",
                ConstructiveGenerateTask.class,
                t -> {
                    t.getSourceDir().set(project.getLayout().getProjectDirectory().dir("src/main/java"));
                    t.getOutputDir().set(project.getLayout().getBuildDirectory().dir("generated/constructive"));
                }
        );

        project.getPlugins().withType(JavaPlugin.class, plugin -> {
            project.getExtensions()
                    .getByType(SourceSetContainer.class)
                    .getByName("main")
                    .getJava()
                    .srcDir(task.flatMap(ConstructiveGenerateTask::getOutputDir));
        });
    }
}
