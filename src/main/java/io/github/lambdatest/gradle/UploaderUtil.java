package io.github.lambdatest.gradle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Utility class providing common upload functionality for the LambdaTest Gradle plugin. This class
 * handles the actual file upload process and response parsing.
 *
 * <p>This utility is used by both {@link AppUploader} and {@link TestSuiteUploader} to handle file
 * uploads to the LambdaTest platform.
 */
public final class UploaderUtil {
    /** Private constructor to prevent instantiation of this utility class. */
    private UploaderUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    /**
     * Uploads a file to LambdaTest and returns its ID.
     *
     * @implNote This method sends the file to {@link Constants#API_URL} and handles the multipart
     *     form data construction and response parsing.
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param filePath The path to the file to be uploaded
     * @return The ID of the uploaded file
     * @throws IOException if there's an error during file upload or response parsing
     */
    public static String uploadAndGetId(String username, String accessKey, String filePath)
            throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)  // Increase connection timeout
        .readTimeout(0, TimeUnit.MILLISECONDS)     // Increase read timeout
        .writeTimeout(0, TimeUnit.MILLISECONDS)    // Increase write timeout
        .build();

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
