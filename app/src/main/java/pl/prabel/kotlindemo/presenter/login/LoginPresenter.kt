package pl.prabel.kotlindemo.presenter.login

import android.util.Base64
import com.appunite.rx.ResponseOrError
import com.appunite.rx.dagger.NetworkScheduler
import com.appunite.rx.dagger.UiScheduler
import com.appunite.rx.functions.BothParams
import pl.prabel.kotlindemo.api.ApiService
import pl.prabel.kotlindemo.content.TokenPreferences
import rx.Observable
import rx.Scheduler
import rx.subjects.PublishSubject
import javax.inject.Inject

class LoginPresenter
@Inject
constructor(apiService: ApiService,
            @NetworkScheduler networkScheduler: Scheduler,
            @UiScheduler uiScheduler: Scheduler,
            val tokenPreferences: TokenPreferences) {

    private val loginErrorObservable: Observable<ResponseOrError<Any>>
    private val loginObserver = PublishSubject.create<BothParams<String, String>>()

    init {
        loginErrorObservable = loginObserver
                .map { userNameWithPassword ->
                    tokenFromBothParamsCredentials(userNameWithPassword)
                }
                .doOnNext { token -> tokenPreferences.edit().setToken(token).commit() }
                .flatMap { token ->
                    apiService.authorizations()
                            .subscribeOn(networkScheduler)
                            .observeOn(uiScheduler)
                            .compose(ResponseOrError.toResponseOrErrorObservable<Any>())
                }
    }

    fun tokenFromBothParamsCredentials(userNameWithPassword: BothParams<String, String>): String {
        val credentials: String = userNameWithPassword.param1() + ":" + userNameWithPassword.param2()
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }

    fun successResponseObservable() = loginErrorObservable
            .compose(ResponseOrError.onlySuccess())

    fun errorResponseObservable() = loginErrorObservable
            .compose(ResponseOrError.onlyError())

    fun loginObserver() = loginObserver
}