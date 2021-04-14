package com.example.foodrunner.model

import com.example.foodrunner.database.CartEntity

data class Orders(val orderId:String,val restName:String,val totalCost:String,val dateTime:String,val foodItems:ArrayList<CartEntity>)
