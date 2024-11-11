package io.github.lambdatest.gradle;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the asynchronous upload of application files to the LambdaTest platform.
 * This class manages the upload process and returns the application ID for test execution.
 *
 * <p>Uses {@link UploaderUtil#uploadAndGetId(String, String, String)} for the actual file upload process.</p>
 */
public class AppUploader {

    private static final Logger logger = LogManager.getLogger(AppUploader.class);

    private String username;
    private String accessKey;
    private String appFilePath;

    /**
     * Creates a new AppUploader instance with the specified credentials and file path.
     *
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param appFilePath The path to the application file to be uploaded
     */
    public AppUploader(String username, String accessKey, String appFilePath) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");
        if (accessKey == null) throw new IllegalArgumentException("Access Key cannot be null");
        if (appFilePath == null) throw new IllegalArgumentException("App File Path cannot be null");

        this.username = username;
        this.accessKey = accessKey;
        this.appFilePath = appFilePath;
    }

    /**
     * Uploads the application file asynchronously to LambdaTest.
     *
     * @return A CompletableFuture that resolves to the uploaded application's ID
     * @implNote Uses CompletableFuture to perform the upload asynchronously, allowing
     *          parallel processing of other tasks
     */
    public CompletableFuture<String> uploadAppAsync() {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        String appId =
                                UploaderUtil.uploadAndGetId(username, accessKey, appFilePath);
                        logger.info("Uploaded app ID: {}", appId);
                        return appId;
                    } catch (IOException e) {
                        logger.error("Error uploading app: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }
}
