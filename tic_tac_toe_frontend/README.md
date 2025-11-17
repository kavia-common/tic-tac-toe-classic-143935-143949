# declarative-samples-android-app
A sample Android application written in the Declarative Gradle DSL, using the prototype Declarative Gradle `androidApplication` Software Type defined in the `org.gradle.experimental.android-ecosystem` ecosystem plugin.

## Building and Running

This sample shows the definition of a multiproject Android application implemented using Kotlin 2.0.21 source code.
The project is the result of reproducing the project produced by the `gradle init` command in Gradle 8.9 as an Android project.

Environment used and recommended for reliable builds:
- Gradle Wrapper: 8.8 (configured in gradle-wrapper.properties)
- Android Gradle Plugin: 8.5.2
- Kotlin Gradle Plugin: 1.9.24
- JDK: 17

Build performance settings (already applied in gradle.properties):
- org.gradle.caching=true
- org.gradle.parallel=true
- org.gradle.configureondemand=false
- org.gradle.workers.max=2
- org.gradle.jvmargs=-Xmx2g -Dkotlin.daemon.jvm.options=-Xmx1g

To build the project without running, use:

```shell
  ./gradlew clean :app:assembleDebug --no-daemon --stacktrace
```

To run the application, first install it on a connected Android device using:

```shell
  ./gradlew :app:installDebug
```

Then search for "Sample Declarative Gradle Android App" and launch app to see a hello world message.