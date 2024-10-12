package io.github.lambdatest.gradle;

public class Constants {
    private Constants() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static final String API_URL = "https://manual-api.lambdatest.com/app/uploadFramework";
    public static final String BUILD_URL =
            "https://mobile-api.lambdatest.com/framework/v1/espresso/build";
    public static final String FLUTTER_BUILD_URL =
            "https://mobile-api.lambdatest.com/framework/v1/flutter/build";
}
