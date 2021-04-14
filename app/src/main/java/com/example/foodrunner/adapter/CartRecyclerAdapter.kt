package com.example.foodrunner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.database.CartEntity

class CartRecyclerAdapter(
    private val listItem: ArrayList<CartEntity>,
) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_cart_row, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = listItem[position]
        holder.itemName.text = item.menuName
        holder.itemPrice.text = "Rs.${item.cost_for_price}"
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.txtRestaurantName)
        val itemPrice: TextView = view.findViewById(R.id.txtPriceName)
    }
}