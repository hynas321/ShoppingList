package com.example.shoppinglist.manager

import com.example.shoppinglist.property.DatabaseMainObject
import com.example.shoppinglist.model.UserModel
import com.example.shoppinglist.model.ProductModel
import com.example.shoppinglist.model.ShoppingListModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.database.DataSnapshot
import java.util.Properties

class DatabaseManager {
    private val configFile = javaClass.classLoader?.getResourceAsStream("config.properties")
    private val properties = Properties().apply {
        configFile?.use { inputStream ->
            load(inputStream)
        }
    }
    private val connectionString: String = properties.getProperty("connection.string")
    private var databaseReference = FirebaseDatabase.getInstance(connectionString).reference

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
                        val key = childSnapshot.key.toString()

                        removeAllShoppingLists(username)
                        databaseReference.child(DatabaseMainObject.users).child(key).removeValue()
                        return
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

    fun removeAllShoppingLists(username: String) {

        databaseReference
            .child(DatabaseMainObject.shoppingLists)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()

                        databaseReference.child(DatabaseMainObject.shoppingLists).child(key).removeValue()
                        removeAllProductsFromShoppingList(username, shoppingList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun updateUser(oldUser: UserModel, newUser: UserModel) {
        val userValues = mapOf(
            "email" to newUser.email,
            "password" to newUser.password
        )

        databaseReference
            .child(DatabaseMainObject.users)
            .orderByChild("username")
            .equalTo(oldUser.username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()

                        databaseReference.child(DatabaseMainObject.users).child(key).updateChildren(userValues)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun updateShoppingList(username: String, oldShoppingList: ShoppingListModel, newShoppingList: ShoppingListModel) {
        val shoppingListValues = mapOf(
            "shoppingListName" to newShoppingList.shoppingListName
        )

        val productValues = mapOf(
            "shoppingListName" to newShoppingList.shoppingListName
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

                        if (shoppingListName == oldShoppingList.shoppingListName) {
                            databaseReference.child(DatabaseMainObject.shoppingLists).child(key).updateChildren(shoppingListValues)
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        databaseReference
            .child(DatabaseMainObject.products)
            .orderByChild("username")
            .equalTo(username)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key.toString()
                        val shoppingListName = childSnapshot.child("shoppingListName").value.toString()

                        if (shoppingListName == oldShoppingList.shoppingListName) {
                            databaseReference.child(DatabaseMainObject.products).child(key).updateChildren(productValues)
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

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getUser(username: String): CompletableDeferred<UserModel> = GlobalScope.async {
        val deferred = CompletableDeferred<UserModel>()

        databaseReference
            .child(DatabaseMainObject.users)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val user = childSnapshot.child("username").value.toString()
                        val email = childSnapshot.child("email").value.toString()
                        val password = childSnapshot.child("password").value.toString()

                        if (user == username) {
                            deferred.complete(UserModel(user, email, password))
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.complete(UserModel("???", "???", ""))
                }
            })

        deferred
    }.await()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getAllShoppingLists(username: String):
            CompletableDeferred<ArrayList<ShoppingListModel>> = GlobalScope.async {
        val deferred = CompletableDeferred<ArrayList<ShoppingListModel>>()
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

                    deferred.complete(shoppingLists)
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.complete(shoppingLists)
                }
            })

        deferred
    }.await()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getAllProducts(username: String, shoppingListName: String):
            CompletableDeferred<ArrayList<ProductModel>> = GlobalScope.async {
        val deferred = CompletableDeferred<ArrayList<ProductModel>>()
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

                    deferred.complete(products)
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.complete(products)
                }
            })

        deferred
    }.await()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun checkIfUserExists(username: String): CompletableDeferred<Boolean> = GlobalScope.async {
        val deferred = CompletableDeferred<Boolean>()

        databaseReference
            .child(DatabaseMainObject.users)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val user = childSnapshot.child("username").value.toString()

                        if (user == username) {
                            deferred.complete(true)
                            return
                        }
                    }

                    deferred.complete(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.complete(false)
                }
            })

        deferred
    }.await()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun checkIfShoppingListExists(username: String, shoppingListName: String): CompletableDeferred<Boolean> = GlobalScope.async {
        val deferred = CompletableDeferred<Boolean>()

        databaseReference
            .child(DatabaseMainObject.shoppingLists)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()

                        if (shoppingList == shoppingListName) {
                            deferred.complete(true)
                            return
                        }
                    }

                    deferred.complete(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.complete(false)
                }
            })

        deferred
    }.await()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun checkIfProductExists(username: String, shoppingListName: String, productName: String): CompletableDeferred<Boolean> = GlobalScope.async {
        val deferred = CompletableDeferred<Boolean>()

        databaseReference
            .child(DatabaseMainObject.products)
            .orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val shoppingList = childSnapshot.child("shoppingListName").value.toString()
                        val product = childSnapshot.child("productName").value.toString()

                        if (shoppingList == shoppingListName && product == productName) {
                            deferred.complete(true)
                            return
                        }
                    }

                    deferred.complete(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.complete(false)
                }
            })

        deferred
    }.await()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun authenticateUserAccount(inputUsername: String, inputPassword: String): CompletableDeferred<Boolean> = GlobalScope.async {
        val deferred = CompletableDeferred<Boolean>()

        databaseReference
            .child(DatabaseMainObject.users)
            .orderByChild("username")
            .equalTo(inputUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val username = childSnapshot.child("username").value.toString()
                        val password = childSnapshot.child("password").value.toString()

                        if (username == inputUsername &&
                            password == inputPassword) {
                            deferred.complete(true)
                        }
                    }

                    deferred.complete(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.complete(false)
                }
            })

        deferred
    }.await()

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