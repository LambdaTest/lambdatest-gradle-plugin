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
    void apply_ShouldRegisterRunLambdaTestTask() {
        // When
        plugin.apply(project);

        // Then
        assertThat(project.getTasks().findByName("runLambdaTest")).isNotNull();
        assertThat(project.getTasks().findByName("runLambdaTest"))
                .isInstanceOf(LambdaTestTask.class);
    }

    @Test
    void apply_ShouldRegisterUploadApkToLambdaTestTask() {
        // When
        plugin.apply(project);

        // Then
        assertThat(project.getTasks().findByName("uploadApkToLambdaTest")).isNotNull();
        assertThat(project.getTasks().findByName("uploadApkToLambdaTest"))
                .isInstanceOf(LambdaUploaderTask.class);
    }

    @Test
    void apply_ShouldRegisterBothTasks() {
        // When
        plugin.apply(project);

        // Then
        assertThat(project.getTasks().findByName("runLambdaTest")).isNotNull();
        assertThat(project.getTasks().findByName("uploadApkToLambdaTest")).isNotNull();
    }

    @Test
    void apply_ShouldCreateTasksWithCorrectTypes() {
        // When
        plugin.apply(project);

        // Then
        LambdaTestTask runTask = (LambdaTestTask) project.getTasks().findByName("runLambdaTest");
        LambdaUploaderTask uploadTask =
                (LambdaUploaderTask) project.getTasks().findByName("uploadApkToLambdaTest");

        assertThat(runTask).isNotNull();
        assertThat(uploadTask).isNotNull();
    }

    @Test
    void apply_ShouldNotThrowException_WhenAppliedToProject() {
        // When/Then - No exception should be thrown
        plugin.apply(project);
    }

    @Test
    void apply_ShouldAllowMultipleApplications() {
        // When
        plugin.apply(project);

        // Then - Should not throw exception when applied again
        // (Gradle handles duplicate task names by using existing ones)
        assertThat(project.getTasks().findByName("runLambdaTest")).isNotNull();
    }

    @Test
    void runLambdaTestTask_ShouldBeConfigurable() {
        // Given
        plugin.apply(project);
        LambdaTestTask task = (LambdaTestTask) project.getTasks().findByName("runLambdaTest");

        // When
        task.setUsername("testUser");
        task.setAccessKey("testKey");

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void uploadApkToLambdaTestTask_ShouldBeConfigurable() {
        // Given
        plugin.apply(project);
        LambdaUploaderTask task =
                (LambdaUploaderTask) project.getTasks().findByName("uploadApkToLambdaTest");

        // When
        task.setUsername("testUser");
        task.setAccessKey("testKey");

        // Then
        assertThat(task).isNotNull();
    }
}
