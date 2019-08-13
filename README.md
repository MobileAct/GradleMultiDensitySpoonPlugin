# GradleMultiDensitySpoonPlugin
[Gradle Spoon Plugin](https://github.com/jaredsburrows/gradle-spoon-plugin) Extension for multi density

## Usage(alpha)
1. execute `./gradlew :plugin:jar`, find output jar(`/plugin/build/libs`)
1. move jar to your project(for example, app module's `plugins` directory)
1. add classpath(for example `/app/build.gradle`'s buildScript.dependencies block, `classpath fileTree(dir: 'plugins', include: ['*.jar'])`)
1. add `apply plugin 'mdspoon'`
1. execute gradle some mdspoon task.

configuration:

```groovy
mdspoon {
    densities = [320, 480, 640] // default value is 420
    titleDelimiter = "-" // this is default value
}
```

Other configuration: [see Gradle Spoon Plugin](https://github.com/jaredsburrows/gradle-spoon-plugin#usage)

## Output
In default configuration, output `build/spoon-output/{density}/**`, and all density images are copied to `build/spoon-output/merged/**`

## License
### Plugin
- using [Kotlin Standard Library](https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib), published by [Apache License 2.0](https://github.com/JetBrains/kotlin/tree/master/license)
- using [Gradle Spoon  Plugin](https://github.com/jaredsburrows/gradle-spoon-plugin), published by [Apache License 2.0](https://github.com/jaredsburrows/gradle-spoon-plugin/blob/master/LICENSE)

### Sample
- using [Kotlin Standard Library](https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib), published by [Apache License 2.0](https://github.com/JetBrains/kotlin/tree/master/license)
- using [Gradle Spoon  Plugin](https://github.com/jaredsburrows/gradle-spoon-plugin), published by [Apache License 2.0](https://github.com/jaredsburrows/gradle-spoon-plugin/blob/master/LICENSE)
- using [Spoon](https://github.com/square/spoon), published by [Apache Licednse 2.0](https://github.com/square/spoon/blob/master/LICENSE.txt)
- using [AndroidX](https://github.com/aosp-mirror/platform_frameworks_support), published by [Apache License 2.0](https://github.com/aosp-mirror/platform_frameworks_support/blob/androidx-master-dev/LICENSE.txt)
- using [ConstraintLayout](https://android.googlesource.com/platform/frameworks/opt/sherpa/+/refs/heads/studio-master-dev/constraintlayout/), published by [Apache License 2.0](https://android.googlesource.com/platform/frameworks/opt/sherpa/+/refs/heads/studio-master-dev/constraintlayout/src/main/java/android/support/constraint/ConstraintLayout.java)
- using [AndroidX Test](https://github.com/android/android-test), published by [Apache License 2.0](https://github.com/android/android-test/blob/master/LICENSE)
- using [JUnit4](https://github.com/junit-team/junit4), published by [Eclipse Public License 1.0](https://github.com/junit-team/junit4/blob/master/LICENSE-junit.txt)

## Contribute
ToDo: Write

## Other
Author: [@MeilCli](https://github.com/MeilCli)
