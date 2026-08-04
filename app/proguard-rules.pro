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

# Glance renders widgets through WorkManager (transitive dependency of
# glance-appwidget). work-runtime 2.7.1 ships the consumer rule
# "-keep class * extends androidx.work.InputMerger" without a member spec, and
# R8 full mode (default since AGP 8) strips the no-arg constructor of kept
# classes, so Class.newInstance() fails with InstantiationException, Glance's
# SessionWorker never runs and the widget stays on the loading placeholder
# forever in release builds. Keep the reflective constructors explicitly.
-keepclassmembers class * extends androidx.work.InputMerger {
    <init>();
}

# Same R8 full-mode pattern: glance-appwidget 1.1.1 only keeps ActionCallback
# classes, not their constructors, and Glance instantiates them by reflection
# when the widget is tapped.
-keepclassmembers class * extends androidx.glance.appwidget.action.ActionCallback {
    <init>();
}