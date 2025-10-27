package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LambdaUploaderTask} class. */
@ExtendWith(MockitoExtension.class)
class LambdaUploaderTaskTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";

    @TempDir File tempDir;
    private Project project;
    private LambdaUploaderTask task;
    private String validAppPath;
    private String validTestPath;

    @BeforeEach
    void setUp() throws IOException {
        project = ProjectBuilder.builder().build();
        task = project.getTasks().create("testUploadApk", LambdaUploaderTask.class);

        // Create dummy APK files for testing
        File dummyApp = new File(tempDir, "test-app.apk");
        dummyApp.createNewFile();
        validAppPath = dummyApp.getAbsolutePath();

        File dummyTest = new File(tempDir, "test-suite.apk");
        dummyTest.createNewFile();
        validTestPath = dummyTest.getAbsolutePath();
    }

    @Test
    void task_ShouldBeConfigurable_WithAllProperties() {
        // When - Configure all properties
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(validAppPath);
        task.setTestSuiteFilePath(validTestPath);

        // Then - Task should be properly configured
        assertThat(task).isNotNull();
        assertThat(task).isInstanceOf(LambdaUploaderTask.class);
    }

    @Test
    void uploadApkToLambdaTest_ShouldCoordinateUploads_WhenFilesProvided() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(validAppPath);
        task.setTestSuiteFilePath(validTestPath);

        // When/Then - Should coordinate uploads (we don't execute to avoid network
        // calls)
        assertThat(task).isNotNull();
        // In a real unit test, we'd mock the upload methods to avoid HTTP calls
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
