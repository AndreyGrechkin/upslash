package com.jdefey.upsplash.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.*
import org.json.JSONException

class AuthStateManager(context: Context) {
    private val authStateScope = CoroutineScope(Dispatchers.IO)
    private val mPrefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readState(): AuthState {
        return try {
            val currentState = mPrefs.getString(KEY_STATE, null)
                ?: return AuthState()
            try {
                AuthState.jsonDeserialize(currentState)
            } catch (ex: JSONException) {
                AuthState()
            }
        } finally {
        }
    }

    fun writeState(state: AuthState?) {
        authStateScope.launch {
            try {
                val editor = mPrefs.edit()
                if (state == null) {
                    editor.remove(KEY_STATE)
                } else {
                    editor.putString(KEY_STATE, state.jsonSerializeString())
                }
                editor.apply()
            } catch (e: java.lang.Exception) {
                AuthState()
            }
        }
    }

    fun clearAuthState() {
        authStateScope.launch {
            try {
                mPrefs.getString(KEY_STATE, null)
                val editor = mPrefs.edit()
                editor.remove(KEY_STATE)
                editor.apply()
            } catch (e: Exception) {
                AuthState()
            }
        }
    }

    companion object {
        const val STORE_NAME = "AuthState"
        const val KEY_STATE = "state"
    }
}
