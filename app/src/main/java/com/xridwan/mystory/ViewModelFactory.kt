package com.xridwan.mystory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xridwan.mystory.datastore.UserPreferences
import com.xridwan.mystory.ui.add.AddViewModel
import com.xridwan.mystory.ui.authentication.AuthViewModel
import com.xridwan.mystory.ui.main.MainViewModel

class ViewModelFactory(private val pref: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}