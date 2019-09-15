package com.fieldbear.instaclonewithjetpack.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fieldbear.instaclonewithjetpack.internals.LoadingState
import com.fieldbear.instaclonewithjetpack.internals.LoadingStateEnum
import com.fieldbear.instaclonewithjetpack.internals.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginViewModel : ViewModel(){
    private val TAG = "LoginViewModel"

    val saveState = MutableLiveData<LoadingState>()


    fun login(email:String, password:String){
        val errMsg = checkValidation(email, password)
        if(errMsg != null){
            saveState.value = LoadingState(LoadingStateEnum.FAIL, errMsg)
            return
        }

        saveState.value = LoadingState(LoadingStateEnum.STARTED)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    saveState.value = LoadingState(LoadingStateEnum.SUCCESS)
                }
            }.addOnFailureListener { exception ->
                val e = exception as FirebaseAuthException
                saveState.value = LoadingState(LoadingStateEnum.FAIL, e.errorCode)
            }

    }

    fun signup(email:String, password: String){
        val errMsg = checkValidation(email, password)
        if(errMsg != null){
            saveState.value = LoadingState(LoadingStateEnum.FAIL, errMsg)
            return
        }

        saveState.value = LoadingState(LoadingStateEnum.STARTED)
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener { task ->
                saveState.value = LoadingState(LoadingStateEnum.SUCCESS)
            }.addOnFailureListener { exception ->
                val e = exception as FirebaseAuthException
                saveState.value = LoadingState(LoadingStateEnum.FAIL, e.errorCode)
            }

    }

    private fun checkValidation(email:String, password: String): String?{
        if(email.isNullOrEmpty()){
            return "Input Email Address"
        }
        if(Utils.isEmailValidate(email) == false){
            return "Invalid Email"
        }
        if(password.isNullOrEmpty()){
            return "Input Password"
        }
        if(password.length < 4){
            return "Password too short. It should be more than 4 characters long."
        }

        return null
    }

    private fun getErrorMessage(errorCode: String): String{
        when(errorCode){
            "ERROR_INVALID_EMAIL" -> return ""
            "ERROR_EMAIL_ALREADY_IN_USE" -> return ""
            "ERROR_WEAK_PASSWORD" -> return ""
            "ERROR_WRONG_PASSWORD" -> return ""
            "ERROR_USER_DISABLED" -> return ""
            "ERROR_USER_NOT_FOUND" -> return ""
            else -> return ""
        }
    }
}


//val errorCode = (task.exception as FirebaseAuthInvalidUserException).errorCode
//authListener?.onFailed(errorCode)
//"ERROR_USER_DISABLED"->{}
//"ERROR_USER_NOT_FOUND"->{}
//"ERROR_USER_TOKEN_EXPIRED"->{}
//"ERROR_INVALID_USER_TOKEN"->{}
//"ERROR_EMAIL_ALREADY_IN_USE"->{}
//"ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL"->{}
//"ERROR_CREDENTIAL_ALREADY_IN_USE"->{}