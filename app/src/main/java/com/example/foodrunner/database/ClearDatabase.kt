package com.example.foodrunner.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class ClearDatabase(val context: Context) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void?): Boolean {


        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-dbs")
                .build()
        db.cartDao().deleteOrder()
        db.close()
        return true
    }
}