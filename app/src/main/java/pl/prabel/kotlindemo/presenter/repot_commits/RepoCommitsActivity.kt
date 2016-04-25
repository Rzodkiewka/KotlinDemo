package pl.prabel.kotlindemo.presenter.repot_commits

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.View
import dagger.Provides
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import pl.prabel.kotlindemo.BaseActivity
import pl.prabel.kotlindemo.MainApplication
import pl.prabel.kotlindemo.R
import pl.prabel.kotlindemo.api.CommitModel
import pl.prabel.kotlindemo.api.RepoModel
import pl.prabel.kotlindemo.dagger.ActivityModule
import pl.prabel.kotlindemo.dagger.ActivitySingleton
import pl.prabel.kotlindemo.extensions.snackBar
import pl.prabel.kotlindemo.presenter.CommitsPresenter
import javax.inject.Inject

class RepoCommitsActivity : BaseActivity() {

    object ExtrasKeys {
        val repoItem = "repo_name"
    }

    @Inject
    lateinit var repoCommitsAdapter: RepoCommitsAdapter
    @Inject
    lateinit var commitsPresenter: CommitsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RepoCommitsActivityUi(repoCommitsAdapter).setContentView(this)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))

        var repoName: RepoModel? = null
        if (intent.extras != null) {
            repoName = intent.extras.getSerializable(ExtrasKeys.repoItem) as RepoModel?
        }

        commitsPresenter.commitsObservable()
                .compose(lifecycleMainObservable.bindLifecycle<List<CommitModel>>())
                .subscribe(repoCommitsAdapter)

        commitsPresenter.errorObservable()
                .compose(lifecycleMainObservable.bindLifecycle<Throwable>())
                .subscribe { snackBar("Cannot fetch repositories") }

        commitsPresenter.repoNameObserver().onNext(repoName)

    }

    fun clickAction(repoModel: CommitModel) {
        snackBar("Click on item: ${repoModel.commit.message}")
    }

    override fun inject(applicationComponent: MainApplication.MainApplicationComponent, activityModule: ActivityModule): BaseActivityComponent {
        val component: RepoCommitsActivity.Component = DaggerRepoCommitsActivity_Component.builder()
                .mainApplicationComponent(applicationComponent)
                .activityModule(activityModule)
                .module(RepoCommitsActivity.Module(this))
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
        fun inject(activity: RepoCommitsActivity)
    }

    @dagger.Module
    class Module(val activity: RepoCommitsActivity) {
        @Provides
        fun provideRepoCommitsAdapter(): RepoCommitsAdapter
                = RepoCommitsAdapter {
            activity.clickAction(it)
        }
    }
}

class RepoCommitsActivityUi(val adapterRepo: RepoCommitsAdapter) : AnkoComponent<RepoCommitsActivity> {
    override fun createView(ui: AnkoContext<RepoCommitsActivity>): View = with(ui) {
        return verticalLayout {
            lparams {
                width = matchParent;
                height = wrapContent
            }

            appBarLayout {
                toolbar {
                    id = R.id.toolbar
                    popupTheme = R.style.AppTheme
                    backgroundResource = R.color.colorPrimary
                }.lparams(width = matchParent) {
                    val tv = TypedValue()
                    if (ui.owner.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                        height = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics);
                    }
                }.setTitleTextColor(Color.WHITE)
            }.lparams(width = matchParent)
                    .lparams { width = matchParent; height = wrapContent }

            recyclerView() {
                adapter = adapterRepo
                layoutManager = LinearLayoutManager(context)
            }
        }
    }
}
