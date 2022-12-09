package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
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
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        overridePendingTransition(0, 0)

        databaseManager = DatabaseManager()
        shoppingListModels = ArrayList()

        username = intent.getStringExtra("string").toString()

        setShoppingListModelsChangeEvent()

        linearLayoutManager = LinearLayoutManager(this)
        shoppingListAdapter = ShoppingListAdapter(this, shoppingListModels, username)

        recyclerView = findViewById(R.id.product_list_recyclerView_product)
        addListButton = findViewById(R.id.shopping_list_button_add_list)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListAdapter

        addListButton.setOnClickListener {
            createItemAlertDialog()
        }
    }

    private fun createItemAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        builder.setTitle("Set name of your new shopping list")
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val shoppingList = ShoppingListModel(input.text.toString(), R.id.imageView_icon)

            shoppingListAdapter.insertItem(shoppingList)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setShoppingListModelsChangeEvent() {
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