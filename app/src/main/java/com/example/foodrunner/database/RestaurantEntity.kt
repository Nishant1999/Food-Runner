package com.example.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey val res_id:Int,
    @ColumnInfo(name="restaurant_name") val resName:String,
    @ColumnInfo(name="restaurant_rating") val resRating:String,
    @ColumnInfo(name="restaurant_cost") val cost_for_price:Int,
    @ColumnInfo(name="restaurant_image") val image_url:String
)




