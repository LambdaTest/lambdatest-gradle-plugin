package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link TestSuiteUploader} class. */
@ExtendWith(MockitoExtension.class)
class TestSuiteUploaderTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";
    private static final String TEST_SUITE_FILE_PATH = "./sample-test.apk";

    @Test
    void constructor_ShouldValidateRequiredParameters() {
        // Valid construction
        TestSuiteUploader validUploader =
                new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH);
        assertThat(validUploader).isNotNull();

        // Null parameter validation
        assertThatThrownBy(() -> new TestSuiteUploader(null, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null");

        assertThatThrownBy(() -> new TestSuiteUploader(TEST_USERNAME, null, TEST_SUITE_FILE_PATH))
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

    @Test
    void uploadTestSuiteAsync_ShouldReturnCompletableFuture() {
        // Given
        TestSuiteUploader testSuiteUploader =
                new TestSuiteUploader(TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH);

        // When
        CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();

        // Then - Future should be created (actual execution will fail due to invalid
        // credentials/missing files)
        assertThat(future).isNotNull();
        assertThat(future).isInstanceOf(CompletableFuture.class);
    }

    @Test
    void uploadTestSuiteAsync_ShouldHandleUploadFailure() {
        // Given - Using invalid credentials will cause upload to fail
        String invalidUsername = "invalid_user";
        String invalidAccessKey = "invalid_key";

        TestSuiteUploader testSuiteUploader =
                new TestSuiteUploader(invalidUsername, invalidAccessKey, TEST_SUITE_FILE_PATH);
        CompletableFuture<String> future = testSuiteUploader.uploadTestSuiteAsync();

        // When/Then - Should handle failure gracefully by throwing CompletionException
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
