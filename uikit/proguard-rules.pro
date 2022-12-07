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
#-renamesourcefileattribute SourceFile


-keepclasseswithmembernames class * {
    native <methods>;
}


-keepclassmembers enum * {
  *;
}

-keepnames class * implements java.io.Serializable


-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    protected <methods>;
    protected <fields>;
}

-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-keep class * extends androidx.fragment.app.DialogFragment




-keepattributes Signature
-keepattributes Exceptions, InnerClasses

-keep class com.zegocloud.uikit.utils.**{*;}
-keep class com.zegocloud.uikit.service.defines.**{*;}
-keep class com.zegocloud.uikit.components.audiovideo.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}
-keep class com.zegocloud.uikit.components.audiovideocontainer.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}
-keep class com.zegocloud.uikit.components.chat.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}
-keep class com.zegocloud.uikit.components.common.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}
-keep class com.zegocloud.uikit.components.memberlist.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}
-keep class com.zegocloud.uikit.components.message.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}
-keep class com.zegocloud.uikit.components.notice.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}

-keep class com.zegocloud.uikit.plugin.**{
  public <methods>;
  public <fields>;
  protected <methods>;
  protected <fields>;
}

-keep class com.zegocloud.uikit.ZegoUIKit{
  public *;
}

-keep class com.zegocloud.uikit.ZegoUIKit$* {
    *;
}

-keep class com.zegocloud.uikit.service.internal.UIKitCore{
  public <methods>;
}

-keep class com.zegocloud.uikit.service.internal.UIKitCoreUser{
  *;
}
-keep class com.zegocloud.uikit.components.internal.RippleIconView{
  public <methods>;
}

-keep class im.zego.**{*;}
-keep class com.zego.**{*;}

