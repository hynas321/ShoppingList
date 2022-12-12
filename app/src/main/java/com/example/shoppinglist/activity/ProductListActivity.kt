package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.adapter.ProductListAdapter
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_product_list.view.*

class ProductListActivity : AppCompatActivity() {
    val context: AppCompatActivity = this

    private lateinit var databaseManager: DatabaseManager
    private lateinit var activityManager: ActivityManager

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addProductButton: Button
    private lateinit var navigationBar: ConstraintLayout
    private lateinit var navigationBarSettingsButton: View

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
        navigationBar = findViewById(R.id.custom_navigation_bar_1)
        navigationBarSettingsButton = navigationBar.custom_navigation_bar_1

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = productListAdapter

        addProductButton.setOnClickListener {
            createItemAlertDialog()
        }

        navigationBarSettingsButton.setOnClickListener {
            activityManager.startActivityWithResources(username, shoppingListName, "ProductListActivity", SettingsActivity::class.java)
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
            val product = ProductModel(input.text.toString(), R.id.imageView_icon, "10", false)

            productListAdapter.insertItem(product)
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
                        val bought = dataSnapshot.child("bought").value.toString().toBoolean()

                        val productModel = ProductModel(productName, categoryIcon, quantity, bought)

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