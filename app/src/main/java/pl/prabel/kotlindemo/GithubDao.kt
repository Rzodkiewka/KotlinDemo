package pl.prabel.kotlindemo

import com.appunite.rx.ObservableExtensions
import com.appunite.rx.ResponseOrError
import com.appunite.rx.dagger.NetworkScheduler
import com.appunite.rx.dagger.UiScheduler
import com.appunite.rx.operators.MoreOperators
import pl.prabel.kotlindemo.api.ApiService
import pl.prabel.kotlindemo.api.CommitModel
import pl.prabel.kotlindemo.api.RepoModel
import rx.Observable
import rx.Observer
import rx.Scheduler
import rx.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubDao
@Inject
constructor(val apiService: ApiService,
            @NetworkScheduler val networkScheduler: Scheduler,
            @UiScheduler val uiScheduler: Scheduler) {

    val repositoriesErrorObservable: Observable<ResponseOrError<List<RepoModel>>>
    val repositoriesCommitsErrorObservable: Observable<ResponseOrError<List<CommitModel>>>
    val commitNameSubject: PublishSubject<RepoModel> = PublishSubject.create()

    init {
        repositoriesErrorObservable = apiService.getRepositories()
                .subscribeOn(networkScheduler)
                .observeOn(uiScheduler)
                .compose(ResponseOrError.toResponseOrErrorObservable<List<RepoModel>>())
                .compose(MoreOperators.repeatOnError<List<RepoModel>>(networkScheduler))
                .compose(MoreOperators.cacheWithTimeout<ResponseOrError<List<RepoModel>>>(networkScheduler))
                .compose(ObservableExtensions.behaviorRefCount<ResponseOrError<List<RepoModel>>>());

        repositoriesCommitsErrorObservable =
                commitNameSubject.switchMap {
                    repoModel ->
                    apiService.getRepoCommits(repoModel.owner.login, repoModel.name)
                            .subscribeOn(networkScheduler)
                            .observeOn(uiScheduler)
                            .compose(ResponseOrError.toResponseOrErrorObservable<List<CommitModel>>())
                }
                        .compose(MoreOperators.repeatOnError<List<CommitModel>>(networkScheduler))
                        .compose(MoreOperators.cacheWithTimeout<ResponseOrError<List<CommitModel>>>(networkScheduler))
                        .compose(ObservableExtensions.behaviorRefCount<ResponseOrError<List<CommitModel>>>());
    }

    fun commitNameObserver(): Observer<RepoModel> {
        return commitNameSubject
    }
}

