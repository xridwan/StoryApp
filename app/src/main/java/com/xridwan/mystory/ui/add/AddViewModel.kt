package com.xridwan.mystory.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.xridwan.mystory.datastore.UserPreferences

class AddViewModel(
    private val preferences: UserPreferences
) : ViewModel() {

    fun getToken(): LiveData<String> = preferences.getToken().asLiveData()
}