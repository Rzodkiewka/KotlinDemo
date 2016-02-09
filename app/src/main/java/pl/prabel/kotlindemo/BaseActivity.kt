package pl.prabel.kotlindemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.trello.rxlifecycle.ActivityEvent
import pl.prabel.kotlindemo.dagger.ActivityModule

import pl.prabel.kotlindemo.rx.LifecycleMainObservable

import rx.subjects.BehaviorSubject

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit public var component: BaseActivityComponent
    }

    private val lifecycleSubject = BehaviorSubject.create<ActivityEvent>()
    protected val lifecycleMainObservable = LifecycleMainObservable(
            LifecycleMainObservable.LifecycleProviderActivity(lifecycleSubject))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val applicationComponent = MainApplication.applicationComponent
        val activityModule = ActivityModule(this)

        component = inject(applicationComponent, activityModule)

        lifecycleSubject.onNext(ActivityEvent.CREATE)
    }

    override fun onStart() {
        super.onStart()

        lifecycleSubject.onNext(ActivityEvent.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(ActivityEvent.RESUME)
    }

    override fun onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE)
        super.onPause()
    }

    override fun onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP)
        super.onStop()
    }

    override fun onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
        super.onDestroy()
    }

    abstract fun inject(applicationComponent: MainApplication.MainApplicationComponent,
                        activityModule: ActivityModule): BaseActivityComponent



    public interface BaseActivityComponent {

    }
}
