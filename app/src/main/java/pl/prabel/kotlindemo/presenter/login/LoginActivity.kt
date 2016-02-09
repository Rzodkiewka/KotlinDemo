package pl.prabel.kotlindemo.presenter.login

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.appunite.rx.functions.BothParams
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.activity_login.*
import pl.prabel.kotlindemo.BaseActivity
import pl.prabel.kotlindemo.MainApplication
import pl.prabel.kotlindemo.R
import pl.prabel.kotlindemo.content.TokenPreferences
import pl.prabel.kotlindemo.dagger.ActivityModule
import pl.prabel.kotlindemo.dagger.ActivitySingleton
import pl.prabel.kotlindemo.extensions.hide
import pl.prabel.kotlindemo.extensions.show
import pl.prabel.kotlindemo.extensions.snackBar
import pl.prabel.kotlindemo.presenter.MainActivity
import javax.inject.Inject

class LoginActivity : BaseActivity() {

    @Inject
    lateinit var presenter: LoginPresenter
    @Inject
    lateinit var tokenPrefs: TokenPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (tokenPrefs.isLogin()) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
            return
        }

        RxView.clicks(login_button)
                .compose(lifecycleMainObservable.bindLifecycle<Any>())
                .map { ignore ->
                    BothParams<String, String>(
                            username_edit_text.text.toString(),
                            password_edit_text.text.toString())
                }
                .doOnNext { progress_view.show() }
                .subscribe(presenter.loginObserver())

        presenter.successResponseObservable()
                .compose(lifecycleMainObservable.bindLifecycle<Any>())
                .subscribe {
                    progress_view.hide()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }

        presenter.errorResponseObservable()
                .compose(lifecycleMainObservable.bindLifecycle<Any>())
                .subscribe {
                    progress_view.hide()
                    snackBar("Bad credentials", container)
                }
    }

    override fun inject(applicationComponent: MainApplication.MainApplicationComponent, activityModule: ActivityModule): BaseActivityComponent {
        val component: Component = DaggerLoginActivity_Component.builder()
                .activityModule(activityModule)
                .mainApplicationComponent(applicationComponent)
                .build()
        component.inject(this)
        return component
    }

    @ActivitySingleton
    @dagger.Component(
            dependencies = arrayOf(MainApplication.MainApplicationComponent::class),
            modules = arrayOf(ActivityModule::class)
    )
    interface Component : BaseActivityComponent {
        fun inject(activity: LoginActivity)
    }
}