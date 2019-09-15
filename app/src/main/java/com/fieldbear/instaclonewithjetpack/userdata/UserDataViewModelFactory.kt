package com.fieldbear.instaclonewithjetpack.userdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserDataViewModelFactory (private val myData:UserData) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserDataViewModel(myData) as T
    }
}