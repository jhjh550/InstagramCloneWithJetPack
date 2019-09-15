package com.fieldbear.instaclonewithjetpack.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fieldbear.instaclonewithjetpack.MyApplication
import com.fieldbear.instaclonewithjetpack.MyProgress
import com.fieldbear.instaclonewithjetpack.R

import com.fieldbear.instaclonewithjetpack.internals.LoadingStateEnum
import com.fieldbear.instaclonewithjetpack.internals.Utils
import com.fieldbear.instaclonewithjetpack.main.MainActivity
import com.fieldbear.instaclonewithjetpack.userdata.UserData
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){

    private lateinit var myProgress: MyProgress
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        myProgress = MyProgress(this)


        viewModel.saveState.observe(this, Observer {
            when(it.state){
                LoadingStateEnum.FAIL->{
                    myProgress.dismiss()
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }
                LoadingStateEnum.SUCCESS->{
                    myProgress.dismiss()
                    (application as MyApplication).saveMyData(UserData())
                    startActivity(Intent(this, MainActivity::class.java))
                }
                LoadingStateEnum.STARTED->{
                    myProgress.show()
                    Utils.hideKeyboard(this)

                }
            }
        })

        btnLogin.setOnClickListener {
            viewModel.login(editTextEmail.text.toString(), editTextPassword.text.toString())
        }

        btnSingup.setOnClickListener {
            viewModel.signup(editTextEmail.text.toString(), editTextPassword.text.toString())
        }
    }


}