package pl.prabel.kotlindemo.dagger

import android.app.Application
import android.content.Context
import com.appunite.rx.android.MyAndroidSchedulers
import com.appunite.rx.dagger.NetworkScheduler
import com.appunite.rx.dagger.UiScheduler
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.okhttp.Cache
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.logging.HttpLoggingInterceptor
import com.squareup.picasso.LruCache
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import pl.prabel.kotlindemo.BuildConfig
import pl.prabel.kotlindemo.api.ApiService
import pl.prabel.kotlindemo.content.AppConsts
import pl.prabel.kotlindemo.content.TokenPreferences
import pl.prabel.kotlindemo.gson.AndroidUnderscoreNamingStrategy
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import rx.Scheduler
import rx.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    @ForApplication
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideCache(@ForApplication context: Context): Cache = Cache(File(context.cacheDir, "cache"), 150 * 1024 * 1024)

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, tokenPreferences: TokenPreferences): OkHttpClient {
        val okHttpClient = OkHttpClient()
        okHttpClient.cache = cache
        okHttpClient.setConnectTimeout(5, TimeUnit.MINUTES)
        okHttpClient.setWriteTimeout(5, TimeUnit.MINUTES)
        okHttpClient.setReadTimeout(30, TimeUnit.MINUTES)

        val interceptors = okHttpClient.interceptors()

        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        interceptors.add(Interceptor { chain ->
            val authToken = tokenPreferences.token
            val builder = chain.request().newBuilder()
            builder.addHeader("Accept", "application/vnd.github.v3+json")
            if (authToken != null) {
                builder.addHeader("Authorization", authToken)
            }
            chain.proceed(builder.build())
        })
        interceptors.add(loggingInterceptor)

        return okHttpClient
    }

    @Provides
    fun providePicasso(@ForApplication context: Context, okHttpClient: OkHttpClient): Picasso
            = Picasso.Builder(context)
            .indicatorsEnabled(BuildConfig.DEBUG)
            .memoryCache(LruCache(context))
            .loggingEnabled(BuildConfig.DEBUG)
            .downloader(OkHttpDownloader(okHttpClient))
            .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder()
            .setFieldNamingStrategy(AndroidUnderscoreNamingStrategy())
            .create()

    @Provides
    fun provideRetrofit(gson: Gson,
                        okHttpClient: OkHttpClient): Retrofit
            = Retrofit.Builder()
            .baseUrl(AppConsts.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    @Provides
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Provides
    @UiScheduler
    fun provideUiScheduler(): Scheduler = MyAndroidSchedulers.mainThread()

    @Provides
    @NetworkScheduler
    fun provideNetworkScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    fun provideTokenPreferences(@ForApplication context: Context): TokenPreferences
            = TokenPreferences(context)
}
