package io.github.lambdatest.gradle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AppUploader {

    private static final Logger logger = LogManager.getLogger(AppUploader.class);

    private String username;
    private String accessKey;
    private String appFilePath;

    public AppUploader(String username, String accessKey, String appFilePath) {
        this.username = username;
        this.accessKey = accessKey;
        this.appFilePath = appFilePath;
    }

    public CompletableFuture<String> uploadAppAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("appFile", appFilePath,
                                RequestBody.create(mediaType, new java.io.File(appFilePath)))
                        .addFormDataPart("type", "espresso-android")
                        .build();
                Request request = new Request.Builder()
                        .url(Constants.API_URL)
                        .method("POST", body)
                        .addHeader("Authorization", Credentials.basic(username, accessKey))
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // Parse the JSON response and extract the app_id
                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    String appId = jsonObject.get("app_id").getAsString();

                    logger.info("Uploaded app ID: {}", appId);
                    return appId;
                }
            } catch (IOException e) {
                logger.error("Error uploading app: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}
