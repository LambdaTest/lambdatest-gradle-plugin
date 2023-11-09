# lambdatest-gradle-plugin
This repository contains the source code for LambdaTest's Espresso Gradle plugin.

## Purpose
The functions of this plugins are:
- Uploads any `Android` app (.apk) and `Espresso` Test Suite app (.apk) file.
- Execute Espresso tests on LamdaTest.

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
### Supported Capabilties:
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
To run the plugin added in the project's `build.gradle`:
```
./gradlew runLambdaTest
```
