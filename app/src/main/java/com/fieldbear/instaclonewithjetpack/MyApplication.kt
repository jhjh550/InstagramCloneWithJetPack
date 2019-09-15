package com.fieldbear.instaclonewithjetpack

import android.app.Application
import android.content.Intent
import android.widget.Toast
import com.fieldbear.instaclonewithjetpack.main.MainActivity
import com.fieldbear.instaclonewithjetpack.userdata.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class MyApplication : Application(){

    private var myData: UserData? = null

    override fun onCreate() {
        super.onCreate()
        val auth = FirebaseAuth.getInstance()
    }

    fun saveMyData(myData: UserData){
        this.myData = myData
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).set(myData)
        }
    }

    fun getMyData(callback:(myData:UserData?)->Unit){
        if(myData != null){
            callback(myData)
        }else {
            FirebaseAuth.getInstance().currentUser?.uid?.let { id->
                FirebaseFirestore.getInstance().collection("users").document(id).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result != null) {
                                myData = task.result?.toObject(UserData::class.java)
                            }
                        }
                        callback(myData)
                }
            }
        }
    }
}