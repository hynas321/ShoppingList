package com.example.shoppinglist.manager

import com.example.shoppinglist.property.DatabaseMainObject
import com.example.shoppinglist.model.UserModel
import com.example.shoppinglist.model.ProductModel
import com.example.shoppinglist.model.ShoppingListModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class DatabaseManager {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://shoppinglist-9f095-default-rtdb.europe-west1.firebasedatabase.app/").reference

    fun writeUser(user: UserModel): Task<Void> {

        return databaseReference
            .child(DatabaseMainObject.users)
            .child(UUID.randomUUID().toString())
            .setValue(user)
    }

    fun writeShoppingList(shoppingList: ShoppingListModel): Task<Void> {

        return databaseReference
            .child(DatabaseMainObject.shoppingLists)
            .child(UUID.randomUUID().toString())
            .setValue(shoppingList)
    }

    fun writeProduct(product: ProductModel): Task<Void> {

        return databaseReference
            .child(DatabaseMainObject.products)
            .child(UUID.randomUUID().toString())
            .setValue(product)
    }

    fun removeUser(username: String) {

        databaseReference
            .child(DatabaseMainObject.users)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun removeShoppingList(username: String, shoppingListName: String) {

        databaseReference
            .child(DatabaseMainObject.shoppingLists)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()

                        if (shoppingList == shoppingListName) {
                            removeAllProductsFromShoppingList(username, shoppingListName)
                            databaseReference.child(DatabaseMainObject.shoppingLists).child(key).removeValue()
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun removeProduct(username: String, shoppingListName: String, productName: String) {

        databaseReference
            .child(DatabaseMainObject.products)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()
                        val product = childSnapshot.child("productName").value.toString()

                        if (shoppingList == shoppingListName && product == productName) {
                            databaseReference.child(DatabaseMainObject.products).child(key).removeValue()
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun removeAllProductsFromShoppingList(username: String, shoppingListName: String) {
        databaseReference
            .child(DatabaseMainObject.products)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()

                        if (shoppingList == shoppingListName) {
                            databaseReference.child(DatabaseMainObject.products).child(key).removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun updateUser(user: UserModel) {
        TODO()
    }

    fun updateShoppingList(username: String, shoppingList: ShoppingListModel) {
        val shoppingListValues = mapOf(
            "shoppingListName" to shoppingList.shoppingListName
        )

        databaseReference
            .child(DatabaseMainObject.shoppingLists)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()
                        val shoppingListName = childSnapshot.child("shoppingListName").value.toString()

                        if (shoppingListName == shoppingList.shoppingListName) {
                            databaseReference.child(DatabaseMainObject.shoppingLists).child(key).updateChildren(shoppingListValues)
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun updateProduct(username: String, shoppingListName: String, product: ProductModel) {
        val productValues = mapOf(
            "bought" to product.bought,
        )

        databaseReference
            .child(DatabaseMainObject.products)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()
                        val productName = childSnapshot.child("productName").value.toString()

                        if (shoppingList == shoppingListName && productName == product.productName) {
                            databaseReference.child(DatabaseMainObject.products).child(key).updateChildren(productValues)
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getAllProducts(username: String, shoppingListName: String): ArrayList<ProductModel> {
        val products = ArrayList<ProductModel>()

        databaseReference
            .child(DatabaseMainObject.products)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()
                        val product = childSnapshot.child("productName").value.toString()
                        val quantity = childSnapshot.child("quantity").value.toString()
                        val bought = childSnapshot.child("bought").value

                        if (shoppingList == shoppingListName) {
                            products.add(ProductModel(username, shoppingListName, product, quantity, bought as Boolean))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        return products
    }

    fun getAllShoppingLists(username: String): ArrayList<ShoppingListModel> {
        val shoppingLists = ArrayList<ShoppingListModel>()

        databaseReference
            .child(DatabaseMainObject.products)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()

                        shoppingLists.add(ShoppingListModel(username, shoppingList))
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        return shoppingLists
    }

    fun getUsersReference(): DatabaseReference {

        return databaseReference.child(DatabaseMainObject.users)
    }

    fun getShoppingListsReference(): DatabaseReference {

        return databaseReference.child(DatabaseMainObject.shoppingLists)
    }

    fun getProductsReference(): DatabaseReference {

        return databaseReference.child(DatabaseMainObject.products)
    }
}