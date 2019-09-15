package com.fieldbear.instaclonewithjetpack.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot

class MainViewModel : ViewModel(){
    private var _postDataList: MutableLiveData<ArrayList<QueryDocumentSnapshot>> = MutableLiveData()
    val postDataList: LiveData<ArrayList<QueryDocumentSnapshot>>
        get() = _postDataList

    init {
        FirebaseFirestore.getInstance().collection("posts")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val list = ArrayList<QueryDocumentSnapshot>()
                for(doc in documents){
                    list.add(doc)
                }
                _postDataList.value = list
            }
    }
}