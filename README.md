# VenueNext Android SDK integration

This is a sample project using the VenueNext Android SDK. 

The SDK and demo app heavily rely on single-activity navigation principles. Future updates to the demo app will provide examples for this use case in a separate activity from the Settings screen. 

For more information use this link:

[https://venuenext.github.io](https://venuenext.github.io)

# Running the Android Demo App
After pulling down VenueNext's Android demo app, you will notice that it doesn't quite work, yet. In fact, it doesn't even compile. There is some set up for you to do first. Follow this step-by-step guide to get started.

## Maven Credentials
Navigate to the `config.gradle` file. Update the username and password for the Maven credentials. This will allow the SDK to be pulled into the project.

```groovy
repositories {
    maven {
        url "https://venuenext.jfrog.io/venuenext/venuenextsdk-android"
        credentials {
            username = 'YOUR_USERNAME'
            password = 'YOUR_PASSWORD'
        }
    }
}
```

> **Pro Tip**: Double tap the `SHIFT` key in Android Studio to quickly search for and navigate to a file.

## Google GMS Key
Navigate to the `AndroidManifest.xml` file. Update the Google GMS application ID.

```xml
54  <meta-data
55    android:name="com.google.android.gms.ads.APPLICATION_ID"
56    android:value="YOUR_GMS_ADS_KEY" />
```

At this point, the app will compile. However, it will be stuck on endless loading screen. Now you need to configure the SDK.

## VenueNext SDK Key
Navigate to the `MainActivity.kt` file. Update the VenueNext SDK Key and Secret.
```kotlin
62    val sdkKey = "YOUR_VN_SDK_KEY"
63    val sdkSecret = "YOUR_VN_SDK_SECRET"
```
> **<span style="color:yellow">WARNING</span>** Make sure you keep your key and secret safe. Hardcoding the values here is fine for quick set up and demonstration purposes, but should never go into a public release.

Now, after running the app, you will still see the endless loading screen, and will likely get an error: `Problem with the User Service: Unable to resolve host "user-server.venuenext.net": No address associated with hostname`.

The next step is to fix the `JSON` configuration file.

> If you have not received your VenueNext configuration file, please speak with a member of the VenueNext team, as it will be necessary to continue.

## Configuration File
VenueNext utilizes `JSON` configuation files to simplify the process of configuring the SDK for our integrators.

Navigate to `vn_sdk_config.json`. Replace its contents with the contents of the config file provided to you by VenueNext. You can now run the app and test its functionality.

For most integrations, there will only be one configuration file. It is possible for some integrators to support multiple organizations within one app. This is currently a rare use case.

If you are **not** supporting multiple organizations within one app, you can ignore the `vn_sdk_config_2.json` file. If you **are** supporting multiple organizations within one app, you may want to update the `vn_sdk_config_2.json` file with the contents of an additional VenueNext provided configuration file. This will allow you to test switching between the organizations.

### How Do I Know If I Am Supporting Multiple Organizations?
For the most part, if you have only been provided with one configuration file, you are not supporting multiple organizations.

# Optional - Additional Set Up

## Localytics
If you are using Localytics, you can update your Localytics scheme in the `AndroidManifest.xml` file.
```xml
24    <intent-filter>
25        <data android:scheme="YOUR_LOCALYTICS_SCHEME" />
26        <action android:name="android.intent.action.VIEW" />
27
28        <category android:name="android.intent.category.DEFAULT" />
29        <category android:name="android.intent.category.BROWSABLE" />
30    </intent-filter>
```

You will also need to update your Localytics key. Navigate to the `localytics.xml` file.
```xml
3   <!-- Define your Localytics app key-->
4   <string name="ll_app_key" translatable="false">YOUR_LOCALYTICS_APP_KEY</string>
```

## Deep Linking
If you would like to test deep linking into the app, update the deep link scheme in the `AndroidManifest.xml` file.
```xml
31    <intent-filter>
32        <data android:scheme="YOUR_APP_DEEPLINK_SCHEME" />
33        <action android:name="android.intent.action.VIEW" />
34
35        <category android:name="android.intent.category.DEFAULT" />
36        <category android:name="android.intent.category.BROWSABLE" />
37    </intent-filter>
```
