package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.adapter.ShoppingListAdapter
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.ShoppingListModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.custom_navigation_bar_1.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ShoppingListActivity : AppCompatActivity() {
    private val context: AppCompatActivity = this

    private lateinit var databaseManager: DatabaseManager
    private lateinit var activityManager: ActivityManager

    private lateinit var shoppingListModels: ArrayList<ShoppingListModel>
    private lateinit var username: String

    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addListButton: Button
    private lateinit var noItemsTextView: TextView
    private lateinit var navigationBar: ConstraintLayout
    private lateinit var navigationBarSettingsButton: View

    private lateinit var titleAlertDialog: String
    private lateinit var nameHintAlertDialog: String
    private lateinit var positiveButtonAlertDialog: String
    private lateinit var negativeButtonAlertDialog: String
    private lateinit var noShoppingListNameToastMessage: String
    private lateinit var shoppingListExistsToastMessage: String
    private lateinit var dataAccessErrorToastMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        overridePendingTransition(0, 0)

        databaseManager = DatabaseManager()
        activityManager = ActivityManager(context)
        shoppingListModels = ArrayList()

        username = intent.getStringExtra("string").toString()

        setShoppingListModelsChangeEvent()

        linearLayoutManager = LinearLayoutManager(this)
        shoppingListAdapter = ShoppingListAdapter(this, shoppingListModels, username)

        recyclerView = findViewById(R.id.product_list_recyclerView_product)
        addListButton = findViewById(R.id.shopping_list_button_add_list)
        noItemsTextView = findViewById(R.id.shopping_list_editText_empty)
        navigationBar = findViewById(R.id.custom_navigation_bar_1)
        navigationBarSettingsButton = navigationBar.settings_icon

        titleAlertDialog = getString(R.string.shopping_list_alertDialog_title)
        nameHintAlertDialog = getString(R.string.shopping_list_alertDialog_name_hint)
        positiveButtonAlertDialog = getString(R.string.shopping_list_alertDialog_positive_button)
        negativeButtonAlertDialog = getString(R.string.shopping_list_alertDialog_negative_button)
        noShoppingListNameToastMessage = getString(R.string.shopping_list_toast_no_shopping_list_name)
        shoppingListExistsToastMessage = getString(R.string.shopping_list_toast_shopping_list_exists)
        dataAccessErrorToastMessage = getString(R.string.shopping_list_data_access_error)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListAdapter

        addListButton.setOnClickListener {
            createItemAlertDialog()
        }

        navigationBarSettingsButton.setOnClickListener {
            activityManager.startActivityWithResources(username, "", "ShoppingListActivity", SettingsActivity::class.java)
        }
    }

    private fun createItemAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        input.hint = nameHintAlertDialog
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.height = 150
        input.gravity = Gravity.CENTER

        builder.setTitle(titleAlertDialog)
        builder.setView(input)

        builder.setPositiveButton(positiveButtonAlertDialog) { _, _ ->
            val shoppingListName = input.text.toString()

            if (shoppingListName == "") {
                Toast.makeText(context, noShoppingListNameToastMessage, Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val shoppingList = ShoppingListModel(username, shoppingListName)

            CoroutineScope(Dispatchers.Main).launch {
                val shoppingListExists = withContext(Dispatchers.IO) {
                    databaseManager.checkIfShoppingListExists(username, shoppingList.shoppingListName).await()
                }

                if (!shoppingListExists) {
                    shoppingListAdapter.insertItem(shoppingList, null)
                }
                else {
                    Toast.makeText(context, shoppingListExistsToastMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton(negativeButtonAlertDialog) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setShoppingListModelsChangeEvent() {
        databaseManager
            .getShoppingListsReference()
            .orderByChild("username")
            .equalTo(username)
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    shoppingListModels.clear()

                    for (dataSnapshot in snapshot.children) {
                        val shoppingListName = dataSnapshot?.child("shoppingListName")?.value.toString()
                        val shoppingListModel = ShoppingListModel(username, shoppingListName)

                        shoppingListModels.add(shoppingListModel)
                    }

                    shoppingListModels.sortWith(compareBy {it.shoppingListName} )
                    shoppingListAdapter.notifyDataSetChanged()

                    if (shoppingListModels.isEmpty()) {
                        noItemsTextView.visibility = View.VISIBLE
                    }
                    else {
                        noItemsTextView.visibility = View.INVISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, dataAccessErrorToastMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}