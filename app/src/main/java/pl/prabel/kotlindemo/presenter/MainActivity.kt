package pl.prabel.kotlindemo.presenter

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import dagger.Provides
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import pl.prabel.kotlindemo.BaseActivity
import pl.prabel.kotlindemo.MainApplication
import pl.prabel.kotlindemo.R
import pl.prabel.kotlindemo.api.RepoModel
import pl.prabel.kotlindemo.dagger.ActivityModule
import pl.prabel.kotlindemo.dagger.ActivitySingleton
import pl.prabel.kotlindemo.extensions.snackBar
import pl.prabel.kotlindemo.presenter.repot_commits.RepoCommitsActivity
import javax.inject.Inject

class MainActivity : BaseActivity(){

    @Inject
    lateinit var repositoriesAdapter: RepositoriesAdapter
    @Inject
    lateinit var repositoriesPresenter: RepositoriesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = repositoriesAdapter

        repositoriesPresenter.repositoriesObservable()
                .compose(lifecycleMainObservable.bindLifecycle<List<RepoModel>>())
                .subscribe(repositoriesAdapter)

        repositoriesPresenter.errorObservable()
                .compose(lifecycleMainObservable.bindLifecycle<Throwable>())
                .subscribe{ snackBar("Cannot fetch repositories")}
    }

    fun clickAction(repoModel: RepoModel) {
        startActivity<RepoCommitsActivity>(RepoCommitsActivity.ExtrasKeys.repoItem to repoModel)
    }

    override fun inject(applicationComponent: MainApplication.MainApplicationComponent, activityModule: ActivityModule): BaseActivityComponent {
        val component: Component = DaggerMainActivity_Component.builder()
                .mainApplicationComponent(applicationComponent)
                .activityModule(activityModule)
                .module(Module(this))
                .build()
        component.inject(this)
        return component
    }

    @ActivitySingleton
    @dagger.Component(
            dependencies = arrayOf(MainApplication.MainApplicationComponent::class),
            modules = arrayOf(
                    ActivityModule::class,
                    Module::class)
    )
    interface Component : BaseActivityComponent {
        fun inject(activity: MainActivity)
    }
    @dagger.Module
    class Module(val activity: MainActivity) {

        @Provides
        fun provideRepoAdapter(): RepositoriesAdapter
                = RepositoriesAdapter {
            activity.clickAction(it)
        }
    }
}
