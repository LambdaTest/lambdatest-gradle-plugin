package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LambdaUploaderTask} class. */
@ExtendWith(MockitoExtension.class)
class LambdaUploaderTaskTest {

    private static final String TEST_USERNAME = TestConfig.getUsername();
    private static final String TEST_ACCESS_KEY = TestConfig.getAccessKey();
    private static final String TEST_APP_FILE_PATH = TestConfig.getAppFilePath();
    private static final String TEST_SUITE_FILE_PATH = TestConfig.getTestSuiteFilePath();
    private static final String TEST_APP_ID = TestConfig.getAppId();
    private static final String TEST_SUITE_ID = TestConfig.getTestSuiteId();

    private Project project;
    private LambdaUploaderTask task;

    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder().build();
        task = project.getTasks().create("testUploadApk", LambdaUploaderTask.class);
    }

    @Test
    void task_ShouldBeCreated_WithCorrectType() {
        // Then
        assertThat(task).isNotNull();
        assertThat(task).isInstanceOf(LambdaUploaderTask.class);
    }

    @Test
    void setUsername_ShouldSetUsername() {
        // When
        task.setUsername(TEST_USERNAME);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setAccessKey_ShouldSetAccessKey() {
        // When
        task.setAccessKey(TEST_ACCESS_KEY);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setAppFilePath_ShouldSetAppFilePath() {
        // When
        task.setAppFilePath(TEST_APP_FILE_PATH);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setTestSuiteFilePath_ShouldSetTestSuiteFilePath() {
        // When
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void uploadApkToLambdaTest_ShouldUploadApp_WhenAppFilePathIsSet() throws IOException {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenReturn(TEST_APP_ID);

            // When
            task.uploadApkToLambdaTest();

            // Then
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH));
        }
    }

    @Test
    void uploadApkToLambdaTest_ShouldUploadTestSuite_WhenTestSuiteFilePathIsSet() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                    .thenReturn(TEST_SUITE_ID);

            // When
            task.uploadApkToLambdaTest();

            // Then
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH));
        }
    }

    @Test
    void uploadApkToLambdaTest_ShouldUploadBoth_WhenBothPathsAreSet() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

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

            // When
            task.uploadApkToLambdaTest();

            // Then
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH));
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH));
        }
    }

    @Test
    void uploadApkToLambdaTest_ShouldNotUpload_WhenNoPathsAreSet() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            // When
            task.uploadApkToLambdaTest();

            // Then
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH),
                    never());
        }
    }

    @Test
    void uploadApkToLambdaTest_ShouldHandleUploadFailure() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenThrow(new IOException("Upload failed"));

            // When/Then
            assertThatThrownBy(() -> task.uploadApkToLambdaTest())
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void uploadApkToLambdaTest_ShouldContinueWithTestSuite_WhenAppUploadSucceeds() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

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

            // When
            task.uploadApkToLambdaTest();

            // Then - Both should have been called
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH));
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH));
        }
    }

    @Test
    void uploadApkToLambdaTest_ShouldUploadAsynchronously() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenReturn(TEST_APP_ID, TEST_SUITE_ID);

            // When
            task.uploadApkToLambdaTest();

            // Then - Both uploads should complete
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH));
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH));
        }
    }
}
