package com.fieldbear.instaclonewithjetpack.userdata

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.fieldbear.instaclonewithjetpack.BaseFragment
import com.fieldbear.instaclonewithjetpack.MyApplication
import com.fieldbear.instaclonewithjetpack.R
import com.fieldbear.instaclonewithjetpack.internals.GlideApp
import com.fieldbear.instaclonewithjetpack.internals.LoadingStateEnum
import com.fieldbear.instaclonewithjetpack.internals.Utils
import com.fieldbear.instaclonewithjetpack.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_userdata.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class UserDataFragment : BaseFragment() {

    private lateinit var viewModel: UserDataViewModel
    private lateinit var viewModelFactory: UserDataViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_userdata, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.apply{
            (application as MyApplication).getMyData { myData ->
                if(myData == null){
                    Toast.makeText(this, "유저 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }else{
                    viewModelFactory = UserDataViewModelFactory(myData)

                    viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserDataViewModel::class.java)
                    bindUI()
                    initUI()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as MainActivity).apply{
            bottom_nav.visibility = View.VISIBLE
        }
    }

    private fun initUI(){
        editTextNickName.setText(viewModel.nickName)
        setProfileImage()

        btnConfirmNickName.setOnClickListener {
            viewModel.checkExistSameNickName(editTextNickName.text.toString())
        }
        btnUserInfoEditDone.setOnClickListener {
            viewModel.saveProfile()
        }
        btnProfileImage.setOnClickListener {
            selectImage()
        }
    }

    private fun bindUI(){
        viewModel.isExistSameNickName.observe(this, Observer {
            it?.let{
                Toast.makeText(activity, "exist same nickName ${it}", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.myData.observe(this, Observer { myData ->
            (activity?.application as MyApplication).saveMyData(myData)
        })

        viewModel.saveState.observe(this, Observer {
            when(it.state){
                LoadingStateEnum.FAIL -> {
                    dismissProgressDlg()
                    Toast.makeText(activity, it.msg, Toast.LENGTH_LONG).show()
                }
                LoadingStateEnum.SUCCESS -> {
                    dismissProgressDlg()
                    moveHomeFragment()
                }
                LoadingStateEnum.STARTED->{
                    showProgressDlg()
                }
            }
        })
    }

    private fun selectImage(){
        activity?.let {
            TedBottomPicker.with(it).show{uri ->
                CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(it)
            }
        }
    }

    override fun myCallback(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                Glide.with(this).load(resultUri).into(imageProfile)
                CoroutineScope(Dispatchers.Main).launch {
                    context?.let{
                        val resizedUri = Utils.getResizedImage(it, resultUri)
                        uploadProfileImage(resizedUri)
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                // todo : notify crop error
            }
        }
    }

    private fun setProfileImage(){
        val ref = getImageProfileRef()

        ref?.let{
            context?.let{
                GlideApp.with(it).load(ref).into(imageProfile)
            }
        }
    }

    private fun uploadProfileImage(uri: Uri){
        val ref = getImageProfileRef()
        ref?.let{
            var fileUri = Uri.fromFile(File(uri.path))
            ref.putFile(fileUri)
                .addOnSuccessListener { Log.d("UserDefaultFragment", "image upload success") }
                .addOnFailureListener { Log.d("UserDefaultFragment", "image upload failed") }
        }
    }

    private fun getImageProfileRef(): StorageReference? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            return FirebaseStorage.getInstance().reference
                .child("profileImage/${uid}.jpg")

        }
        return null
    }
}
