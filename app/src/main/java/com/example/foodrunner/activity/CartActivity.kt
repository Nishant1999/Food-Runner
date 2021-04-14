package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_WIRELESS_SETTINGS
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.CartRecyclerAdapter
import com.example.foodrunner.adapter.RestaurantDetailsRecyclerAdapter
import com.example.foodrunner.database.CartEntity
import com.example.foodrunner.database.ClearDatabase
import com.example.foodrunner.database.RetrieveCart
import com.example.foodrunner.model.RestaurantDetailsObject
import com.example.foodrunner.util.ConnectionManager
import okhttp3.internal.http2.Settings
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var recyclerRestaurant: RecyclerView
    lateinit var btnOrder: Button
    lateinit var frameLayout: FrameLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var toolbar: Toolbar
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var restaurantName:TextView
    private lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var costOrder:Button
    lateinit var itemList:ArrayList<CartEntity>
    private var resName:String?=""
    private var totalCost:String?=""
    lateinit var userId:String
    lateinit var res_id:String
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        initiateValue()
        setUpToolbar()
        attachAdapter()
        btnOrder.setOnClickListener {
            placingOrder()
        }
    }
    private fun attachAdapter() {
        progressLayout.visibility = View.GONE
        layoutManager = LinearLayoutManager(this)
        recyclerAdapter = CartRecyclerAdapter( itemList)
        recyclerRestaurant.layoutManager = layoutManager
        recyclerRestaurant.adapter = recyclerAdapter
    }
    fun initiateValue()
    {
        recyclerRestaurant=findViewById(R.id.recyclerRestaurant)

        btnOrder=findViewById(R.id.btnOrderPlaced)

        layoutManager= LinearLayoutManager(this)

        frameLayout = findViewById(R.id.frame)

        restaurantName=findViewById(R.id.restaurantName)


        toolbar=findViewById(R.id.toolbar)

        progressLayout = findViewById(R.id.progressLayout)

        progressBar = findViewById(R.id.progressBar)

        //when fragment is loaded progress layout is visible

        progressLayout.visibility= View.VISIBLE


        if(intent!=null)
        {
            resName=intent.getStringExtra("resName")
            restaurantName.text=resName
            totalCost=intent.getStringExtra("cost")
            btnOrder.text="Place Order(Total:Rs.$totalCost)"
        }
        itemList=RetrieveCart(this).execute().get() as ArrayList<CartEntity>
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        userId=sharedPreferences.getString("user_id","100").toString()
        res_id=itemList[0].res_id.toString()
    }
    fun getFoodJsonArray():JSONArray
    {
        val food=JSONArray()
        for(item in itemList)
        {
            val foodobject=JSONObject()
            foodobject.put("food_item_id",item.menu_id)
            food.put(foodobject)
        }
        return food
    }

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun placingOrder() {
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val jsonParams = JSONObject()
        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", res_id)
        jsonParams.put("total_cost", totalCost)
        jsonParams.put("food", getFoodJsonArray())

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val clearDataBase = ClearDatabase(this).execute().get()
                            if (clearDataBase) {
                                val intent = Intent(this, OrderPlaced::class.java)
                                startActivity(intent)
                                finishAffinity()
                            } else {
                                Toast.makeText(this, "Some Error occurred!", Toast.LENGTH_LONG).show()
                            }

                        } else {
                            val errorMessage = data.getString("errorMessage")
                            progressLayout.visibility = View.GONE
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                            finish()
                        }

                    } catch (e: Exception) {
                        progressLayout.visibility = View.GONE
                        Toast.makeText(this, "Some Error $e occurred!", Toast.LENGTH_LONG).show()
                    }

                }, Response.ErrorListener {
                    progressLayout.visibility = View.GONE
                    Toast.makeText(this, "Volley Error Occurred", Toast.LENGTH_LONG).show()
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
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
              /*  val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()*/

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        if(id==android.R.id.home)
        {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}