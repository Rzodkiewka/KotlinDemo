package pl.prabel.kotlindemo

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.appunite.rx.dagger.NetworkScheduler
import com.appunite.rx.dagger.UiScheduler
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.Component
import pl.prabel.kotlindemo.api.ApiService
import pl.prabel.kotlindemo.content.TokenPreferences
import pl.prabel.kotlindemo.dagger.AppModule
import pl.prabel.kotlindemo.dagger.DaoModule
import pl.prabel.kotlindemo.dagger.ForApplication
import rx.Scheduler
import javax.inject.Singleton

public class MainApplication : MultiDexApplication() {

    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit public var applicationComponent: MainApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerMainApplication_MainApplicationComponent.builder()
                .appModule(AppModule(this))
                .build()
        applicationComponent.inject(this)
    }

    @Singleton
    @Component(modules = arrayOf(
            AppModule::class,
            DaoModule::class))
    public interface MainApplicationComponent {
        fun inject(application: MainApplication)

        @ForApplication
        fun context(): Context

        fun picasso(): Picasso

        fun apiService(): ApiService

        @NetworkScheduler
        fun networkSchedule(): Scheduler

        @UiScheduler
        fun uiSchedule(): Scheduler

        fun gson(): Gson

        fun tokenPreferences(): TokenPreferences

        fun githubDao(): GithubDao
    }
}