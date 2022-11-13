package com.example.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var shoppingListsAdapter: ShoppingListsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        shoppingListsAdapter = ShoppingListsAdapter(getShoppingLists())
        recyclerView = findViewById(R.id.main_recyclerView_shopping_list)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListsAdapter

        //shoppingListsAdapter.notifyDataSetChanged()

    }

    private fun getShoppingLists(): ArrayList<ShoppingListModel> {
        val list = ArrayList<ShoppingListModel>()

        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "a", R.drawable.ic_launcher_foreground)
        )
        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "b", R.drawable.ic_launcher_foreground)
        )
        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "c", R.drawable.ic_launcher_foreground)
        )
        return list
    }
}