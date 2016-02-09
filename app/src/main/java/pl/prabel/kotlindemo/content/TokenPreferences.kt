package pl.prabel.kotlindemo.content

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.common.base.Strings

class TokenPreferences
constructor(context: Context) {

    class TokenPreferencesEditor
    @SuppressLint("CommitPrefEdits")
    internal constructor(preferences: SharedPreferences) {

        private val mEditor: SharedPreferences.Editor

        init {
            mEditor = preferences.edit()
        }

        fun commit(): Boolean {
            return mEditor.commit()
        }

        fun clear(): TokenPreferencesEditor {
            mEditor.clear()
            return this
        }

        fun setToken(token: String): TokenPreferencesEditor {
            mEditor.putString(PREFS_TOKEN, token)
            return this
        }
    }

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, 0)
    }

    fun edit(): TokenPreferencesEditor {
        return TokenPreferencesEditor(preferences)
    }

    fun isLogin(): Boolean = !Strings.isNullOrEmpty(token);

    val token: String?
        get() = preferences.getString(PREFS_TOKEN, null)

    companion object {
        private val PREFERENCES_NAME = "github_prefs"

        val PREFS_TOKEN = "prefs_token"
    }
}

