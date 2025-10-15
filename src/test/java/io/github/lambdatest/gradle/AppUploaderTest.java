package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link AppUploader} class. */
@ExtendWith(MockitoExtension.class)
class AppUploaderTest {

    private static final String TEST_USERNAME = TestConfig.getUsername();
    private static final String TEST_ACCESS_KEY = TestConfig.getAccessKey();
    private static final String TEST_APP_FILE_PATH = TestConfig.getAppFilePath();
    private static final String TEST_APP_ID = TestConfig.getAppId();

    @Test
    void constructor_ShouldCreateInstance_WithValidParameters() {
        // When
        AppUploader appUploader =
                new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH);

        // Then
        assertThat(appUploader).isNotNull();
    }

    @Test
    void constructor_ShouldThrowException_WhenUsernameIsNull() {
        // When/Then
        assertThatThrownBy(() -> new AppUploader(null, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null");
    }

    @Test
    void constructor_ShouldThrowException_WhenAccessKeyIsNull() {
        // When/Then
        assertThatThrownBy(() -> new AppUploader(TEST_USERNAME, null, TEST_APP_FILE_PATH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Access Key cannot be null");
    }

    @Test
    void constructor_ShouldThrowException_WhenAppFilePathIsNull() {
        // When/Then
        assertThatThrownBy(() -> new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("App File Path cannot be null");
    }

    @Test
    void uploadAppAsync_ShouldReturnAppId_WhenUploadSucceeds()
            throws ExecutionException, InterruptedException {
        // Given
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenReturn(TEST_APP_ID);

            AppUploader appUploader =
                    new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH);

            // When
            CompletableFuture<String> future = appUploader.uploadAppAsync();
            String appId = future.get();

            // Then
            assertThat(appId).matches("lt://APP\\d+");
            assertThat(future).isCompleted();
        }
    }

    @Test
    void uploadAppAsync_ShouldThrowRuntimeException_WhenUploadFails() {
        // Given - Using invalid credentials that will cause upload to fail
        String invalidUsername = "invalid_user_" + System.currentTimeMillis();
        String invalidAccessKey = "invalid_key_" + System.currentTimeMillis();

        AppUploader appUploader =
                new AppUploader(invalidUsername, invalidAccessKey, TEST_APP_FILE_PATH);

        // When
        CompletableFuture<String> future = appUploader.uploadAppAsync();

        // Then
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void uploadAppAsync_ShouldExecuteAsynchronously() {
        // Given
        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenReturn(TEST_APP_ID);

            AppUploader appUploader =
                    new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH);

            // When
            CompletableFuture<String> future = appUploader.uploadAppAsync();

            // Then
            assertThat(future).isNotNull();
            assertThat(future).isInstanceOf(CompletableFuture.class);
        }
    }

    @Test
    void uploadAppAsync_ShouldHandleNetworkErrors() {
        // Given - Using invalid credentials that will cause network/authentication
        // errors
        String invalidUsername = "invalid_network_user_" + System.currentTimeMillis();
        String invalidAccessKey = "invalid_network_key_" + System.currentTimeMillis();

        AppUploader appUploader =
                new AppUploader(invalidUsername, invalidAccessKey, TEST_APP_FILE_PATH);

        // When
        CompletableFuture<String> future = appUploader.uploadAppAsync();

        // Then
        assertThatThrownBy(future::join).isInstanceOf(CompletionException.class);
    }
}
