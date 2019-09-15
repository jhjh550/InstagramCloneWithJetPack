package com.fieldbear.instaclonewithjetpack.post.add

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.fieldbear.instaclonewithjetpack.BaseFragment

import com.fieldbear.instaclonewithjetpack.R
import com.fieldbear.instaclonewithjetpack.internals.LoadingStateEnum
import com.fieldbear.instaclonewithjetpack.internals.Utils
import com.fieldbear.instaclonewithjetpack.main.MainActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_post_add.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostAddFragment : BaseFragment() {

    private lateinit var viewModel: PostAddViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_add, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PostAddViewModel::class.java)

        bindUI()
    }

    private fun bindUI(){
        selectImage()

        viewModel.saveState.observe(this, Observer {
            when(it.state){
                LoadingStateEnum.FAIL->{
                    dismissProgressDlg()
                    Toast.makeText(activity, "posting failed for ${it.msg}", Toast.LENGTH_LONG).show()
                }
                LoadingStateEnum.SUCCESS->{
                    dismissProgressDlg()
                    Toast.makeText(activity, "Posting success!", Toast.LENGTH_LONG).show()
                    moveHomeFragment()
                }
                LoadingStateEnum.STARTED->{
                    showProgressDlg()
                    Utils.hideKeyboard(activity as Activity)
                }
            }
        })

        postImageView.setOnClickListener {
            selectImage()
        }
        btnAddComment.setOnClickListener {
            viewModel.addPost(editTextComment.text.toString())
        }
    }



    private fun selectImage(){
        activity?.let {
            val permissions = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
            (activity as MainActivity).checkRuntimePermission(permissions){
                TedBottomPicker.with(it).show { uri ->
                    CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(it)
                }
            }
        }
    }


    override fun myCallback(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                postImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                Glide.with(this).load(resultUri).into(postImageView)
                CoroutineScope(Dispatchers.Main).launch {
                    context?.let{
                        viewModel.resizedUri = Utils.getResizedImage(it, resultUri)
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                // todo : notify crop error
            }
        }
    }


}
