package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    lateinit var imgLogoImage:ImageView
    lateinit var etMobileNumber:EditText
    lateinit var etPassword:EditText
    lateinit var btnLogin:Button
    lateinit var txtForgotPassword:TextView
    lateinit var txtSignUp:TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)


        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)

        setContentView(R.layout.activity_login)

        if(isLoggedIn)
        {
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
            finish()
        }

        imgLogoImage=findViewById(R.id.imgLogoImage)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogIn)
        txtForgotPassword=findViewById(R.id.txtForgotPassword)
        txtSignUp=findViewById(R.id.txtAccountYet)
        btnLogin.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this@LoginActivity as Context)) {

                val queue= Volley.newRequestQueue(this@LoginActivity)
                val url="http://13.235.250.119/v2/login/fetch_result"
                val jsonParams= JSONObject()

                jsonParams.put("mobile_number",etMobileNumber.text.toString())
                jsonParams.put("password",etPassword.text.toString())
                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {
                        try {
                            val data=it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            Log.d("success",success.toString())

                            val data1=data.getJSONObject("data")
                            Log.d("Username",data1.getString("email"))
                            if(success)
                            {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)


                                savePreference(
                                    data1.getString("user_id"),
                                    data1.getString("name"),
                                    data1.getString("email"),
                                    data1.getString("mobile_number"),
                                    data1.getString("address"))

                                startActivity(intent)
                                finish()
                            }
                            else
                            {
                                val dialog= AlertDialog.Builder(this@LoginActivity)
                                dialog.setTitle(" Failed")
                                val addMessage:String=data.getString("errorMessage")
                                dialog.setMessage(addMessage)
                                dialog.setPositiveButton("Ok"){text,listener ->
                                }
                                dialog.create()
                                dialog.show()
                            }
                        }
                        catch (e: JSONException)
                        {
                            Toast.makeText(this@LoginActivity,"Some Error occurred", Toast.LENGTH_SHORT).show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(this@LoginActivity,"Some Error occurred in error listener",
                            Toast.LENGTH_SHORT).show()
                        println("error aa gyi")
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String,String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="9bf534118365f1"
                        return headers
                    }
                }

                queue.add(jsonRequest)




            }
            else{
                val dialog=AlertDialog.Builder(this@LoginActivity as Context)
                dialog.setTitle("Login Failed")
                dialog.setMessage("Sorry, unable to login.Please check your internet connection")
                dialog.setPositiveButton("Ok"){text,listener ->
                }
                dialog.create()
                dialog.show()
            }
        }
        txtSignUp.setOnClickListener {
            val intent=Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        txtForgotPassword.setOnClickListener {

            val intent=Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    fun savePreference(user_id:String,name:String,email:String,mobile_number:String,address:String)
    {
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
        sharedPreferences.edit().putString("user_id",user_id).apply()
        sharedPreferences.edit().putString("Name",name).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("Mobile_Number",mobile_number).apply()
        sharedPreferences.edit().putString("address",address).apply()
    }
}