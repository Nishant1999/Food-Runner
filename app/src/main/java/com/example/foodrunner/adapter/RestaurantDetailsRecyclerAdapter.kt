package com.example.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.CartActivity
import com.example.foodrunner.adapter.RestaurantDetailsRecyclerAdapter.RestaurantViewHolder.Companion.count
import com.example.foodrunner.database.CartEntity
import com.example.foodrunner.database.CountMenu
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.model.RestaurantDetailsObject

class RestaurantDetailsRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<RestaurantDetailsObject>,
    val btnAddToCart: Button,
    var resName: String,
    var resId:Int
) :
    RecyclerView.Adapter<RestaurantDetailsRecyclerAdapter.RestaurantViewHolder>() {


    var total_cost = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_row_restaurant_details, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {

        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.name
        holder.txtPriceName.text = restaurant.cost_for_one.toString()

        val cartEntity = CartEntity(
            restaurant.id,
            restaurant.name,
            restaurant.cost_for_one,
            resId
        )

        val checkAdd = DBAsyncTask(context, cartEntity, 1).execute()
        val isFav = checkAdd.get()

        if (isFav) {

            holder.btnAdd.text = "Remove"
            val noFavColor = ContextCompat.getColor(context, R.color.orange)
            holder.btnAdd.setBackgroundColor(noFavColor)
            getCount()
        } else {

            holder.btnAdd.text = "Add"
            val noFavColor = ContextCompat.getColor(context, R.color.splashColor)
            holder.btnAdd.setBackgroundColor(noFavColor)
            getCount()
        }

        holder.btnAdd.setOnClickListener {
            if (!DBAsyncTask(context, cartEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, cartEntity, 2).execute()
                val result = async.get()
                if (result) {

                    btnAddToCart.visibility = View.VISIBLE

                    holder.btnAdd.text = "Remove"
                    val addColor = ContextCompat.getColor(context, R.color.orange)
                    holder.btnAdd.setBackgroundColor(addColor)
                    total_cost += restaurant.cost_for_one
                    getCount()
                } else {
                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show()
                }
            } else {
                val async = DBAsyncTask(context, cartEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.btnAdd.text = "Add"
                    val noFavColor = ContextCompat.getColor(context, R.color.splashColor)
                    holder.btnAdd.setBackgroundColor(noFavColor)
                    total_cost -= restaurant.cost_for_one
                    getCount()
                } else {
                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPriceName: TextView = view.findViewById(R.id.txtPriceName)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)

        companion object {
            @SuppressLint("StaticFieldLeak")
            var count: Int = 0
        }

    }

    class DBAsyncTask(val context: Context, val cartEntity: CartEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-dbs").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val cart: CartEntity? = db.cartDao().getCartById(cartEntity.menu_id)
                    db.close()
                    return cart != null
                }
                2 -> {
                    db.cartDao().insertMenuItem(cartEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.cartDao().deleteMenuItem(cartEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }



    fun getCount() {
        if (CountMenu(context).execute().get() as Int > 0) {
            btnAddToCart.visibility = View.VISIBLE
            btnAddToCart.setOnClickListener {
                val intent = Intent(context, CartActivity::class.java)
                intent.putExtra("resName", resName)
                intent.putExtra("cost", total_cost.toString())
                context.startActivity(intent)
            }
        } else {
            btnAddToCart.visibility = View.GONE
        }
    }




}


