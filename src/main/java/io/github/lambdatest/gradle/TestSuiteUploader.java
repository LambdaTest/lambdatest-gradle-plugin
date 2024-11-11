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

    /**
     * Creates a new TestSuiteUploader instance with the specified credentials and file path.
     *
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param testSuiteFilePath The path to the test suite file to be uploaded
     */
    public TestSuiteUploader(String username, String accessKey, String testSuiteFilePath) {
        this.username = username;
        this.accessKey = accessKey;
        this.testSuiteFilePath = testSuiteFilePath;
    }

    /**
     * Uploads the test suite file asynchronously to LambdaTest.
     *
     * <p>Implementation Note: Uses CompletableFuture to perform the upload asynchronously, allowing parallel processing of other tasks.</p>
     *
     * @return A CompletableFuture that resolves to the uploaded test suite's ID
     */
    public CompletableFuture<String> uploadTestSuiteAsync() {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        String testSuiteId =
                                UploaderUtil.uploadAndGetId(username, accessKey, testSuiteFilePath);
                        logger.info("Uploaded test suite ID: {}", testSuiteId);
                        return testSuiteId;
                    } catch (IOException e) {
                        logger.error("Error uploading test suite app: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }
}
