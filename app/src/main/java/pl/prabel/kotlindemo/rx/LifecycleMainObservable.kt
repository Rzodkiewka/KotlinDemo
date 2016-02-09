package pl.prabel.kotlindemo.rx

import com.appunite.rx.internal.Preconditions.checkNotNull
import com.trello.rxlifecycle.ActivityEvent
import com.trello.rxlifecycle.FragmentEvent
import com.trello.rxlifecycle.RxLifecycle
import rx.Observable
import javax.inject.Inject

class LifecycleMainObservable
@Inject
constructor(lifecycleProvider: LifecycleMainObservable.LifecycleProvider) {

    private val lifecycleProvider: LifecycleProvider

    interface LifecycleProvider {

        fun <T> bindLifecycle(): Observable.Transformer<T, T>
    }

    class LifecycleProviderFragment(lifecycle: Observable<FragmentEvent>) : LifecycleProvider {

        private val lifecycle: Observable<FragmentEvent>

        init {
            this.lifecycle = checkNotNull(lifecycle)
        }


        override fun <T> bindLifecycle(): Observable.Transformer<T, T> {
            return RxLifecycle.bindFragment<T>(lifecycle)
        }
    }

    class LifecycleProviderActivity(lifecycle: Observable<ActivityEvent>) : LifecycleProvider {

        private val lifecycle: Observable<ActivityEvent>

        init {
            this.lifecycle = checkNotNull(lifecycle)
        }

        override fun <T> bindLifecycle(): Observable.Transformer<T, T> {
            return RxLifecycle.bindActivity<T>(lifecycle)
        }
    }

    init {
        this.lifecycleProvider = checkNotNull(lifecycleProvider)
    }

    fun <T> bindLifecycle(): Observable.Transformer<T, T> {
        return lifecycleProvider.bindLifecycle<T>()
    }
}
