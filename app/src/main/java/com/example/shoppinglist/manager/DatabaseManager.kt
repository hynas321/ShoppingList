package com.example.shoppinglist.manager

import com.example.shoppinglist.property.DatabaseMainObject
import com.example.shoppinglist.model.UserModel
import com.example.shoppinglist.model.ProductModel
import com.example.shoppinglist.model.ShoppingListModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class DatabaseManager {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://shoppinglist-9f095-default-rtdb.europe-west1.firebasedatabase.app/").reference

    fun writeUser(user: UserModel): Task<Void> {
        val userValues = object {
            val email: String = user.email
            val password: String = user.password
        }

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(user.username)
            .setValue(userValues)
    }

    fun writeShoppingList(username: String, shoppingList: ShoppingListModel): Task<Void> {
        val shoppingListValues = object {
            val iconImageViewId = shoppingList.iconImageViewId
        }

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
            .child(shoppingList.shoppingListName)
            .setValue(shoppingListValues)
    }

    fun writeProduct(username: String, shoppingListName: String, product: ProductModel): Task<Void> {
        val productValues = object {
            val categoryIcon = product.categoryIcon
            var quantity = product.quantity
        }

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
            .child(shoppingListName)
            .child(DatabaseMainObject.products)
            .child(product.productName)
            .setValue(productValues)
    }

    fun removeUser(username: String): Task<Void> {

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .removeValue()
    }

    fun removeShoppingList(username: String, shoppingListName: String): Task<Void> {
        return databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
            .child(shoppingListName)
            .removeValue()
    }

    fun removeProduct(username: String, shoppingListName: String, productName: String): Task<Void> {

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
            .child(shoppingListName)
            .child(DatabaseMainObject.products)
            .child(productName)
            .removeValue()
    }

    fun updateUser(user: UserModel) {
        val userValues = mapOf(
            "email" to user.email,
            "password" to user.password,
        )

        databaseReference
            .child(DatabaseMainObject.users)
            .child(user.username)
            .updateChildren(userValues)
    }

    fun updateShoppingList(username: String, shoppingList: ShoppingListModel) {
        val shoppingListValues = mapOf(
            "iconImageViewId" to shoppingList.iconImageViewId
        )

        databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
            .child(shoppingList.shoppingListName)
            .updateChildren(shoppingListValues)
    }

    fun updateProduct(username: String, shoppingListName: String, product: ProductModel) {
        val productValues = mapOf(
            "bought" to product.bought,
            "categoryIcon" to product.categoryIcon,
            "quantity" to product.quantity
        )

        databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
            .child(shoppingListName)
            .child(DatabaseMainObject.products)
            .child(product.productName)
            .updateChildren(productValues)
    }

    fun getUsersReference(): DatabaseReference {

        return databaseReference.child(DatabaseMainObject.users)
    }

    fun getShoppingListsReference(username: String): DatabaseReference {

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
    }

    fun getProductsReference(username: String, shoppingListName: String): DatabaseReference {

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(username)
            .child(DatabaseMainObject.shoppingLists)
            .child(shoppingListName)
            .child(DatabaseMainObject.products)
    }
}