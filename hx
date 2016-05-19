-keepclasseswithmembernames class * {

native ;

}

-keepclasseswithmembers class * {

public (android.content.Context, android.util.AttributeSet);

}

-keepclasseswithmembers class * {

public (android.content.Context, android.util.AttributeSet, int);

}

-keepclassmembers class * extends android.app.Activity {

public void *(android.view.View);

}

-keepclassmembers enum * {

public static **[] values();

public static ** valueOf(java.lang.String);

}

-keep class * implements android.os.Parcelable {

public static final android.os.Parcelable$Creator *;

}

-libraryjars libs/umeng_sdk.jar

-keepclassmembers class * {

public (org.json.JSONObject);

}

-keep public class com.smile.android.R$*{

public static final int *;

}

-libraryjars libs/gson-2.2.2.jar

-keep class sun.misc.Unsafe { ; } 

-keep class com.google.gson.stream.* { ; } 

-keep class com.google.gson.examples.android.model.* { ; } 

-keep class com.google.gson.* { *;}

-libraryjars libs/android-support-v4.jar

-dontwarn android.support.v4.**



-keep class android.support.v4.** { *; } 

-libraryjars /libs/nineoldandroids-2.4.0.jar

-dontwarn com.nineoldandroids.**

-keep class com.nineoldandroids.** { *;}