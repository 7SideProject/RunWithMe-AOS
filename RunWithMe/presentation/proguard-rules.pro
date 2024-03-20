# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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
#-renamesourcefileattribute SourceFile


-printusage ./usage.txt


-keep class javax.mail.** { *; }
-keep class com.sun.mail.** { *; }
-keep class com.side.data.model.** { *;}
-keep class com.side.data.mapper.** { *;}
-keep class com.side.data.entity.** { *; }
-keep class com.side.data.util.** { *; }
-keep class com.side.domain.base.BaseResponse { *; }
-keep class com.side.domain.utils.ResultType { *; }
-keep class com.side.domain.model.** { *; }
-keep class com.side.runwithme.model.** { *;}
-keep class com.side.runwithme.mapper.** { *;}
-keep class com.side.runwithme.base.** { *; }
-keep class com.side.runwithme.util.EventFlow { *; }
-keep class com.side.runwithme.util.EventFlowSlot { *; }
-keep class com.side.runwithme.util.MutableEventFlow { *; }
-keep class com.side.runwithme.util.ReadOnlyEventFlow { *; }
-keep class com.side.runwithme.util.SaveableMutableStateFlow { *; }
-keep class com.side.runwithme.view.join.GMailSender { *; }
-keep class com.side.runwithme.view.join.SendMail { *; }
-keep class com.kakao.sdk.**.model.* { *; }
-keep class * extends com.google.gson.TypeAdapter

-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**
-dontwarn javax.mail.**
-dontwarn com.sun.mail.**
-dontwarn com.side.data.model.**
-dontwarn com.side.data.mapper.**
-dontwarn com.side.data.entity.**
-dontwarn com.side.data.util.**
-dontwarn com.side.domain.base.BaseResponse
-dontwarn com.side.domain.utils.ResultType
-dontwarn com.side.domain.model.**
-dontwarn com.side.runwithme.model.**
-dontwarn com.side.runwithme.mapper.**
-dontwarn com.side.runwithme.base.**
-dontwarn com.side.runwithme.util.EventFlow
-dontwarn com.side.runwithme.util.EventFlowSlot
-dontwarn com.side.runwithme.util.MutableEventFlow
-dontwarn com.side.runwithme.util.ReadOnlyEventFlow
-dontwarn com.side.runwithme.util.SaveableMutableStateFlow
-dontwarn com.side.runwithme.view.join.GMailSender
-dontwarn com.side.runwithme.view.join.SendMail

-keep class com.side.runwithme.libs.** { *; }
-dontwarn com.side.runwithme.libs.**

-keep class com.naver.maps.** { *; }
-dontwarn com.naver.maps.**

-keep class com.google.android.gms.location.** { *; }


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

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response