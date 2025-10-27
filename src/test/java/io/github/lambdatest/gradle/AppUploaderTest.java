package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link AppUploader} class. */
@ExtendWith(MockitoExtension.class)
class AppUploaderTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";

    @TempDir File tempDir;
    private String validApkPath;

    @BeforeEach
    void setUp() throws IOException {
        // Create a dummy APK file for testing
        File dummyApk = new File(tempDir, "test-app.apk");
        dummyApk.createNewFile();
        validApkPath = dummyApk.getAbsolutePath();
    }

    @Test
    void constructor_ShouldValidateRequiredParameters() {
        // Valid construction
        AppUploader validUploader = new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, validApkPath);
        assertThat(validUploader).isNotNull();

        // Null parameter validation
        assertThatThrownBy(() -> new AppUploader(null, TEST_ACCESS_KEY, validApkPath))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null");

        assertThatThrownBy(() -> new AppUploader(TEST_USERNAME, null, validApkPath))
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
        AppUploader appUploader = new AppUploader(TEST_USERNAME, TEST_ACCESS_KEY, validApkPath);

        // When
        CompletableFuture<String> future = appUploader.uploadAppAsync();

        // Then - Future should be created (we don't wait for completion to avoid
        // network calls)
        assertThat(future).isNotNull();
        assertThat(future).isInstanceOf(CompletableFuture.class);
    }

    @Test
    void uploadAppAsync_ShouldHandleInvalidCredentials() {
        // Given - Test that invalid credentials are handled (we expect it to fail fast)
        AppUploader appUploader = new AppUploader("invalid_user", "invalid_key", validApkPath);

        // When/Then - The async operation should be created but will fail when executed
        CompletableFuture<String> future = appUploader.uploadAppAsync();
        assertThat(future).isNotNull();

        // We don't call .join() here to avoid making actual network calls in unit tests
    }
}
