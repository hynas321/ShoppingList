package com.example.shoppinglist.model

class ShoppingListModel(
    var shoppingListId: String, //PK
    var username: String,       //FK
    var iconImageViewId: Int,
    var shoppingListName: String
) : Model