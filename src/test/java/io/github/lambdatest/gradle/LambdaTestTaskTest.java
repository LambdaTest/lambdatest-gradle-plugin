package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LambdaTestTask} class. */
@ExtendWith(MockitoExtension.class)
class LambdaTestTaskTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";
    private static final List<String> TEST_DEVICES = Arrays.asList("Pixel 6-12", "Galaxy S21-11");

    @TempDir File tempDir;
    private Project project;
    private LambdaTestTask task;
    private String validAppPath;
    private String validTestPath;

    @BeforeEach
    void setUp() throws IOException {
        project = ProjectBuilder.builder().build();
        task = project.getTasks().create("testLambdaTest", LambdaTestTask.class);

        // Create dummy APK files for testing
        File dummyApp = new File(tempDir, "test-app.apk");
        if (!dummyApp.createNewFile()) {
            throw new IOException("Failed to create test app APK file");
        }
        validAppPath = dummyApp.getAbsolutePath();

        File dummyTest = new File(tempDir, "test-suite.apk");
        if (!dummyTest.createNewFile()) {
            throw new IOException("Failed to create test suite APK file");
        }
        validTestPath = dummyTest.getAbsolutePath();
    }

    @Test
    void task_ShouldBeConfigurable_WithAllProperties() {
        // When - Configure all basic properties
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(validAppPath);
        task.setTestSuiteFilePath(validTestPath);
        task.setDevice(TEST_DEVICES);
        task.setBuild("Test Build");
        task.setVideo(true);
        task.setIsFlutter(false);

        // Then - Task should be properly configured
        assertThat(task).isNotNull();
        assertThat(task).isInstanceOf(LambdaTestTask.class);
    }
}
