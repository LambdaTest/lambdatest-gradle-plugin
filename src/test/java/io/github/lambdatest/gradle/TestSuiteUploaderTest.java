package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link TestSuiteUploader} class. */
@ExtendWith(MockitoExtension.class)
class TestSuiteUploaderTest {

    private static final String TEST_USERNAME = TestConfig.getUsername();
    private static final String TEST_ACCESS_KEY = TestConfig.getAccessKey();
    private static final String TEST_SUITE_FILE_PATH = TestConfig.getTestSuiteFilePath();
    private static final String TEST_SUITE_ID = TestConfig.getTestSuiteId();

    @Test
    void constructor_ShouldCreateInstance_WithValidParameters() {
        // When
        TestSuiteUploader testSuiteUploader =
                new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH);

        // Then
        assertThat(testSuiteUploader).isNotNull();
    }

    @Test
    void uploadTestSuiteAsync_ShouldReturnTestSuiteId_WhenUploadSucceeds()
            throws ExecutionException, InterruptedException {
        // Given
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                    .thenReturn(TEST_SUITE_ID);

            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH);

            // When
            CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();
            String testSuiteId = future.get();

            // Then
            assertThat(testSuiteId).matches("lt://APP\\d+");
            assertThat(future).isCompleted();
        }
    }

    @Test
    void uploadTestSuiteAsync_ShouldThrowRuntimeException_WhenUploadFails() {
        // Given
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                    .thenThrow(new IOException("Upload failed"));

            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH);

            // When
            CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();

            // Then
            assertThatThrownBy(future::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(RuntimeException.class)
                    .hasRootCauseInstanceOf(IOException.class);
        }
    }

    @Test
    void uploadTestSuiteAsync_ShouldExecuteAsynchronously() {
        // Given
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                    .thenReturn(TEST_SUITE_ID);

            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH);

            // When
            CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();

            // Then
            assertThat(future).isNotNull();
            assertThat(future).isInstanceOf(CompletableFuture.class);
        }
    }

    @Test
    void uploadTestSuiteAsync_ShouldHandleIOException() {
        String INVALID_SUITE_FILE_PATH = "invalid/path/to/suite.zip";
        // Given
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME,
                                            TEST_ACCESS_KEY,
                                            INVALID_SUITE_FILE_PATH))
                    .thenThrow(new IOException("File not found"));

            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, INVALID_SUITE_FILE_PATH);

            // When
            CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();

            // Then
            assertThatThrownBy(future::join).isInstanceOf(CompletionException.class);
        }
    }

    @Test
    void uploadTestSuiteAsync_ShouldHandleAuthenticationErrors() {
        // Given
        String invalidUsername = "invalid_user_" + System.currentTimeMillis();
        String invalidAccessKey = "invalid_key_" + System.currentTimeMillis();
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            invalidUsername,
                                            invalidAccessKey,
                                            TEST_SUITE_FILE_PATH))
                    .thenThrow(new IOException("Authentication failed"));

            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(invalidUsername, invalidAccessKey, TEST_SUITE_FILE_PATH);

            // When
            CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();

            // Then
            assertThatThrownBy(future::join)
                    .isInstanceOf(CompletionException.class)
                    .hasMessageContaining(
                            "https://manual-api.lambdatest.com/app/uploadFramework");
        }
    }

    @Test
    void uploadTestSuiteAsync_ShouldReturnCompletableFuture() {
        // Given
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                    .thenReturn(TEST_SUITE_ID);

            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH);

            // When
            CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();

            // Then
            assertThat(future).isNotNull();
        }
    }
}
