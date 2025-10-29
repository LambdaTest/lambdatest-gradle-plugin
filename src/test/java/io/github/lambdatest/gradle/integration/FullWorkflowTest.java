package io.github.lambdatest.gradle.integration;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lambdatest.gradle.AppUploader;
import io.github.lambdatest.gradle.TestExecutor;
import io.github.lambdatest.gradle.TestSuiteUploader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FullWorkflowTest {
    private MockLambdaTestServer mockServer;
    private String testAppPath;
    private String testSuitePath;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockLambdaTestServer();
        mockServer.start();

        testAppPath = getClass().getClassLoader().getResource("test-app.apk").getPath();
        testSuitePath = getClass().getClassLoader().getResource("test-suite.apk").getPath();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.stop();
    }

    @Test
    void testFullWorkflow() throws Exception {
        // Arrange - Mock responses for upload and execution
        mockServer.enqueueUploadResponse("lt://APP123456789");
        mockServer.enqueueUploadResponse("lt://TEST123456789");
        mockServer.enqueueBuildResponse("BUILD123456789");

        // Act - Upload app and test suite
        AppUploader appUploader = new AppUploader("testuser", "testkey", testAppPath);
        TestSuiteUploader testUploader =
                new TestSuiteUploader("testuser", "testkey", testSuitePath);

        CompletableFuture<String> appIdFuture = appUploader.uploadAppAsync();
        CompletableFuture<String> testIdFuture = testUploader.uploadTestSuiteAsync();

        String appId = appIdFuture.get();
        String testId = testIdFuture.get();

        // Execute tests
        TestExecutor executor =
                new TestExecutor(
                        "testuser", "testkey", appId, testId, Arrays.asList("Pixel 3-9"), false);
        executor.executeTests(new HashMap<>());

        // Assert - Verify all three requests were made
        RecordedRequest appUploadRequest = mockServer.takeRequest();
        assertEquals("POST", appUploadRequest.getMethod());
        assertTrue(appUploadRequest.getPath().contains("uploadFramework"));

        RecordedRequest testUploadRequest = mockServer.takeRequest();
        assertEquals("POST", testUploadRequest.getMethod());
        assertTrue(testUploadRequest.getPath().contains("uploadFramework"));

        RecordedRequest executionRequest = mockServer.takeRequest();
        assertEquals("POST", executionRequest.getMethod());
        assertTrue(executionRequest.getPath().contains("espresso/build"));
    }

    @Test
    void testUploadOnlyWorkflow() throws Exception {
        // Arrange
        mockServer.enqueueUploadResponse("lt://APP123456789");
        mockServer.enqueueUploadResponse("lt://TEST123456789");

        // Act
        AppUploader appUploader = new AppUploader("testuser", "testkey", testAppPath);
        TestSuiteUploader testUploader =
                new TestSuiteUploader("testuser", "testkey", testSuitePath);

        CompletableFuture<String> appIdFuture = appUploader.uploadAppAsync();
        CompletableFuture<String> testIdFuture = testUploader.uploadTestSuiteAsync();

        String appId = appIdFuture.get();
        String testId = testIdFuture.get();

        // Assert - Since uploads are async, either could get either ID
        assertTrue(appId.equals("lt://APP123456789") || appId.equals("lt://TEST123456789"));
        assertTrue(testId.equals("lt://APP123456789") || testId.equals("lt://TEST123456789"));
        assertNotEquals(appId, testId); // They should be different

        RecordedRequest appUploadRequest = mockServer.takeRequest();
        assertEquals("POST", appUploadRequest.getMethod());
        assertTrue(appUploadRequest.getPath().contains("uploadFramework"));

        RecordedRequest testUploadRequest = mockServer.takeRequest();
        assertEquals("POST", testUploadRequest.getMethod());
        assertTrue(testUploadRequest.getPath().contains("uploadFramework"));
    }

    @Test
    void testExecutionWithExistingIds() throws Exception {
        // Arrange
        mockServer.enqueueBuildResponse("BUILD123456789");

        // Act
        TestExecutor executor =
                new TestExecutor(
                        "testuser",
                        "testkey",
                        "lt://EXISTING_APP",
                        "lt://EXISTING_TEST",
                        Arrays.asList("Pixel 3-9"),
                        false);
        executor.executeTests(new HashMap<>());

        // Assert
        RecordedRequest executionRequest = mockServer.takeRequest();
        assertEquals("POST", executionRequest.getMethod());
        assertTrue(executionRequest.getPath().contains("espresso/build"));
    }
}
