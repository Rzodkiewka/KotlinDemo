package pl.prabel.kotlindemo.presenter

import com.appunite.rx.ResponseOrError
import pl.prabel.kotlindemo.GithubDao
import pl.prabel.kotlindemo.api.CommitModel
import rx.Observable
import javax.inject.Inject

class CommitsPresenter
@Inject
constructor(githubDao: GithubDao) {

    private val commitsErrorObservable: Observable<ResponseOrError<List<CommitModel>>>
    val githubDao = githubDao

    init {
        commitsErrorObservable = githubDao.repositoriesCommitsErrorObservable
    }

    fun commitsObservable() = commitsErrorObservable
            .compose(ResponseOrError.onlySuccess<List<CommitModel>>())

    fun errorObservable() = commitsErrorObservable
            .compose(ResponseOrError.onlyError<List<CommitModel>>())

    fun repoNameObserver() = githubDao.commitNameObserver()

}
