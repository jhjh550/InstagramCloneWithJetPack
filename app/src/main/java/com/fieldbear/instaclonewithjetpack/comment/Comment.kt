package com.fieldbear.instaclonewithjetpack.comment

data class Comment (
    val postId: String = "",
    val parentCommentId: String = "",
    val userId: String = "",
    val text: String = "",
    val nickName: String = "",
    val timeStamp: Long = 0

)