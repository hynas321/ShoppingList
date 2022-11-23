package com.example.shoppinglist.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.product.ProductListAdapter
import com.example.shoppinglist.product.ProductModel

class ProductListActivity : AppCompatActivity() {
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addProductButton: Button

    private val productModels = getProducts()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
        overridePendingTransition(0, 0)

        val context: ProductListActivity = this

        linearLayoutManager = LinearLayoutManager(this)
        productListAdapter = ProductListAdapter(productModels)
        recyclerView = findViewById(R.id.product_list_recyclerView_product)
        addProductButton = findViewById(R.id.product_list_button_add_product)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = productListAdapter

        addProductButton.setOnClickListener {
            productModels.add(
                ProductModel(
                    R.drawable.ic_launcher_background, "New shopping list", "5"
                )
            )

            productListAdapter.notifyItemInserted(productModels.size - 1)
        }
    }

    override fun onBackPressed() {
        val shoppingListIntent = Intent(this, ShoppingListActivity::class.java)
        overridePendingTransition(0, 0)
        startActivity(shoppingListIntent)
    }

    private fun getProducts(): ArrayList<ProductModel> {
        val list = ArrayList<ProductModel>()

        list.add(
            ProductModel(
                R.drawable.ic_shopping_cart, "Product 1", "5")
        )
        list.add(
            ProductModel(
                R.drawable.ic_shopping_cart, "Product 2", "10")
        )
        list.add(
            ProductModel(
                R.drawable.ic_shopping_cart, "Product 3", "100")
        )

        return list
    }
}