package io.github.lambdatest.gradle;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the execution of tests on the LambdaTest platform. This class handles the test execution
 * configuration and communication with the LambdaTest API.
 *
 * <p>Uses endpoints defined in {@link Constants} for API communication.
 */
public class TestExecutor {
    private static final Logger logger = LogManager.getLogger(TestExecutor.class);

    private String username;
    private String accessKey;
    private String appId;
    private String testSuiteId;
    private List<String> device;
    private Boolean isFlutter;

    /**
     * Creates a new TestExecutor with the specified configuration.
     *
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param appId The ID of the uploaded application
     * @param testSuiteId The ID of the uploaded test suite
     * @param device List of target devices for test execution
     * @param isFlutter Boolean indicating if this is a Flutter application
     */
    public TestExecutor(
            String username,
            String accessKey,
            String appId,
            String testSuiteId,
            List<String> device,
            Boolean isFlutter) {
        this.username = username;
        this.accessKey = accessKey;
        this.appId = appId;
        this.testSuiteId = testSuiteId;
        this.device = device;
        this.isFlutter = isFlutter;
    }

    /**
     * Executes the tests on LambdaTest with the specified parameters.
     *
     * @implNote This method constructs the test capabilities and sends them to either {@link
     *     Constants#BUILD_URL} or {@link Constants#FLUTTER_BUILD_URL} based on whether it's a
     *     Flutter or standard application.
     * @param params Map of additional test execution parameters
     * @throws IOException if there's an error in communication with the LambdaTest API
     */
    public void executeTests(Map<String, String> params) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            MediaType mediaType = MediaType.parse("application/json");

            Map<String, Object> capabilities = new HashMap<>();
            capabilities.put("app", appId);
            capabilities.put("testSuite", testSuiteId);
            capabilities.put("device", device);
            capabilities.putAll(params);

            logger.info("Capabilities: {}", capabilities);

            String url =
                    (isFlutter == null || !isFlutter)
                            ? Constants.BUILD_URL
                            : Constants.FLUTTER_BUILD_URL;
            RequestBody body = RequestBody.create(gson.toJson(capabilities), mediaType);

            Request request =
                    new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", Credentials.basic(username, accessKey))
                            .addHeader("Content-Type", "application/json")
                            .post(body)
                            .build();
            Response response = client.newCall(request).execute();

            logger.info("Running Tests");
            logger.info(response.body().string());
        } catch (IOException e) {
            logger.error("Error executing tests: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
