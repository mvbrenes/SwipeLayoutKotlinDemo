package com.marcobrenes.slideexample

/**
 * Created by marco on 11/19/2017.
 */

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView


import com.squareup.picasso.Picasso

class CartListAdapter(private val context: Context, private val cartList: MutableList<Item>) : RecyclerView.Adapter<CartListAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var description: TextView = view.findViewById(R.id.description)
        var price: TextView = view.findViewById(R.id.price)
        var thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        var viewBackground: RelativeLayout = view.findViewById(R.id.view_background)
        var viewForeground: RelativeLayout = view.findViewById(R.id.view_foreground)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = cartList[position]
        holder.name.text = item.name
        holder.description.text = item.description
        holder.price.text = "$" + item.price

        Picasso.with(context)
                .load(item.thumbnail)
                .into(holder.thumbnail)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    fun removeItem(position: Int) {
        cartList.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Item, position: Int) {
        cartList.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }
}