# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/users/aastha.gupta/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#Keeping Various Attributes
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes *Annotation*


# Keeping Various Classes
# Gson specific classes
-keep class com.google.**
-dontwarn com.google.**
-dontwarn rx.internal.util.**
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
#OkHttp3 specific classes
-dontwarn okhttp3.**
-keep class okhttp3.** {*;}
-dontnote okhttp3.**

#Keeping Library Classes
-keep class com.webkul.sociallogin.** { *; }
-keepclassmembers class com.webkul.sociallogin.** { *; }
-keepclasseswithmembers class * {
  @com.webkul.sociallogin.* <methods>;
}

#Ignoring theprogaurd for these classes as packages are of core for us
-dontnote org.apache.http.**
-dontnote android.net.http.**
-dontnote com.google.android.gms.**
-dontnote com.android.vending.**



#Proguard rules for faceBook login
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepnames class com.facebook.FacebookActivity
-keepnames class com.facebook.CustomTabActivity

-keep class com.facebook.login.Login
-keep class com.twitter.sdk.android.** { *; }
-dontwarn com.twitter.sdk.android.**

### OKIO

# java.nio.file.* usage which cannot be used at runtime. Animal sniffer annotation.
-dontwarn okio.**
-dontnote okio.**
# JDK 7-only method which is @hide on Android. Animal sniffer annotation.
-dontwarn okio.DeflaterSink.**
-dontnote okio.DeflaterSink.**


-dontwarn com.squareup.picasso.**
-dontnote com.squareup.picasso.**

#Twitter Proguard Rules
-dontwarn com.squareup.okhttp.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn rx.**
-dontwarn retrofit.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* *;
}

# Brahamastra
#-ignorewarnings