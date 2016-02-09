package pl.prabel.kotlindemo.presenter

import com.appunite.rx.ResponseOrError
import pl.prabel.kotlindemo.GithubDao
import pl.prabel.kotlindemo.api.RepoModel
import rx.Observable
import javax.inject.Inject

class RepositoriesPresenter
@Inject
constructor(githubDao: GithubDao) {

    private val repositoriesErrorObservable: Observable<ResponseOrError<List<RepoModel>>>

    init {
        repositoriesErrorObservable = githubDao.repositoriesErrorObservable
    }

    fun repositoriesObservable() = repositoriesErrorObservable
            .compose(ResponseOrError.onlySuccess<List<RepoModel>>())

    fun errorObservable() = repositoriesErrorObservable
            .compose(ResponseOrError.onlyError<List<RepoModel>>())
}
