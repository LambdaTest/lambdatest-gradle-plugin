package io.github.lambdatest.gradle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class UploaderUtil {
    private UploaderUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static String uploadAndGetId(String username, String accessKey, String filePath)
            throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                                "appFile",
                                filePath,
                                RequestBody.create(new File(filePath), mediaType))
                        .addFormDataPart("type", "espresso-android")
                        .build();
        Request request =
                new Request.Builder()
                        .url(Constants.API_URL)
                        .addHeader("Authorization", Credentials.basic(username, accessKey))
                        .post(body)
                        .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            String id = jsonObject.get("app_id").getAsString();

            return id;
        }
    }
}
