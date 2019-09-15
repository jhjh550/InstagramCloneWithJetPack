package com.fieldbear.instaclonewithjetpack.comment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fieldbear.instaclonewithjetpack.post.Post
import com.fieldbear.instaclonewithjetpack.userdata.UserData

class PostCommentsViewModelFactory (
    private val postId: String,
    private val post: Post,
    private val myData: UserData) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostCommentsViewModel(postId, post, myData) as T
    }
}