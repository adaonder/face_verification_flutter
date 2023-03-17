# test_face_verification

A new Flutter project.

## Getting Started

This project is a starting point for a Flutter application.

Face Verification Android and iOS


# For Android;

+ Kotlin: 1.8.0
+ ndk {
  abiFilters 'armeabi-v7a', 'arm64-v8a'
  }
+ viewBinding { enabled = true }
+ Android Extra lib;

    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    //Face lib
    implementation 'net.java.dev.jna:jna:5.11.0@aar'
    implementation 'io.swagger:swagger-annotations:1.5.18'
    implementation 'com.squareup.okhttp:okhttp:2.7.5'
    implementation 'com.squareup.okhttp:logging-interceptor:2.7.5'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'io.gsonfire:gson-fire:1.8.0'
    implementation 'org.threeten:threetenbp:1.3.5'

    //extra
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    implementation 'commons-io:commons-io:2.8.0'



# TODO iOS;