# KMMFile
KMMFile is an utility library helps applications working with File System (Android/iOS) in Kotlin Multiplatform.

I publish all of our libraries to `mavenCentral()`, you can add it to your project
```
repositories {
   mavenCentral()
}
...
commonMain by getting {
    dependencies {
        implementation 'io.github.helios:kmm-filesystem:1.0.0'
    }
}
```

## Quick Guide
### Android
Make sure you have provided the application context for FileSystem by calling method `init(Context context)`, for example:

```
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // provide FileSystem application context
        FileSystem.init(this)
    }
}
```
