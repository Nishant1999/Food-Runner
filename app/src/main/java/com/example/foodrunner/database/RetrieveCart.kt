package com.example.foodrunner.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class RetrieveCart(val context: Context):AsyncTask<Void,Void,List<CartEntity>>() {
    override fun doInBackground(vararg params: Void?): List<CartEntity> {
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-dbs")
                .build()
        val allMenu=db.cartDao().getAllMenuItem()
        db.close()
        return allMenu
    }
}