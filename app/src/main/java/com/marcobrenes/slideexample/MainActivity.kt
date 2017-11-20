package com.marcobrenes.slideexample

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.*
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class MainActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener  {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var recyclerView: RecyclerView
    private var cartList: MutableList<Item> = ArrayList()
    private lateinit var mAdapter: CartListAdapter
    private lateinit var coordinatorLayout: CoordinatorLayout

    // url to fetch menu json
    private val URL = "https://api.androidhive.info/json/menu.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.my_cart)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recycler_view)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        cartList = ArrayList()
        mAdapter = CartListAdapter(this, cartList as ArrayList<Item>)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mAdapter

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        val itemTouchHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)


        // making http call and fetching menu json
        prepareCart()
    }

    /**
     * method make volley network call and parses json
     */
    private fun prepareCart() {
        val request = JsonArrayRequest(URL,
                Response.Listener { response ->
                    if (response == null) {
                        Toast.makeText(applicationContext, "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show()
                        return@Listener
                    }

                    val items = Gson().fromJson<List<Item>>(response.toString(), object : TypeToken<List<Item>>() {

                    }.type)

                    // adding items to cart list
                    cartList.clear()
                    cartList.addAll(items)

                    // refreshing recycler view
                    mAdapter.notifyDataSetChanged()
                }, Response.ErrorListener { error ->
            // error in getting json
            Log.d(TAG, "Error: " + error.message)
            Toast.makeText(applicationContext, "Error: " + error.message, Toast.LENGTH_SHORT).show()
        })

        MyApplication.instance.addToRequestQueue(request)
    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            val name = cartList[viewHolder.getAdapterPosition()].name

            // backup of removed item for undo purpose
            val deletedItem = cartList[viewHolder.getAdapterPosition()]
            val deletedIndex = viewHolder.getAdapterPosition()

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition())

            // showing snack bar with Undo option
            //val snackbar = Snackbar.make(coordinatorLayout?, name + " removed from cart!", Snackbar.LENGTH_LONG)
            val snackbar = Snackbar.make(coordinatorLayout, "${name} removed from cart!", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO", View.OnClickListener {
                // undo is selected, restore the deleted item
                mAdapter.restoreItem(deletedItem, deletedIndex)
            })
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }
}
