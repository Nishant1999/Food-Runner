package com.example.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {

    @Insert
    fun insertMenuItem(cartEntity: CartEntity)

    @Delete
    fun deleteMenuItem(cartEntity: CartEntity)

    @Query("select * from cart")
    fun getAllMenuItem():List<CartEntity>

    @Query("select * from cart where menu_id=:menu_id")
    fun getCartById(menu_id:Int):CartEntity

    @Query("select COUNT(menu_name) from cart")
    fun getCount():Int

    @Query("Delete from cart")
    fun deleteOrder()

}