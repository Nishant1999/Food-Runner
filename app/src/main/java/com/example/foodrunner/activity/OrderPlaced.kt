package com.example.foodrunner.activity

import android.content.Intent
import android.graphics.Color.green
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.foodrunner.R

class OrderPlaced : AppCompatActivity() {
    lateinit var okButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        okButton = findViewById(R.id.ok)
        val ok = ContextCompat.getColor(this, R.color.orange)
        okButton.setBackgroundColor(ok)
        okButton.setOnClickListener {
            openMainActivity()
        }
    }

    private fun openMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        openMainActivity()
    }
}