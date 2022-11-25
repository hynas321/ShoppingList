package com.example.shoppinglist.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.adapter.ShoppingListAdapter
import com.example.shoppinglist.model.ShoppingListModel


class ShoppingListActivity : AppCompatActivity() {
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addListButton: Button

    private val shoppingListModels = getShoppingLists()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        overridePendingTransition(0, 0)

        linearLayoutManager = LinearLayoutManager(this)
        shoppingListAdapter = ShoppingListAdapter(this, shoppingListModels)
        recyclerView = findViewById(R.id.product_list_recyclerView_product)
        addListButton = findViewById(R.id.shopping_list_button_add_list)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListAdapter

        addListButton.setOnClickListener {
            shoppingListAdapter.createItemAlertDialog()
        }
    }

    private fun getShoppingLists(): ArrayList<ShoppingListModel> {
        val list = ArrayList<ShoppingListModel>()

        list.add(
            ShoppingListModel("Aaa", "B",
            R.drawable.ic_shopping_cart, "Shopping list 1")
        )
        list.add(
            ShoppingListModel("Abb", "B",
            R.drawable.ic_shopping_cart, "Shopping list 2")
        )
        list.add(
            ShoppingListModel("Acc", "B",
            R.drawable.ic_shopping_cart, "Shopping list 3")
        )

        return list
    }

    override fun onBackPressed() {
        moveTaskToBack(true);
    }
}