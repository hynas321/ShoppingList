package com.example.shoppinglist.model

class ProductModel(
    var productId: String,      //PK
    var shoppingListId: String, //FK
    var categoryIcon: Int,
    var productName: String,
    var quantity: String
) : Model