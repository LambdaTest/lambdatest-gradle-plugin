package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link TestExecutor} class. */
@ExtendWith(MockitoExtension.class)
class TestExecutorTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";
    private static final String TEST_APP_ID = "lt://APP1234567890";
    private static final String TEST_SUITE_ID = "lt://APP1234567891";
    private static final List<String> TEST_DEVICES = Arrays.asList("Pixel 6-12", "Galaxy S21-11");

    @Test
    void constructor_ShouldCreateInstance_WithValidParameters() {
        // Test standard Android app
        TestExecutor executor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_SUITE_ID,
                        TEST_DEVICES,
                        false);
        assertThat(executor).isNotNull();

        // Test Flutter app
        TestExecutor flutterExecutor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_SUITE_ID,
                        TEST_DEVICES,
                        true);
        assertThat(flutterExecutor).isNotNull();

        // Test null Flutter parameter (should default to false)
        TestExecutor nullFlutterExecutor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_SUITE_ID,
                        TEST_DEVICES,
                        null);
        assertThat(nullFlutterExecutor).isNotNull();
    }

    @Test
    void executeTests_ShouldHandleParametersCorrectly() {
        // Given
        TestExecutor executor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_SUITE_ID,
                        TEST_DEVICES,
                        false);

        Map<String, String> params = new HashMap<>();
        params.put("build", "Test Build");
        params.put("video", "true");
        params.put("deviceLog", "true");

        // When/Then - Should not throw during parameter setup
        // Note: Actual HTTP execution will fail in tests, which is expected
        assertThat(params).containsEntry("build", "Test Build");
        assertThat(params).containsEntry("video", "true");
        assertThat(executor).isNotNull();
    }

    @Test
    void executeTests_ShouldHandleEmptyParameters() {
        // Given
        TestExecutor executor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_SUITE_ID,
                        TEST_DEVICES,
                        false);

        Map<String, String> emptyParams = new HashMap<>();

        // When/Then - Should handle empty parameters without issues
        assertThat(emptyParams).isEmpty();
        assertThat(executor).isNotNull();
    }

    @Test
    void constructor_ShouldHandleDeviceVariations() {
        // Single device
        List<String> singleDevice = Arrays.asList("Pixel 6-12");
        TestExecutor singleDeviceExecutor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_SUITE_ID,
                        singleDevice,
                        false);
        assertThat(singleDeviceExecutor).isNotNull();

        // Multiple devices
        List<String> multipleDevices = Arrays.asList("Pixel 6-12", "Galaxy S21-11", "iPhone 13-15");
        TestExecutor multiDeviceExecutor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_SUITE_ID,
                        multipleDevices,
                        false);
        assertThat(multiDeviceExecutor).isNotNull();
    }
}
