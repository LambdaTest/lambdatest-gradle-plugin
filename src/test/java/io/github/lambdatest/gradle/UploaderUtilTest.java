package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/** Minimal unit tests for {@link UploaderUtil} class. */
class UploaderUtilTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";
    private static final String TEST_APP_FILE_PATH = "./sample-app.apk";

    @Test
    void constructor_ShouldNotBeInstantiable() {
        // When/Then - Utility class should not be instantiable
        assertThatThrownBy(() -> {
            // Use reflection to try to create instance
            var constructor = UploaderUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void uploadAndGetId_ShouldHandleInvalidCredentials() {
        // Given - Invalid credentials will cause HTTP failure
        String invalidUsername = "invalid_user";
        String invalidAccessKey = "invalid_key";

        // When/Then - Should throw IOException for invalid credentials
        assertThatThrownBy(() -> UploaderUtil.uploadAndGetId(invalidUsername, invalidAccessKey, TEST_APP_FILE_PATH))
                .isInstanceOf(IOException.class);
    }

    @Test
    void uploadAndGetId_ShouldHandleInvalidFilePath() {
        // Given - Non-existent file path
        String invalidFilePath = "/non/existent/file.apk";

        // When/Then - Should handle file not found gracefully
        assertThatThrownBy(() -> UploaderUtil.uploadAndGetId(TEST_USERNAME, TEST_ACCESS_KEY, invalidFilePath))
                .isInstanceOf(Exception.class); // Could be IOException or other file-related exception
    }
}