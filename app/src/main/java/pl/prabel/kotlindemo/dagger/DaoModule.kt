package pl.prabel.kotlindemo.dagger

import com.appunite.rx.dagger.NetworkScheduler
import com.appunite.rx.dagger.UiScheduler
import dagger.Module
import dagger.Provides
import pl.prabel.kotlindemo.GithubDao
import pl.prabel.kotlindemo.api.ApiService
import rx.Scheduler
import javax.inject.Singleton

@Module
class DaoModule {

    @Provides
    @Singleton
    fun provideGithhubDao(apiService: ApiService,
                          @NetworkScheduler networkScheduler: Scheduler,
                          @UiScheduler uiScheduler: Scheduler): GithubDao
            = GithubDao(apiService, networkScheduler, uiScheduler)
}

