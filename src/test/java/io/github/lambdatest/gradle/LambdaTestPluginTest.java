package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link LambdaTestPlugin} class. */
class LambdaTestPluginTest {

    private Project project;
    private LambdaTestPlugin plugin;

    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder().build();
        plugin = new LambdaTestPlugin();
    }

    @Test
    void apply_ShouldRegisterBothRequiredTasks() {
        // When
        plugin.apply(project);

        // Then
        assertThat(project.getTasks().findByName("runLambdaTest"))
                .isNotNull()
                .isInstanceOf(LambdaTestTask.class);

        assertThat(project.getTasks().findByName("uploadApkToLambdaTest"))
                .isNotNull()
                .isInstanceOf(LambdaUploaderTask.class);
    }

    @Test
    void apply_ShouldAllowTaskConfiguration() {
        // Given
        plugin.apply(project);

        // When
        LambdaTestTask runTask = (LambdaTestTask) project.getTasks().findByName("runLambdaTest");
        LambdaUploaderTask uploadTask = (LambdaUploaderTask) project.getTasks().findByName("uploadApkToLambdaTest");

        runTask.setUsername("testUser");
        uploadTask.setAccessKey("testKey");

        // Then - Tasks should be configurable without exceptions
        assertThat(runTask).isNotNull();
        assertThat(uploadTask).isNotNull();
    }
}
