package io.github.lambdatest.gradle;

import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class TestExecutor {
    private final static Logger logger = Logger.getLogger(TestExecutor.class.getName());
    private String username;
    private String accessKey;
    private String appId;
    private String testSuiteId;
    private String device;

    public TestExecutor(String username, String accessKey, String appId, String testSuiteId, String device) {
        this.username = username;
        this.accessKey = accessKey;
        this.appId = appId;
        this.testSuiteId = testSuiteId;
        this.device = device;
    }

    public void executeTests(Map<String, String> params) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");

            StringBuilder jsonBodyBuilder = new StringBuilder(String.format("{\n"
                    + "    \"app\" : \"%s\",\n"
                    + "    \"testSuite\": \"%s\",\n"
                    + "    \"device\" :  [\"%s\"]", appId, testSuiteId, device));

            if (params.get("build") != null) jsonBodyBuilder.append(String.format(",\n    \"build\": \"%s\"", params.get("build")));
            if (params.get("deviceLog") != null) jsonBodyBuilder.append(String.format(",\n    \"deviceLog\": %s", params.get("deviceLog")));
            if (params.get("IdleTimeout") != null) jsonBodyBuilder.append(String.format(",\n    \"IdleTimeout\": %s", params.get("IdleTimeout")));
            if (params.get("queueTimeout") != null) jsonBodyBuilder.append(String.format(",\n    \"queueTimeout\": %s", params.get("queueTimeout")));
            if (params.get("video") != null) jsonBodyBuilder.append(String.format(",\n    \"video\": %s", params.get("video")));
            if (params.get("network") != null) jsonBodyBuilder.append(String.format(",\n    \"network\": %s", params.get("network")));
            if (params.get("tunnel") != null) jsonBodyBuilder.append(String.format(",\n    \"tunnel\": %s", params.get("tunnel")));
            if (params.get("tunnelName") != null) jsonBodyBuilder.append(String.format(",\n    \"tunnelName\": \"%s\"", params.get("tunnelName")));
            if (params.get("geoLocation") != null) jsonBodyBuilder.append(String.format(",\n    \"geoLocation\": \"%s\"", params.get("geoLocation")));

            jsonBodyBuilder.append("\n}");

            RequestBody body = RequestBody.create(mediaType,jsonBodyBuilder.toString());

            Request request = new Request.Builder()
                    .url(Constants.BUILD_URL)
                    .method("POST", body)
                    .addHeader("Authorization", Credentials.basic(username, accessKey))
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();

            logger.info("Running Tests");
            logger.info(response.body().string());
    } catch (IOException e) {
        logger.severe("Error executing tests: " + e.getMessage());
    }
    }
}
