package com.fieldbear.instaclonewithjetpack.userdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.fieldbear.instaclonewithjetpack.internals.LoadingState
import com.fieldbear.instaclonewithjetpack.internals.LoadingStateEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserDataViewModel(_myData:UserData) : ViewModel() {

    val myData = MutableLiveData<UserData>()
    var nickName = _myData.nickName
    init {
        myData.value = _myData
    }

    var isExistSameNickName =  MutableLiveData<Boolean>()
    val saveState = MutableLiveData<LoadingState>()

    fun checkExistSameNickName(newNickName:String){
        nickName = newNickName
        val errMsg = checkNicknameValidation()
        if(errMsg != null){
            saveState.value = LoadingState(LoadingStateEnum.FAIL, errMsg)
            return
        }

        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("nickName", nickName)
            .get()
            .addOnSuccessListener { result ->
                var count = 0
                for(doc in result){
                    FirebaseAuth.getInstance().currentUser?.let{ if(it.uid != doc.id) count++ }
                }

                isExistSameNickName.value = count > 0
            }
    }


    fun saveProfile(){
        val errMsg = checkEditDoneValidation()
        if(errMsg != null){
            saveState.value = LoadingState(LoadingStateEnum.FAIL, errMsg)
            return
        }

        myData.value = UserData(nickName)
        saveState.value = LoadingState(LoadingStateEnum.SUCCESS)
    }

    private fun checkNicknameValidation(): String?{
        if(nickName.length <= 2){
            return "minimum length 2"
        }

        return null
    }


    private fun checkEditDoneValidation(): String?{
        if(nickName.isEmpty()){
            if(isExistSameNickName.value == null){
                return "need to check nickname exist"
            }else if(isExistSameNickName.value == true){
                return "change nickname"
            }
        }else{
            if(isExistSameNickName.value != null){
                if(isExistSameNickName.value == true){
                    return "change nickname"
                }
            }
        }

        return null
    }
}
