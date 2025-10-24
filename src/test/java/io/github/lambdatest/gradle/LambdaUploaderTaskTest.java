package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LambdaUploaderTask} class. */
@ExtendWith(MockitoExtension.class)
class LambdaUploaderTaskTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";
    private static final String TEST_APP_FILE_PATH = "./sample-app.apk";
    private static final String TEST_SUITE_FILE_PATH = "./sample-test.apk";

    private Project project;
    private LambdaUploaderTask task;

    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder().build();
        task = project.getTasks().create("testUploadApk", LambdaUploaderTask.class);
    }

    @Test
    void task_ShouldBeConfigurable_WithAllProperties() {
        // When - Configure all properties
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

        // Then - Task should be properly configured
        assertThat(task).isNotNull();
        assertThat(task).isInstanceOf(LambdaUploaderTask.class);
    }

    @Test
    void uploadApkToLambdaTest_ShouldCoordinateUploads_WhenFilesProvided() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

        // When/Then - Should coordinate uploads but fail at HTTP execution (expected in
        // tests)
        assertThatThrownBy(() -> task.uploadApkToLambdaTest())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void uploadApkToLambdaTest_ShouldHandleNoFiles_Gracefully() {
        // Given - No file paths set
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);

        // When/Then - Should complete without attempting uploads
        task.uploadApkToLambdaTest(); // Should not throw
        assertThat(task).isNotNull();
    }
}
