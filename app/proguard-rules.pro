# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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
-renamesourcefileattribute SourceFile
-optimizationpasses 5
-printmapping mapping.txt
-printmapping build/outputs/mapping/release/mapping.txt
-verbose
#-dontoptimize
#-dontshrink
-dontusemixedcaseclassnames
#-keepparameternames
#-dontpreverify
-keepattributes *Annotation*
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-repackageclasses 'ax.bx.cx'
-flattenpackagehierarchy 'ax.bx.cx'
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-overloadaggressively
#-adaptresourcefilenames **.properties,**.gif,**.jpg,**.png,**.xml,**.xsd,**.wsdl
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
-keep @kotlinx.android.parcel.Parcelize public class *
# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

-dontwarn androidx.databinding.**
-keep class androidx.databinding.** { *; }
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
}
-keep class com.google.common.* {*;}
-keep class com.google.common.** {*;}
-keep class java.util.concurrent.** { *; }
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
# ACRA loads Plugins using reflection
-keep class * implements org.acra.plugins.Plugin {*;}
# ACRA uses enum fields in json
-keep class org.acra.* {*;}
-keep class org.acra.** {*;}

# autodsl accesses constructors using reflection
-keepclassmembers class * implements org.acra.config.Configuration { <init>(...); }

# ACRA creates a proxy for this interface
-keep interface org.acra.ErrorReporter
-dontwarn android.support.**

-dontwarn com.google.auto.service.AutoService
-keep class com.crashlytics.** { *; }
-keep class java.util.concurrent.** { *; }
-dontwarn com.crashlytics.**
-keep class com.mbridge.** {*; }
-keep interface com.mbridge.** {*; }
-keep class android.support.v4.** { *; }
-dontwarn com.mbridge.**
-keep class **.R$* { public static final int mbridge*; }
-keep public class com.mbridge.* extends androidx.** { *; }
-keep public class androidx.viewpager.widget.PagerAdapter{ *; }
-keep interface androidx.annotation.IntDef{ *; }
-keep interface androidx.annotation.Nullable{ *; }
-keep interface androidx.annotation.CheckResult{ *; }
-keep interface androidx.annotation.NonNull{ *; }
-keep public class androidx.fragment.app.Fragment{ *; }
-keep public class androidx.core.content.FileProvider{ *; }
-keep public class androidx.core.app.NotificationCompat{ *; }
-keep public class androidx.appcompat.widget.AppCompatImageView { *; }
-keep public class androidx.recyclerview.*{ *; }
-keepnames class kotlinx.coroutines.** {*;}
-keepnames class kotlinx.coroutines.* {*;}
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keepnames class com.bmik.android.sdk.SDKBaseController
-keepnames class com.bmik.android.sdk.BaseSdkApplication

-keep class kotlinx.coroutines.* {*;}
-keep class kotlinx.coroutines.** {*;}
-keep class com.google.common.* {*;}
-keep class com.google.common.** {*;}
-keep class android.net.ConnectivityManager {*;}
-keep class android.app.AppOpsManager {*;}
# ACRA loads Plugins using reflection
-keep class * implements org.acra.plugins.Plugin {*;}
# ACRA uses enum fields in json
-keep class org.acra.* {*;}
-keep class org.acra.** {*;}

# autodsl accesses constructors using reflection
-keepclassmembers class * implements org.acra.config.Configuration { <init>(...); }

# ACRA creates a proxy for this interface
-keep interface org.acra.ErrorReporter

-dontwarn android.support.**

-dontwarn com.google.auto.service.AutoService
-keep class com.crashlytics.** { *; }
-keep class java.util.concurrent.** { *; }
-dontwarn com.crashlytics.**
-keep class com.mbridge.** {*; }
-keep interface com.mbridge.** {*; }
-keep class android.support.v4.** { *; }
-dontwarn com.mbridge.**
-keep class **.R$* { public static final int mbridge*; }
-keep public class com.mbridge.* extends androidx.** { *; }
-keep public class androidx.viewpager.widget.PagerAdapter{ *; }
-keep interface androidx.annotation.IntDef{ *; }
-keep interface androidx.annotation.Nullable{ *; }
-keep interface androidx.annotation.CheckResult{ *; }
-keep interface androidx.annotation.NonNull{ *; }
-keep public class androidx.fragment.app.Fragment{ *; }
-keep public class androidx.core.content.FileProvider{ *; }
-keep public class androidx.core.app.NotificationCompat{ *; }
-keep public class androidx.appcompat.widget.AppCompatImageView { *; }
-keep public class androidx.recyclerview.*{ *; }

# Retrofit
-keep class com.google.gson.** { *; }
-keep public class com.google.gson.** {public private protected *;}
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.xml.stream.** { *; }
-keep class retrofit.** { *; }
-keep class com.google.appengine.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**


