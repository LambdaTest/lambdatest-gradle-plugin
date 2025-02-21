# lambdatest-gradle-plugin

This repository contains the source code for LambdaTest's Espresso Gradle plugin.

## Purpose
The functions of this plugin are:
- Uploads any `Android` app (.apk) and `Espresso` Test Suite app (.apk) file.
- Execute Espresso tests on LambdaTest.

## Usage
To use this plugin in your project, please do the following:

### Add to `build.gradle`:
```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.lambdatest:lambdatest-gradle-plugin:1.0.0"
  }
}

apply plugin: "io.github.lambdatest.gradle"

```
### Add LambdaTest parameters to `build.gradle`:
#### Configuration for running test and optionally uploading your apps

```
runLambdaTest {
    username = 'yourLambdaTestUsername'
    accessKey = 'yourLambdaTestAccessKey'
    appFilePath = 'pathToYourAppFile'
    testSuiteFilePath = 'pathToYourTestSuite'
    device = 'desiredDeviceForTesting'
    isFlutter = true //if you are running flutter dart tests
    appId = "lt//1234343" //provide this only if you have already uploaded the app
    testSuiteId = "lt//1223444" //provide this only if you have already uploaded the app
}
```

#### Configuration for only uploading your apps

```
uploadApkToLambdaTest {
    username = 'yourLambdaTestUsername'
    accessKey = 'yourLambdaTestAccessKey'
    appFilePath = 'pathToYourAppFile'
    testSuiteFilePath = 'pathToYourTestSuite'
}
```


### Supported Capabilities:

The following capabilities are supported:

- `app`: Enter the app id generated while uploading the app. Example:lt://APP123456789123456789
- `testSuite`: Enter the test suite id generated while uploading the test suite. Example: lt://APP123456789123456789
- `device`: Enter the name and os version of the device in “DeviceName-OSVersion” format. Example: Pixel 3 XL-9 or Galaxy S21 Ultra 5G-11.
- `video`: Generate video for all the tests that have run. Example: true.
- `queueTimeout`: Enter the time in seconds after which you want your build to timeout from queue. Example: 300.
- `idleTimeout`: Enter the time in seconds for maximum running time on a test in the build. Example: 120.
- `deviceLog`: Boolean value to generate device logs. Example: true.
- `build`: Set the name of the Espresso test build. Example: My Espresso Build.
- `geoLocation`: Set the geolocation country code if you want to enable the same in your test. Example - FR.
- `tunnel`, `tunnelName`: Set tunnel as true and provide the tunnelName such as NewTunnel as needed if you are running a tunnel.

## Execution:
#### To run the test with the plugin added in the project's `build.gradle`:
```
./gradlew runLambdaTest
```

#### If you just want to upload apks and not run actual tests:

```
./gradlew uploadApkToLambdaTest
```


## About LambdaTest

[LambdaTest](https://www.lambdatest.com/) is a cloud based selenium grid infrastructure that can
help you run automated cross browser compatibility tests on 2000+
different browser and operating system environments. LambdaTest supports
all programming languages and frameworks that are supported with Selenium,
and have easy integrations with all popular CI/CD platforms. It's a perfect
solution to bring your [selenium automation testing](https://www.lambdatest.com/selenium-automation)
to cloud based infrastructure that not only helps you increase your test coverage over
multiple desktop and mobile browsers, but also allows you to cut down your
test execution time by running tests on parallel.

## License

Licensed under the [APACHE license](./LICENSE).
