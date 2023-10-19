package io.github.lambdatest.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LambdaTestPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // Register a new task named "runLambdaTest" and associate it with the LambdaTestTask class
        project.getTasks().create("runLambdaTest", LambdaTestTask.class);
    }
}
