package com.example.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.adapter.RestaurantDetailsRecyclerAdapter
import com.example.foodrunner.R
import com.example.foodrunner.database.ClearDatabase
import com.example.foodrunner.database.CountMenu
import com.example.foodrunner.model.RestaurantDetailsObject
import com.example.foodrunner.util.ConnectionManager

import org.json.JSONException
import org.json.JSONObject

class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var recyclerRestaurant: RecyclerView
    lateinit var btnAddtoCart: Button
    lateinit var frameLayout: FrameLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantDetailsRecyclerAdapter
    lateinit var restaurantNameList: ArrayList<RestaurantDetailsObject>
    lateinit var toolbar: Toolbar
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var resId: Int = 100
    var resName: String = "Restaurant"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        if (intent != null) {
            resId = intent.getIntExtra("id", 1)
            resName = intent.getStringExtra("name").toString()
        } else {
            Toast.makeText(this@RestaurantDetailsActivity, "Some Error Occurred", Toast.LENGTH_LONG)
                .show()
        }

        recyclerRestaurant = findViewById(R.id.recyclerRestaurant)

        btnAddtoCart = findViewById(R.id.btnAddToCart)

        layoutManager = LinearLayoutManager(this)

        frameLayout = findViewById(R.id.frame)

        restaurantNameList = ArrayList<RestaurantDetailsObject>()

        toolbar = findViewById(R.id.toolbar)

        progressLayout = findViewById(R.id.progressLayout)

        progressBar = findViewById(R.id.progressBar)

        //when fragment is loaded progress layout is visible

        progressLayout.visibility = View.VISIBLE

        btnAddtoCart.visibility = View.GONE


        setUpToolbar()

        fetchData()
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
        }


    return super.onOptionsItemSelected(item)
}

    override fun onBackPressed() {
        if(CountMenu(this).execute().get() as Int>0)
        {
                val cleared=ClearDatabase(this).execute().get()
                if(cleared==true)
                {
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                else {
                    Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_LONG).show()
                }
        }


        else
            startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            if (restaurantNameList.isEmpty())
                fetchData()
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
        super.onResume()
    }
    private fun fetchData() {

        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)

        val url: String = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

        if (ConnectionManager().checkConnectivity(this as Context)) {

            val jsonRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener<JSONObject> {

                    try {

                        progressLayout.visibility = View.GONE

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val data1 = data.getJSONArray("data")
                            for (i in 0 until data1.length()) {
                                val restaurantObject = data1.getJSONObject(i)
                                val restaurantNameObject = RestaurantDetailsObject(
                                    restaurantObject.getString("id").toInt(),
                                    restaurantObject.getString("name"),
                                    restaurantObject.getString("cost_for_one").toInt()
                                )
                                restaurantNameList.add(restaurantNameObject)

                                layoutManager = LinearLayoutManager(this)
                                recyclerAdapter =
                                    RestaurantDetailsRecyclerAdapter(
                                        this as Context,
                                        restaurantNameList, btnAddtoCart, resName, resId
                                    )

                                recyclerRestaurant.adapter = recyclerAdapter
                                recyclerRestaurant.layoutManager = layoutManager
                                recyclerRestaurant.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerRestaurant.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this as Context,
                            "Some unexpected error occurred in catch !!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this as Context,
                        "Some unexpected error occurred in error listener",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

}