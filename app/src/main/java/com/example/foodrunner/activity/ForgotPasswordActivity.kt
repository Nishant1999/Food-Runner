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

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber:EditText
    lateinit var etEmail:EditText
    lateinit var btnNext:Button
    private var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE)

        setContentView(R.layout.activity_forgot_password)

        etMobileNumber=findViewById(R.id.etMobileNumber)
        etEmail=findViewById(R.id.etEmail)
        btnNext=findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val userMbNo = etMobileNumber.text.toString().trim()
            val userEmail = etEmail.text.toString().trim()

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", userMbNo)
            jsonParams.put("email", userEmail)

            if (ConnectionManager().checkConnectivity(this)) {
                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    Response.Listener {

                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {

                                val firstTry = data.getBoolean("first_try")

                                if (!firstTry) {
                                    if (this != null) {
                                        Toast.makeText(
                                            this,
                                            "Not the first try...Please use the Already mailed OTP.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    if (this != null) {
                                        Toast.makeText(
                                            this,
                                            "OTP sent successfully on the registered email address",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                                sharedPreferences?.edit()?.putString("mobile_number",etMobileNumber.text.toString().trim())?.apply()
                                val intent= Intent(this@ForgotPasswordActivity,NewPasswordActivity::class.java)
                                startActivity(intent)
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
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}