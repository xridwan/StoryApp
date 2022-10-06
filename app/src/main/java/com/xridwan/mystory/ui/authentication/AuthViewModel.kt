package com.xridwan.mystory.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.xridwan.mystory.datastore.UserPreferences
import kotlinx.coroutines.launch

class AuthViewModel(private val preferences: UserPreferences) : ViewModel() {

    fun isLogin(): LiveData<Boolean> = preferences.isLogin().asLiveData()

    fun login() {
        viewModelScope.launch {
            preferences.login()
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.setToken(token)
        }
    }
}