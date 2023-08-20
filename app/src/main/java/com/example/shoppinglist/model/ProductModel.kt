package com.example.shoppinglist.model

class ProductModel(
    var username: String, // FK
    var shoppingListName: String,  //FK
    var productName: String,
    var quantity: String,
    var bought: Boolean,
) : Model