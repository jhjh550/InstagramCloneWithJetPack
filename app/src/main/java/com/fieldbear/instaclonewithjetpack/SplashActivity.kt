package com.fieldbear.instaclonewithjetpack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fieldbear.instaclonewithjetpack.login.LoginActivity
import com.fieldbear.instaclonewithjetpack.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        if(FirebaseAuth.getInstance().currentUser == null){
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }else{
            (application as MyApplication).getMyData {
                if(it == null){
                    Toast.makeText(this, "유저 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }else{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
   }
}
