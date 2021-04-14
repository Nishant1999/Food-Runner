package com.example.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.adapter.FavouriteRecyclerAdapter
import com.example.foodrunner.R
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity

class FavouriteFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var dbRestaurantList= listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFavourite=view.findViewById(R.id.recyclerHome)

        layoutManager= LinearLayoutManager(activity)


        progressLayout = view.findViewById(R.id.progressLayout)




        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE

        dbRestaurantList=RetrieveFavourite(activity as Context).execute().get()

        if(activity!=null)
        {
            progressLayout.visibility=View.GONE
            recyclerAdapter= FavouriteRecyclerAdapter(activity as Context,
                dbRestaurantList as ArrayList<RestaurantEntity>
            )
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        return view
    }
    class RetrieveFavourite(val context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>()
    {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-dbs").build()

            return db.restaurantDao().getAllRestaurant()
        }

    }

}