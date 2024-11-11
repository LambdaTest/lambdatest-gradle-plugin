package io.github.lambdatest.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The main plugin class that integrates LambdaTest functionality into the Gradle build system.
 * This plugin adds the 'runLambdaTest' task to the project's task container.
 */
public class LambdaTestPlugin implements Plugin<Project> {

    /**
     * Applies the plugin to the specified Gradle project, registering the LambdaTest task.
     *
     * @param project The Gradle project to which this plugin is being applied
     */
    @Override
    public void apply(Project project) {
        // Register a new task named "runLambdaTest" and associate it with the LambdaTestTask class
        project.getTasks().create("runLambdaTest", LambdaTestTask.class);
    }
}
