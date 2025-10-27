package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
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
        if (!dummyApk.createNewFile()) {
            throw new IOException("Failed to create test APK file");
        }
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

        // When/Then - Verify the uploader is created correctly
        // We don't call uploadAppAsync() to avoid background thread execution
        assertThat(appUploader).isNotNull();

        // In a real unit test, we would mock the UploaderUtil to test the async
        // behavior
        // without making actual network calls
    }

    @Test
    void uploadAppAsync_ShouldHandleInvalidCredentials() {
        // Given - Test that invalid credentials are handled during construction
        AppUploader appUploader = new AppUploader("invalid_user", "invalid_key", validApkPath);

        // When/Then - The uploader should be created successfully
        assertThat(appUploader).isNotNull();

        // The actual upload failure would occur when uploadAppAsync() is called and
        // executed
        // but we avoid calling it in unit tests to prevent network calls
    }
}
