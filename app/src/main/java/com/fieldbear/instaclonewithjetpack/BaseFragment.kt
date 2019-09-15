package com.fieldbear.instaclonewithjetpack

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fieldbear.instaclonewithjetpack.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

open class BaseFragment : Fragment(){

    open fun myCallback(requestCode: Int, resultCode: Int, data: Intent?){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).onActivityCallback = ::myCallback
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).onActivityCallback = null
    }

    fun moveHomeFragment(){
        (activity as MainActivity).bottom_nav.selectedItemId = R.id.navigation_home
    }

    fun showProgressDlg(){
        (activity as MainActivity).myProgress.show()
    }

    fun dismissProgressDlg(){
        (activity as MainActivity).myProgress.dismiss()
    }

}