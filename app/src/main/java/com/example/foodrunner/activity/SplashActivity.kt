package com.example.foodrunner.activity


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.foodrunner.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent=Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        },2000)
    }

}