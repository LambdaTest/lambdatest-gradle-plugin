package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link AppUploader} class. */
@ExtendWith(MockitoExtension.class)
class AppUploaderTest {

        private static final String TEST_USERNAME = "testuser";
        private static final String TEST_ACCESS_KEY = "test_access_key";
        private static final String TEST_APP_FILE_PATH = "./sample-app.apk";

        @Test
        void constructor_ShouldValidateRequiredParameters() {
                // Valid construction
                AppUploader validUploader = new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH);
                assertThat(validUploader).isNotNull();

                // Null parameter validation
                assertThatThrownBy(() -> new AppUploader(null, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Username cannot be null");

                assertThatThrownBy(() -> new AppUploader(TEST_USERNAME, null, TEST_APP_FILE_PATH))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Access Key cannot be null");

                assertThatThrownBy(() -> new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, null))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("App File Path cannot be null");

                assertThatThrownBy(() -> new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, "invalid.txt"))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("App file must have a .apk extension");
        }

        @Test
        void uploadAppAsync_ShouldReturnCompletableFuture() {
                // Given
                AppUploader appUploader = new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH);

                // When
                CompletableFuture<String> future = appUploader.uploadAppAsync();

                // Then - Future should be created (actual execution will fail due to invalid
                // credentials/missing files)
                assertThat(future).isNotNull();
                assertThat(future).isInstanceOf(CompletableFuture.class);
        }

        @Test
        void uploadAppAsync_ShouldHandleUploadFailure() {
                // Given - Using invalid credentials will cause upload to fail
                String invalidUsername = "invalid_user";
                String invalidAccessKey = "invalid_key";

                AppUploader appUploader = new AppUploader(invalidUsername, invalidAccessKey, TEST_APP_FILE_PATH);
                CompletableFuture<String> future = appUploader.uploadAppAsync();

                // When/Then - Should handle failure gracefully by throwing CompletionException
                assertThatThrownBy(future::join)
                                .isInstanceOf(CompletionException.class)
                                .hasCauseInstanceOf(RuntimeException.class);
        }
}
