package com.fieldbear.instaclonewithjetpack.post

import java.io.Serializable

data class Post(
    val userId: String = "",
    val imagePath: String = "",
    val text: String = "",
    val timeStamp: Long = 0
): Serializable