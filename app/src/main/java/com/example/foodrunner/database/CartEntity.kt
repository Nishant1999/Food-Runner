package com.example.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="cart")
data class CartEntity(
    @PrimaryKey val menu_id:Int,
    @ColumnInfo(name="menu_name") val menuName:String,
    @ColumnInfo(name="menu_price") val cost_for_price:Int,
    @ColumnInfo(name="res_id") val res_id:Int
)