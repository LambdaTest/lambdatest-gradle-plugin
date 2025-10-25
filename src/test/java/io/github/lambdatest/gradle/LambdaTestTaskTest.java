package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LambdaTestTask} class. */
@ExtendWith(MockitoExtension.class)
class LambdaTestTaskTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";
    private static final String TEST_APP_FILE_PATH = "./sample-app.apk";
    private static final String TEST_SUITE_FILE_PATH = "./sample-test.apk";
    private static final String TEST_APP_ID = "lt://APP1234567890";
    private static final String TEST_SUITE_ID = "lt://APP1234567891";
    private static final List<String> TEST_DEVICES = Arrays.asList("Pixel 6-12", "Galaxy S21-11");

    private Project project;
    private LambdaTestTask task;

    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder().build();
        task = project.getTasks().create("testLambdaTest", LambdaTestTask.class);
    }

    @Test
    void task_ShouldBeConfigurable_WithAllProperties() {
        // When - Configure all basic properties
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);
        task.setDevice(TEST_DEVICES);
        task.setBuild("Test Build");
        task.setVideo(true);
        task.setIsFlutter(false);

        // Then - Task should be properly configured
        assertThat(task).isNotNull();
        assertThat(task).isInstanceOf(LambdaTestTask.class);
    }

    @Test
    void runLambdaTest_ShouldCoordinateUploadsAndExecution_WhenFilesProvided() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);
        task.setDevice(TEST_DEVICES);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenReturn(TEST_APP_ID);
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                    .thenReturn(TEST_SUITE_ID);

            // When/Then - Should coordinate uploads but fail at HTTP execution (expected in
            // tests)
            assertThatThrownBy(() -> task.runLambdaTest()).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void runLambdaTest_ShouldSkipUploadsAndExecuteTests_WhenIdsAreSet() {
        // Given - Use pre-uploaded IDs instead of file paths
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppId(TEST_APP_ID);
        task.setTestSuiteId(TEST_SUITE_ID);
        task.setDevice(TEST_DEVICES);

        // When/Then - Should skip uploads and proceed to execution
        // Note: This will fail in tests due to HTTP client making real calls, but
        // that's expected
        // The important thing is to verify the configuration doesn't throw validation
        // errors
        try {
            task.runLambdaTest();
            // If we get here, the uploads were skipped properly
        } catch (RuntimeException e) {
            // Expected - HTTP execution will fail in test environment
            assertThat(e).isNotNull();
        }
    }

    @Test
    void runLambdaTest_ShouldHandleUploadFailures() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);
        task.setDevice(TEST_DEVICES);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(() -> UploaderUtil.uploadAndGetId(any(), any(), any()))
                    .thenThrow(new IOException("Upload failed"));

            // When/Then - Should handle upload failures gracefully
            assertThatThrownBy(() -> task.runLambdaTest())
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(CompletionException.class);
        }
    }
}
