package com.fieldbear.instaclonewithjetpack

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.fieldbear.instaclonewithjetpack.R


class MyProgress(context: Context) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress);
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }
}