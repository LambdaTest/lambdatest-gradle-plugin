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
}
