package pl.prabel.kotlindemo.dagger

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @Provides
    @ForActivity
    fun provideResources(): Resources = activity.resources

    @Provides
    @ForActivity
    fun provideContext(): Context = activity

    @Provides
    fun provideAssetManager(@ForActivity context: Context): AssetManager = context.assets

    @Provides
    fun provideLayoutInflater(@ForActivity context: Context): LayoutInflater = LayoutInflater.from(context)

    @Provides
    internal fun provideInputMethodManager(@ForActivity context: Context): InputMethodManager
            = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Provides
    internal fun provideFragmentManager(): FragmentManager = activity.supportFragmentManager
}