-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keep class retrofit2.* { *; }
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Add any classes the interact with gson
# the following line is for illustration purposes
-keep class com.example.asheq.models.** { *; }



# Hide warnings about references to newer platforms in the library
-dontwarn android.support.v7.**
# don't process support library
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
# To support Enum type of class members
-keepclassmembers enum * { *; }

-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class com.longkd.chatgpt_openai.open.dto.** { *; }
-keep class com.longkd.chatgpt_openai.open.dto.*
-keep class com.longkd.chatgpt_openai.base.model.** { *; }
-keep class com.longkd.chatgpt_openai.base.model.*
-keepnames class com.longkd.chatgpt_openai.open.client.OpenAiService
-keep class com.longkd.chatgpt_openai.open.client.OpenAiUtils
-keep class com.longkd.chatgpt_openai.open.client.OpenAiUtils{*;}
-keep class com.longkd.chatgpt_openai.base.OpenAIHolder
-keep class com.longkd.chatgpt_openai.base.OpenAIHolder{*;}
-keep class android.content.SharedPreferences
-keep class android.content.SharedPreferences{}
-keepattributes Signature

-keepattributes Annotation

-keep class okhttp3.** { *; }

-keep interface okhttp3.** { *; }

-dontwarn okhttp3.**
-keep class okhttp3.Request
-keep class okhttp3.Request {*;}
-keep class okhttp3.Request$Builder
-keep class okhttp3.Interceptor
-keep class okhttp3.Interceptor {*;}
-keep class okhttp3.Interceptor$Chain
-dontwarn okhttp3.Interceptor$Chain
-dontwarn okhttp3.Request$Builder
-keep class com.squareup.okhttp3.** {
*;
}
-dontwarn com.squareup.okhttp.*
# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keep class java.lang.reflect.Method{*;}
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}
-keep class * extends com.longkd.chatgpt_openai.base.mvvm.BaseViewModel {
    <init>();
}
-keep class * extends com.longkd.chatgpt_openai.base.mvvm.BaseViewModel {
    <init>(android.app.Application);
}
-keep class com.longkd.chatgpt_openai.feature.home.HomeViewModel {
    *;
}
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}
-keep class * extends com.longkd.chatgpt_openai.base.mvvm.BaseViewModel {
    <init>();
}
-keep class * extends com.longkd.chatgpt_openai.base.mvvm.BaseViewModel {
    <init>(android.app.Application);
}

-keep class org.xmlpull.v1.** { *; }
-dontwarn org.xmlpull.v1.**
-keep class com.longkd.chatgpt_openai.open.client.OpenAiService
-keep class com.longkd.chatgpt_openai.open.client.OpenAiService{*;}
-keep class com.longkd.chatgpt_openai.open.client.TimeStampService
-keep class com.longkd.chatgpt_openai.open.client.TimeStampService{*;}
-keepnames class com.longkd.chatgpt_openai.open.dto.completion.CompletionRequest
-keepnames class com.longkd.chatgpt_openai.open.dto.completion.*{
*;
}
-keep class retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
-keep class * extends retrofit2.CallAdapter
-keep class * extends retrofit2.CallAdapter{*;}
-keep class retrofit2.CallAdapter{*;}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepnames class io.reactivex.**
-keepnames class io.reactivex.** { public *; }

-keepnames class com.longkd.chatgpt_openai.base.data.TopicDto
-keepnames class com.longkd.chatgpt_openai.base.data.TopicDto{ *; }
-keepclassmembers class com.longkd.chatgpt_openai.base.data.TopicDto {
    private <fields>;
}

-keepnames class com.longkd.chatgpt_openai.feature.art.StyleArtDto
-keepnames class com.longkd.chatgpt_openai.feature.art.StyleArtDto{ *; }
-keepclassmembers class com.longkd.chatgpt_openai.feature.art.StyleArtDto {
    private <fields>;
}
-keepclassmembers class com.longkd.chatgpt_openai.open.dto.completion.TokenDto {
    private <fields>;
}
-keep class com.longkd.chatgpt_openai.base.widget.ItemSetting
-keep class com.longkd.chatgpt_openai.base.widget.ItemSetting{*;}
-keep class com.longkd.chatgpt_openai.base.util.MyActivityObserver
-keep class com.longkd.chatgpt_openai.base.util.MyActivityObserver{*;}
-keep class com.longkd.chatgpt_openai.feature.art.wallpaper.WallPaperViewModel {
    *;
}
-keep class com.longkd.chatgpt_openai.feature.home_new.gallery.ListGalleryViewModel {
    *;
}
-keep class com.longkd.chatgpt_openai.feature.summary.SummaryFileViewModel{
    *;
}
-keep public class com.android.installreferrer.** { *; }