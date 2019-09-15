package com.fieldbear.instaclonewithjetpack.comment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fieldbear.instaclonewithjetpack.comment.Comment
import com.fieldbear.instaclonewithjetpack.post.Post
import com.fieldbear.instaclonewithjetpack.userdata.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot

class PostCommentsViewModel(private val postId:String,
                            val post: Post,
                            private val myData: UserData
) : ViewModel() {



    private var _commentsDataList: MutableLiveData<ArrayList<QueryDocumentSnapshot>> = MutableLiveData()
    val commentList: LiveData<ArrayList<QueryDocumentSnapshot>>
        get() = _commentsDataList


    private val listenerRegistration: ListenerRegistration
    override fun onCleared() {
        super.onCleared()
        listenerRegistration.remove()
    }

    init{
        // realtime update
        listenerRegistration = FirebaseFirestore.getInstance().collection("comments")
            .orderBy("timeStamp")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    return@addSnapshotListener
                }
                querySnapshot?.let{
                    val list = ArrayList<QueryDocumentSnapshot>()
                    for(doc in querySnapshot){
                        list.add(doc)
                    }
                    _commentsDataList.value = list
                }

            }
    }

    /*
    val postId: String = "",
    val parentCommentId: String = "",
    val userId: String = "",
    val text: String = "",
    val nickName: String = "",
    val timeStamp: Long = 0
     */
    fun saveComment(commentText:String){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let{
            val comment = Comment(
                postId, "", uid, commentText, myData.nickName,
                System.currentTimeMillis())

            FirebaseFirestore.getInstance().collection("comments").add(comment)
                .addOnCompleteListener {

                }
        }
    }
}
