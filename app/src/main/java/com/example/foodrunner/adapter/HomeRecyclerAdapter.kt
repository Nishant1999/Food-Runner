package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room


import com.example.foodrunner.R
import com.example.foodrunner.activity.RestaurantDetailsActivity
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso


class HomeRecyclerAdapter(val context: Context, val itemList:ArrayList<Restaurant>,val user_id:Int):RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val restaurant=itemList[position]
        holder.txtRestaurantName.text=restaurant.name
        holder.txtRating.text=restaurant.rating
        holder.txtPriceName.text=restaurant.cost_for_one.toString()
        Picasso.get().load(restaurant.image_url).into(holder.imgResLogo)

        val restaurantEntity= RestaurantEntity(restaurant.id,
            restaurant.name,
            restaurant.rating,
            restaurant.cost_for_one,
            restaurant.image_url)

        val checkFav=DBAsyncTask(context,restaurantEntity,1).execute()
        val isFav=checkFav.get()

        if(isFav)
        {
            holder.imgFavourite.setBackgroundResource(R.drawable.ic_heart_filling)
        }
        else
        {
            holder.imgFavourite.setBackgroundResource(R.drawable.ic_heart_lining)
        }
        holder.imgFavourite.setOnClickListener {
            if(!DBAsyncTask(context,restaurantEntity,1).execute().get())
            {
                val async=DBAsyncTask(context,restaurantEntity,2).execute()
                val result=async.get()
                if(result)
                {
                    Toast.makeText(context,"Favourite Button",Toast.LENGTH_LONG).show()
                    holder.imgFavourite.setBackgroundResource(R.drawable.ic_heart_filling)
                }
                else
                {
                    Toast.makeText(context,"Error Occurred",Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                val async=DBAsyncTask(context,restaurantEntity,3).execute()
                val result=async.get()
                if(result)
                {
                    Toast.makeText(context,"Removed From favourites",Toast.LENGTH_LONG).show()
                    holder.imgFavourite.setBackgroundResource(R.drawable.ic_heart_lining)
                }
                else
                {
                    Toast.makeText(context,"Error From favourites",Toast.LENGTH_LONG).show()
                }
            }
        }
        holder.cardView.setOnClickListener {

            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("id", restaurant.id)
            intent.putExtra("name", restaurant.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    class HomeViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        val txtRestaurantName:TextView=view.findViewById(R.id.txtRestaurantName)
        val txtPriceName:TextView=view.findViewById(R.id.txtPriceName)
        val txtRating:TextView=view.findViewById(R.id.txtRating)
        val imgResLogo:ImageView=view.findViewById(R.id.imgResLogo)
        val cardView: CardView =view.findViewById(R.id.cardView)
        val imgFavourite:ImageView=view.findViewById(R.id.imgFavourite)
    }
    class DBAsyncTask(val context: Context, private val restaurantEntity: RestaurantEntity, private val mode:Int):AsyncTask<Void,Void,Boolean>()
    {
        private val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-dbs").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode)
            {
                1->
                {
                    val restaurant: RestaurantEntity ?=db.restaurantDao().getBookById(restaurantEntity.res_id.toString())
                    db.close()
                    return restaurant !=null
                }
                2->
                {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3->
                {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}