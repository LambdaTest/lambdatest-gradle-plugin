package io.github.lambdatest.gradle.integration;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lambdatest.gradle.AppUploader;
import io.github.lambdatest.gradle.TestSuiteUploader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadIntegrationTest {
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
    void testAppUploadSuccess() throws Exception {
        // Arrange
        String expectedAppId = "lt://APP123456789";
        mockServer.enqueueUploadResponse(expectedAppId);

        AppUploader uploader = new AppUploader("testuser", "testkey", testAppPath);

        // Act
        CompletableFuture<String> result = uploader.uploadAppAsync();
        String actualAppId = result.get();

        // Assert
        assertEquals(expectedAppId, actualAppId);

        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertTrue(request.getPath().contains("uploadFramework"));
        assertNotNull(request.getHeader("Authorization"));
    }

    @Test
    void testTestSuiteUploadSuccess() throws Exception {
        // Arrange
        String expectedTestSuiteId = "lt://TEST123456789";
        mockServer.enqueueUploadResponse(expectedTestSuiteId);

        TestSuiteUploader uploader = new TestSuiteUploader("testuser", "testkey", testSuitePath);

        // Act
        CompletableFuture<String> result = uploader.uploadTestSuiteAsync();
        String actualTestSuiteId = result.get();

        // Assert
        assertEquals(expectedTestSuiteId, actualTestSuiteId);

        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertTrue(request.getPath().contains("uploadFramework"));
    }

    @Test
    void testUploadFailure() {
        // Arrange
        mockServer.enqueueErrorResponse(401, "Unauthorized");

        AppUploader uploader = new AppUploader("baduser", "badkey", testAppPath);

        // Act & Assert
        CompletableFuture<String> result = uploader.uploadAppAsync();
        assertThrows(Exception.class, result::get);
    }
}
