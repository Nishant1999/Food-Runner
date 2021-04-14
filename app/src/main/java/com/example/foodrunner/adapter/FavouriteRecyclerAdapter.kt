package com.example.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.example.foodrunner.R
import com.example.foodrunner.R.drawable.ic_heart_lining
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, private val restaurantList:ArrayList<RestaurantEntity>):
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favourite_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val restaurant = restaurantList[position]
        holder.txtRestaurantName.text = restaurant.resName
        holder.txtRating.text = restaurant.resRating
        holder.txtPriceName.text = restaurant.cost_for_price.toString()
        Picasso.get().load(restaurant.image_url).into(holder.imgResLogo)

        val restEntity = RestaurantEntity(
            restaurant.res_id,
            restaurant.resName,
            restaurant.resRating,
            restaurant.cost_for_price,
            restaurant.image_url
        )

        val checkFav = DBAsyncTask(context, restEntity, 1).execute()

        val isFav = checkFav.get()

        if (isFav) {
            holder.imgFavourite.setImageResource(R.drawable.ic_heart_filling)
        } else {
            holder.imgFavourite.setImageResource(ic_heart_lining)
        }

        holder.imgFavourite.setOnClickListener {
            if (!DBAsyncTask(context, restEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.imgFavourite.setImageResource(R.drawable.ic_heart_filling)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = DBAsyncTask(context, restEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.imgFavourite.setImageResource(ic_heart_lining)
                    restaurantList.remove(restEntity)
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPriceName: TextView = view.findViewById(R.id.txtPriceName)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgResLogo: ImageView = view.findViewById(R.id.imgResLogo)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
    }
    class DBAsyncTask(
        val context: Context,
        private val restEntity: RestaurantEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {

        /*
          Mode 1-> Check DB if the restaurant is favourite or not
          Mode 2-> Save the restaurant into DB as Favourite
          Mode 3-> Remove the favourite restaurant
         */
        private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao().getBookById(restEntity.res_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    // save
                    db.restaurantDao().insertRestaurant(restEntity)
                    db.close()
                    return true
                }
                3 -> {
                    // Remove
                    db.restaurantDao().deleteRestaurant(restEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}
//restaurant.remove(restaurantEntity)
//notifyDataSetChange()

