package com.example.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("select * from restaurant")
    fun getAllRestaurant():List<RestaurantEntity>

    @Query("select * from restaurant where res_id=:res_id")
    fun getBookById(res_id:String):RestaurantEntity
}