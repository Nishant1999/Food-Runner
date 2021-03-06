package com.example.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.model.Restaurant
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment() : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var restaurantList=arrayListOf<Restaurant>()
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    var restaurantDisplayList = arrayListOf<Restaurant>()
    var userId:Int=0
    private val ratingComparator = Comparator<Restaurant>{ rest1, rest2 ->
        if(rest1.rating.compareTo(rest2.rating,true) == 0){
            rest1.name.compareTo(rest2.name,true)
        }else{
            rest1.rating.compareTo(rest2.rating,true)
        }
    }
    private val costComparator = Comparator<Restaurant>{ rest1, rest2 ->
        if(rest1.cost_for_one.toString().compareTo(rest2.cost_for_one.toString(),true) == 0){
            rest1.rating.compareTo(rest2.rating,true)
        }else{
            rest1.cost_for_one.toString().compareTo(rest2.cost_for_one.toString(),true)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view=inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences=
            activity?.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)!!


        userId= Integer.parseInt(sharedPreferences.getString("user_id","00").toString())



        recyclerHome=view.findViewById(R.id.recyclerHome)

        layoutManager=LinearLayoutManager(activity)

        getSearchView()



        /* recyclerHome.addItemDecoration(
             DividerItemDecoration(
                 recyclerHome.context,(layoutManager as LinearLayoutManager).orientation
             )
         )*/

        val queue= Volley.newRequestQueue(activity as Context)

        val url:String="http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url,null, Response.Listener<JSONObject> {

                try {

                    //Hide the progress bar


                    //Here it will handle the response
                    val data=it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    if (success) {

                        val data1 = data.getJSONArray("data")
                        for (i in 0 until data1.length()) {
                            val restaurantJsonObject = data1.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id").toInt(),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one").toInt(),
                                restaurantJsonObject.getString("image_url")
                            )
                            restaurantDisplayList.add(restaurantObject)
                        }
                            restaurantList.addAll(restaurantDisplayList)


                            layoutManager = LinearLayoutManager(activity)
                            recyclerAdapter =
                                HomeRecyclerAdapter(activity as Context, restaurantDisplayList,userId)

                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager

                        }

                }

                catch (e: JSONException)
                {
                    Toast.makeText(activity as Context,"Some unexpected error occurred in catch !!!",
                        Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                //Here it will handle the error
                if(activity!=null) {
                    Toast.makeText(
                        activity as Context,
                        "Some unexpected error occurred in error listener",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }) {
                //Sending header in our request
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view;


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rating -> {
                Collections.sort(restaurantDisplayList,ratingComparator)
                restaurantDisplayList.reverse()
            }
            R.id.lowToHigh -> {
                Collections.sort(restaurantDisplayList,costComparator)
            }
            R.id.highToLow -> {
                Collections.sort(restaurantDisplayList,costComparator)
                restaurantDisplayList.reverse()
            }
        }

        // to tell the adapter class to change the order
        // of the list displayed we are writing the below code
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }
    private fun getSearchView() {
        val searchView = activity?.findViewById<SearchView>(R.id.searchView)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(nextText: String?): Boolean {
                if (nextText!!.isNotBlank()) {
                    restaurantDisplayList.clear()
                    val search = nextText.toLowerCase()
                    restaurantList.forEach {
                        if (it.name.toLowerCase().contains(search)) {
                            restaurantDisplayList.add(it)
                        }
                    }
                    if (restaurantDisplayList.isEmpty()) {
                        Toast.makeText(context, "Restaurant Not Found!!", Toast.LENGTH_SHORT).show()
                    }
                    recyclerAdapter.notifyDataSetChanged()
                } else {
                    restaurantDisplayList.clear()
                    restaurantDisplayList.addAll(restaurantList)
                    recyclerAdapter.notifyDataSetChanged()
                }
                return true
            }

        })

    }

}