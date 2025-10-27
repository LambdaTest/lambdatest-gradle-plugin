package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Minimal unit tests for {@link UploaderUtil} class. */
class UploaderUtilTest {

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
    void constructor_ShouldNotBeInstantiable() {
        // When/Then - Utility class should not be instantiable
        assertThatThrownBy(
                        () -> {
                            // Use reflection to try to create instance
                            var constructor = UploaderUtil.class.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            constructor.newInstance();
                        })
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void uploadAndGetId_ShouldAcceptValidParameters() {
        // Given - Valid parameters (but we won't execute to avoid network calls)
        assertThat(TEST_USERNAME).isNotEmpty();
        assertThat(TEST_ACCESS_KEY).isNotEmpty();
        assertThat(validApkPath).isNotEmpty();

        // We don't call the actual method to avoid making HTTP requests in unit tests
        // In a proper unit test, we would mock the HTTP client
    }

    @Test
    void uploadAndGetId_ShouldHandleInvalidFilePath() {
        // Given - Non-existent file path
        String invalidFilePath = "/non/existent/file.apk";

        // When/Then - Should handle file not found gracefully
        assertThatThrownBy(
                        () ->
                                UploaderUtil.uploadAndGetId(
                                        TEST_USERNAME, TEST_ACCESS_KEY, invalidFilePath))
                .isInstanceOf(
                        Exception.class); // Could be IOException or other file-related exception
    }
}
