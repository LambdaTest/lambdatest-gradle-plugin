package io.github.lambdatest.gradle;

import okhttp3.*;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class TestSuiteUploader {
    private final static Logger logger = Logger.getLogger(TestSuiteUploader.class.getName());
    private String username;
    private String accessKey;
    private String testSuiteFilePath;

    public TestSuiteUploader(String username, String accessKey, String testSuiteFilePath) {
        this.username = username;
        this.accessKey = accessKey;
        this.testSuiteFilePath = testSuiteFilePath;
    }

    public CompletableFuture<String> uploadTestSuiteAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("appFile", testSuiteFilePath,
                                RequestBody.create(mediaType, new java.io.File(testSuiteFilePath)))
                        .addFormDataPart("type", "espresso-android")
                        .build();
                Request request = new Request.Builder()
                        .url(Constants.API_URL)
                        .method("POST", body)
                        .addHeader("Authorization", Credentials.basic(username, accessKey))
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    String testSuiteId = jsonObject.get("app_id").getAsString();

                    logger.info("Uploaded test suite ID : " + testSuiteId);
                    return testSuiteId;
                }
            } catch (IOException e) {
                logger.severe("Error uploading test suite app: " + e.getMessage());
                throw new RuntimeException(e); // CompletableFuture will catch this exception
            }
        });
    }
}
