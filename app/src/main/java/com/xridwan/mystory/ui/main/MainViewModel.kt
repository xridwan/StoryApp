package com.xridwan.mystory.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.xridwan.mystory.datastore.UserPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: UserPreferences) : ViewModel() {

    fun getToken(): LiveData<String> = preferences.getToken().asLiveData()

    fun logout() {
        viewModelScope.launch {
            preferences.logout()
        }
    }
}