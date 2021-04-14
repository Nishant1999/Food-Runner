package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject

class NewPasswordActivity : AppCompatActivity() {

    lateinit var etOtp:EditText
    lateinit var etPassword:EditText
    lateinit var etConfirmPassword:EditText
    lateinit var btnSubmit:Button
    var mobile_number:String?=""
    private var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        setContentView(R.layout.activity_new_password)

        etOtp=findViewById(R.id.etOtp)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        btnSubmit=findViewById(R.id.btnSubmit)


        btnSubmit.setOnClickListener {
            val mobile_number = sharedPreferences?.getString("mobile_number", "9999999999")
            val userPass = etPassword.text.toString().trim()
            val otp = etOtp.text.toString().trim()
            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/reset_password/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobile_number)
            jsonParams.put("password", userPass)
            jsonParams.put("otp", otp)
            if (ConnectionManager().checkConnectivity(this)) {
                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    Response.Listener {

                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {
                                val successMessage = data.getString("successMessage")
                                if (this != null) {
                                    Toast.makeText(
                                        this,
                                        successMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                sharedPreferences?.edit()?.clear()?.apply()
                                val intent=Intent(this@NewPasswordActivity,LoginActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {

                                val errorMessage = data.getString("errorMessage")
                                if (this != null) {
                                    Toast.makeText(
                                        this,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        } catch (e: Exception) {

                            if (this != null) {
                                Toast.makeText(
                                    this,
                                    "Some Error $e occurred!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }, Response.ErrorListener {

                        if (this != null) {
                            Toast.makeText(
                               this,
                                "Volley Error Occurred",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }
                queue.add(jsonRequest)

            } else {
                if (this != null) {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        // Do Nothing
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        this?.finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        // Do Nothing
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
            }

        }

    }

    override fun onBackPressed() {
        val intent = Intent(this@NewPasswordActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }
}