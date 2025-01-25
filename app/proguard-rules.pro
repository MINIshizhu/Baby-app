# 保留 Compose 相关类
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# 保留 Room 实体类
-keep class com.example.babycare.data.entity.** { *; }

# 保留 Hilt 相关类
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# 保留 Kotlin 序列化
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# 保留 Retrofit 接口
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# 保留自定义异常
-keep class com.example.babycare.exception.** { *; }

# 保留 Android Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留 R 文件
-keep class **.R$* {
    public static <fields>;
}

# 移除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
} 