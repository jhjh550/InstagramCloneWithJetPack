package com.fieldbear.instaclonewithjetpack.internals

enum class LoadingStateEnum {
    NONE, STARTED, SUCCESS, FAIL
}

data class LoadingState(
    var state: LoadingStateEnum = LoadingStateEnum.NONE,
    var msg: String = "" )
