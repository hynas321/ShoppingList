package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.view.Gravity

import android.widget.LinearLayout
import kotlinx.android.synthetic.main.custom_navigation_bar_1.view.*


class ProductListActivity : AppCompatActivity() {
    private val context: AppCompatActivity = this

    private lateinit var databaseManager: DatabaseManager
    private lateinit var activityManager: ActivityManager

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addProductButton: Button
    private lateinit var noItemsTextView: TextView
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
        noItemsTextView = findViewById(R.id.product_list_empty)
        navigationBar = findViewById(R.id.custom_navigation_bar_1)
        navigationBarSettingsButton = navigationBar.settings_icon
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = productListAdapter

        addProductButton.setOnClickListener {
            createItemAlertDialog()
        }

        navigationBarSettingsButton.setOnClickListener {
            activityManager.startActivityWithResources(username, shoppingListName, "ProductListActivity", SettingsActivity::class.java)
        }
    }

    private fun createItemAlertDialog() {
        val builder = AlertDialog.Builder(this)

        val nameInput = EditText(this)
        nameInput.hint = "Name"
        nameInput.inputType = InputType.TYPE_CLASS_TEXT
        nameInput.height = 150
        nameInput.gravity = Gravity.CENTER

        val quantityInput = EditText(this)
        quantityInput.hint = "Quantity"
        quantityInput.inputType = InputType.TYPE_CLASS_TEXT
        quantityInput.height = 150
        quantityInput.gravity = Gravity.CENTER

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        builder.setTitle("Add a new product")
        layout.addView(nameInput)
        layout.addView(quantityInput)
        builder.setView(layout)

        builder.setPositiveButton("OK") { _, _ ->
            val productName = nameInput.text.toString()
            var productQuantity = quantityInput.text.toString()

            if (productName == "") {
                Toast.makeText(context, "Cannot create the product with no name", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            if (productQuantity == "") {
                productQuantity = "No quantity"
            }
            else {
                productQuantity = "Quantity: $productQuantity"
            }

            val product =
                ProductModel(username, shoppingListName, productName,
                    productQuantity, false)

            CoroutineScope(Dispatchers.Main).launch {
                val productExists = withContext(Dispatchers.IO) {
                    databaseManager.checkIfProductExists(username, shoppingListName, product.productName).await()
                }

                if (!productExists) {
                    productListAdapter.insertItem(product)
                }
                else {
                    Toast.makeText(context, "Product already exists", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setProductModelsChangeEvent() {
        databaseManager.getProductsReference()
            .orderByChild("username")
            .equalTo(username)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    productModels.clear()

                    for (dataSnapshot in snapshot.children) {
                        val shoppingList = dataSnapshot.child("shoppingListName").value.toString()
                        val productName = dataSnapshot.child("productName").value.toString()
                        val quantity = dataSnapshot.child("quantity").value.toString()
                        val bought = dataSnapshot.child("bought").value.toString().toBoolean()

                        if (shoppingList == shoppingListName) {
                            val productModel = ProductModel(username, shoppingListName, productName, quantity, bought)

                            productModels.add(productModel)
                        }
                    }
                    productModels.sortWith(compareBy {it.productName} )
                    productListAdapter.notifyDataSetChanged()

                    if (productModels.isEmpty()) {
                        noItemsTextView.visibility = View.VISIBLE
                    }
                    else {
                        noItemsTextView.visibility = View.INVISIBLE
                    }
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