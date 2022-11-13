package com.example.shoppinglist

class ShoppingListModel(
    private val iconImageViewId: Int,
    private val name: String,
    private val deleteIconImageViewId: Int
) {

    fun getIcon(): Int {
        return iconImageViewId
    }

    fun getName(): String {
        return name
    }

    fun getDeleteIcon(): Int {
        return deleteIconImageViewId
    }
}