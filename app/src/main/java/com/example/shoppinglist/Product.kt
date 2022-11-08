package com.example.shoppinglist

import android.widget.ImageView

class Product(
    private val name: String,
    private val quantity: Int,
    private val priceSum: Double,
    private val categoryImage: ImageView
) {

    fun getName(): String {
        return name
    }

    fun getQuantity(): Int {
        return quantity
    }

    fun getPrice(): Double {
        return priceSum
    }

    fun getCategory(): ImageView {
        return categoryImage
    }
}