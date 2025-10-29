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

/** Unit tests for {@link TestSuiteUploader} class. */
@ExtendWith(MockitoExtension.class)
class TestSuiteUploaderTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";

    @TempDir File tempDir;
    private String validApkPath;

    @BeforeEach
    void setUp() throws IOException {
        // Create a dummy APK file for testing
        File dummyApk = new File(tempDir, "test-suite.apk");
        if (!dummyApk.createNewFile()) {
            throw new IOException("Failed to create test suite APK file");
        }
        validApkPath = dummyApk.getAbsolutePath();
    }

    @Test
    void constructor_ShouldValidateRequiredParameters() {
        // Valid construction
        TestSuiteUploader validUploader =
                new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, validApkPath);
        assertThat(validUploader).isNotNull();

        // Null parameter validation
        assertThatThrownBy(() -> new TestSuiteUploader(null, TEST_ACCESS_KEY, validApkPath))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null");

        assertThatThrownBy(() -> new TestSuiteUploader(TEST_USERNAME, null, validApkPath))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Access Key cannot be null");

        assertThatThrownBy(() -> new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Test Suite File Path cannot be null");

        assertThatThrownBy(
                        () -> new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, "invalid.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Test suite file must have a .apk extension");
    }
}
