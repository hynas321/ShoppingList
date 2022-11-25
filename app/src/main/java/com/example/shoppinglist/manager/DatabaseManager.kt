package com.example.shoppinglist.manager

import com.example.shoppinglist.property.DatabaseMainObject
import com.example.shoppinglist.model.UserModel
import com.example.shoppinglist.model.ProductModel
import com.example.shoppinglist.model.ShoppingListModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking

class DatabaseManager() {
    private var database: DatabaseReference =
        FirebaseDatabase.getInstance("https://shoppinglist-9f095-default-rtdb.europe-west1.firebasedatabase.app/").reference

    fun writeUser(user: UserModel): Boolean = runBlocking {
        var complete = false
        val userId = user.username

        val userValues = object {
            val email = user.email
            val password = user.password
        }

        database
            .child(DatabaseMainObject.users)
            .child(userId)
            .setValue(userValues)
            .addOnCompleteListener {
                complete = true
            }

        return@runBlocking true
    }

    fun writeShoppingList(shoppingList: ShoppingListModel): Boolean = runBlocking {
        var complete = false
        val shoppingListId = "123"

        val shoppingListValues = object {
            val username = shoppingList.username
            val iconImageViewId = shoppingList.iconImageViewId
            val name = shoppingList.username
        }

        database
            .child(DatabaseMainObject.users)
            .child(shoppingListId)
            .setValue(shoppingListValues)
            .addOnCompleteListener {
                complete = true
            }

        return@runBlocking complete
    }

    fun writeProduct(product: ProductModel): Boolean = runBlocking{
        var complete = false
        val productId = "123"

        val shoppingListValues = object {
            val shoppingListId = product.shoppingListId
            val categoryIcon = product.categoryIcon
            val productName = product.productName
            val quantity = product.quantity
        }

        database
            .child(DatabaseMainObject.users)
            .child(productId)
            .setValue(shoppingListValues)
            .addOnCompleteListener {
                complete = true
            }

        return@runBlocking complete
    }

    fun readUser(username: String) {

    }

    fun readShoppingLists(id: String) {

    }

    fun readProducts(id: String) {

    }
}