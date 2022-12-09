package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.adapter.ProductListAdapter
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.ProductModel
import com.example.shoppinglist.model.ShoppingListModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProductListActivity : AppCompatActivity() {
    val context: AppCompatActivity = this

    private lateinit var databaseManager: DatabaseManager
    private lateinit var activityManager: ActivityManager

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addProductButton: Button

    private lateinit var productModels: ArrayList<ProductModel>
    private lateinit var username: String
    private lateinit var shoppingListName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
        overridePendingTransition(0, 0)

        databaseManager = DatabaseManager()
        activityManager = ActivityManager(context)
        productModels = ArrayList()

        username = intent.getStringExtra("string1").toString()
        shoppingListName = intent.getStringExtra("string2").toString()

        setProductModelsChangeEvent()

        linearLayoutManager = LinearLayoutManager(this)
        productListAdapter = ProductListAdapter(this, productModels, username, shoppingListName)
        recyclerView = findViewById(R.id.product_list_recyclerView_product)
        addProductButton = findViewById(R.id.product_list_button_add_product)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = productListAdapter

        addProductButton.setOnClickListener {
            createItemAlertDialog()
        }
    }

    fun createItemAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        builder.setTitle("Set name of your new product")
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val productModel = ProductModel(input.text.toString(), R.id.imageView_icon, "10")

            productListAdapter.insertItem(productModel)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setProductModelsChangeEvent() {
        databaseManager.getProductsReference(username, shoppingListName)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    productModels.clear()

                    for (dataSnapshot in snapshot.children) {

                        val productName = dataSnapshot.key.toString()
                        val categoryIcon =
                            dataSnapshot.child("categoryIcon").value.toString().toInt()
                        val quantity = dataSnapshot.child("quantity").value.toString()

                        val productModel = ProductModel(productName, categoryIcon, quantity)

                        productModels.add(productModel)
                    }

                    productListAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error, cannot access data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        overridePendingTransition(0, 0)
        activityManager.startActivityWithResources(username, ShoppingListActivity::class.java)
    }
}