package com.example.foodrunner.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class CountMenu(val context: Context) : AsyncTask<Void, Void, Int>() {

    override fun doInBackground(vararg params: Void?): Int {


        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-dbs")
                .build()
        val count = db.cartDao().getCount()
        db.close()
        return count
    }
}