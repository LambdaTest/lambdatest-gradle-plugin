package io.github.lambdatest.gradle;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestExecutor {
    private static final Logger logger = LogManager.getLogger(TestExecutor.class);

    private String username;
    private String accessKey;
    private String appId;
    private String testSuiteId;
    private List<String> device;
    private Boolean isFlutter;

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
