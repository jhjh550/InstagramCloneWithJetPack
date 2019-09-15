package com.fieldbear.instaclonewithjetpack.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import com.fieldbear.instaclonewithjetpack.MyApplication
import com.fieldbear.instaclonewithjetpack.MyProgress
import com.fieldbear.instaclonewithjetpack.R
import com.fieldbear.instaclonewithjetpack.internals.OnActivityResultType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQ_CODE = 101
    private lateinit var navController: NavController
    var onActivityCallback: OnActivityResultType? = null
    var onPermissionCallback:(()->Unit)? = null

    lateinit var myProgress: MyProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavController()
        setupMyData()
        myProgress = MyProgress(this)
    }




    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun setupMyData(){
        (application as MyApplication).getMyData {
            if(it == null){
                Toast.makeText(this, "유저 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                finish()
            }else {
                if (TextUtils.isEmpty(it.nickName)) {
                    bottom_nav.selectedItemId = R.id.navigation_profile
                    bottom_nav.visibility = View.GONE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setHomeButtonEnabled(false)
                }
            }
        }
    }

    private fun setupNavController(){
        navController = findNavController(R.id.navigationHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        bottom_nav.let{
            NavigationUI.setupWithNavController(it, navController)
            it.setOnNavigationItemReselectedListener {
                // to nothing
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityCallback?.let { it(requestCode, resultCode, data) }
    }


    fun checkRuntimePermission(permissions:Array<String>, callback:()->Unit){
        val granted = ContextCompat.checkSelfPermission(this, permissions[0])
        if(granted != PackageManager.PERMISSION_GRANTED){
            onPermissionCallback = callback
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQ_CODE)
        }else{
            callback()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_REQ_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }else{
                    onPermissionCallback?.let { it() }
                }
            }
        }
    }
}
