package com.fieldbear.instaclonewithjetpack.post.add

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.fieldbear.instaclonewithjetpack.internals.LoadingState
import com.fieldbear.instaclonewithjetpack.internals.LoadingStateEnum
import com.fieldbear.instaclonewithjetpack.post.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File

class PostAddViewModel : ViewModel() {

    var resizedUri: Uri? = null
    val saveState = MutableLiveData<LoadingState>()

    fun addPost(postText:String){
        val errMsg = validateAddPost(postText)
        if(errMsg != null){
            saveState.value = LoadingState(LoadingStateEnum.FAIL, errMsg)
            return
        }

        saveState.value = LoadingState(LoadingStateEnum.STARTED)
        uploadImage().addOnFailureListener{
            saveState.value = LoadingState(LoadingStateEnum.FAIL, "Image upload failed.")
        }.addOnSuccessListener {
            savePostData(postText).addOnFailureListener {
                saveState.value = LoadingState(LoadingStateEnum.FAIL, "Image upload failed.")
            }.addOnSuccessListener {
                saveState.value = LoadingState(LoadingStateEnum.SUCCESS)
            }
        }

    }

    private fun uploadImage(): UploadTask {
        val path = getChildPathWithResizedImage()
        val ref = FirebaseStorage.getInstance().reference.child(path)
        var fileUri = Uri.fromFile(File(resizedUri!!.path))
        return ref.putFile(fileUri)
    }

    private fun getChildPathWithResizedImage():String{
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        return "postImage/${uid}/${resizedUri!!.lastPathSegment}"
    }

    private fun savePostData(postText: String): Task<DocumentReference> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val path = getChildPathWithResizedImage()
        val post = Post(uid!!, path, postText, System.currentTimeMillis())
        return FirebaseFirestore.getInstance().collection("posts").add(post)
    }


    private fun validateAddPost(postText: String): String?{
        if(resizedUri == null)
            return "There is no selected image."

        if(TextUtils.isEmpty(postText))
            return "There is no postText"

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if(uid == null)
            return "There is no user info."

        return null
    }
}
