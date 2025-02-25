package io.github.lambdatest.gradle;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the asynchronous upload of test suite files to the LambdaTest platform. This class
 * manages the upload process and returns the test suite ID for test execution.
 */
public class TestSuiteUploader {

    private static final Logger logger = LogManager.getLogger(TestSuiteUploader.class);

    private String username;
    private String accessKey;
    private String testSuiteFilePath;
    private Boolean isVirtualDevice;

    /**
     * Creates a new TestSuiteUploader instance with the specified credentials and file path.
     *
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param testSuiteFilePath The path to the test suite file to be uploaded
     * @param isVirtualDevice Boolean indicating if upload is to a virtual device
     */
    public TestSuiteUploader(
            String username, String accessKey, String testSuiteFilePath, Boolean isVirtualDevice) {
        this.username = username;
        this.accessKey = accessKey;
        this.testSuiteFilePath = testSuiteFilePath;
        this.isVirtualDevice = isVirtualDevice;
    }

    /**
     * Uploads the test suite file asynchronously to LambdaTest.
     *
     * @implNote Uses CompletableFuture to perform the upload asynchronously, allowing parallel
     *     processing of other tasks.
     * @return A CompletableFuture that resolves to the uploaded test suite's ID
     */
    public CompletableFuture<String> uploadTestSuiteAsync() {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        String testSuiteId =
                                UploaderUtil.uploadAndGetId(
                                        username, accessKey, testSuiteFilePath, isVirtualDevice);
                        logger.info("Uploaded test suite ID: {}", testSuiteId);
                        return testSuiteId;
                    } catch (IOException e) {
                        logger.error("Error uploading test suite app: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }
}
