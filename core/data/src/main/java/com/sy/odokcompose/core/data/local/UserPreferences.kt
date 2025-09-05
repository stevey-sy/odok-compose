package com.sy.odokcompose.core.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    
    private val _loginStatusFlow = MutableStateFlow(isUserLoggedIn())
    val loginStatusFlow: Flow<Boolean> = _loginStatusFlow.asStateFlow()
    
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
        _loginStatusFlow.value = isLoggedIn
    }
    
    companion object {
        private const val PREFERENCES_NAME = "odok_preferences"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}