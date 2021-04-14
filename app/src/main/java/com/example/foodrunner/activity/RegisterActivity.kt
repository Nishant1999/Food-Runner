package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    //lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etMobileNumber:EditText
    lateinit var etLocation:EditText
    lateinit var password:EditText
    lateinit var confirmPassword:EditText
    lateinit var btnRegister:Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)



        setContentView(R.layout.activity_register)


        //toolbar=findViewById(R.id.toolbar)
        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmail)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etLocation=findViewById(R.id.etLocation)
        password=findViewById(R.id.etPassword)
        confirmPassword=findViewById(R.id.etConfirmPassword)
        btnRegister=findViewById(R.id.btnRegister)
        //setUpToolbar()

        btnRegister.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this@RegisterActivity as Context)) {

                val queue= Volley.newRequestQueue(this@RegisterActivity)
                val url="http://13.235.250.119/v2/register/fetch_result"
                val jsonParams=JSONObject()

                jsonParams.put("name",etName.text.toString())
                jsonParams.put("mobile_number",etMobileNumber.text.toString())
                jsonParams.put("password",password.text.toString())
                jsonParams.put("address",etLocation.text.toString())
                jsonParams.put("email",etEmail.text.toString())

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
                                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)

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
                                  val dialog= AlertDialog.Builder(this@RegisterActivity)
                                  dialog.setTitle(" Failed")
                                  val addMessage:String=data.getString("errorMessage")
                                  dialog.setMessage(addMessage)
                                  dialog.setPositiveButton("Ok"){text,listener ->
                                  }
                                  dialog.create()
                                  dialog.show()
                              }
                          }
                          catch (e:JSONException)
                          {
                              Toast.makeText(this@RegisterActivity,"Some Error occurred",Toast.LENGTH_SHORT).show()
                          }

                }, Response.ErrorListener {
                        Toast.makeText(this@RegisterActivity,"Some Error occurred in error listener",Toast.LENGTH_SHORT).show()
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
            else
            {
                val dialog= AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Registration Failed")
                dialog.setMessage("Sorry, unable to login.Please check your internet connection")
                dialog.setPositiveButton("Ok"){text,listener ->
                }
                dialog.create()
                dialog.show()
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
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

   /* fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId

        if(id==android.R.id.home)
        {
            val intent=Intent(this@RegisterActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }*/

}