package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.adapter.ShoppingListAdapter
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.ShoppingListModel
import com.example.shoppinglist.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class ShoppingListActivity : AppCompatActivity() {
    val context: AppCompatActivity = this

    private lateinit var databaseManager: DatabaseManager

    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addListButton: Button

    private lateinit var shoppingListModels: ArrayList<ShoppingListModel>
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        overridePendingTransition(0, 0)

        databaseManager = DatabaseManager()
        shoppingListModels = ArrayList()

        user = intent.getSerializableExtra("model") as UserModel

        setShoppingLists(user.username)

        linearLayoutManager = LinearLayoutManager(this)
        shoppingListAdapter = ShoppingListAdapter(this, shoppingListModels, user)

        recyclerView = findViewById(R.id.product_list_recyclerView_product)
        addListButton = findViewById(R.id.shopping_list_button_add_list)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListAdapter

        addListButton.setOnClickListener {
            shoppingListAdapter.createItemAlertDialog()
        }
    }

    private fun setShoppingLists(username: String) {
        databaseManager.getShoppingListsReference(username)
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    shoppingListModels.clear()

                    for (dataSnapshot in snapshot.children) {

                        val shoppingListName = dataSnapshot.key.toString()
                        val iconImageViewId = dataSnapshot.child("iconImageViewId").value.toString().toInt()

                        val shoppingListModel = ShoppingListModel(shoppingListName, iconImageViewId)

                        shoppingListModels.add(shoppingListModel)
                    }

                    shoppingListAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error, cannot access data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        moveTaskToBack(true);
    }
}