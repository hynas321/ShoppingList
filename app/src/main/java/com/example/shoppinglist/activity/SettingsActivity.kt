package com.example.shoppinglist.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.shoppinglist.R
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.UserModel
import kotlinx.android.synthetic.main.custom_navigation_bar_1.view.*
import kotlinx.coroutines.*

class SettingsActivity : AppCompatActivity() {
    private val context: AppCompatActivity = this

    private lateinit var activityManager: ActivityManager
    private lateinit var databaseManager: DatabaseManager
    private lateinit var user: UserModel

    private lateinit var username: String
    private lateinit var shoppingListName: String
    private lateinit var previousActivityName: String

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var removeAccountButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var changeEmailButton: Button
    private lateinit var logOutButton: Button
    private lateinit var navigationBar: ConstraintLayout
    private lateinit var navigationBarListButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        overridePendingTransition(0, 0)

        activityManager = ActivityManager(this)
        databaseManager = DatabaseManager()

        username = intent.getStringExtra("string1").toString()
        shoppingListName = intent.getStringExtra("string2").toString()
        previousActivityName = intent.getStringExtra("string3").toString()

        usernameTextView = findViewById(R.id.settings_username_set_textView)
        emailTextView = findViewById(R.id.settings_email_set_textView)
        changePasswordButton = findViewById(R.id.settings_change_password_button)
        changeEmailButton = findViewById(R.id.settings_change_email_button)
        removeAccountButton = findViewById(R.id.settings_remove_account_button)
        logOutButton = findViewById(R.id.settings_button_log_out)
        navigationBar = findViewById(R.id.custom_navigation_bar_2)
        navigationBarListButton = navigationBar.list_icon

        CoroutineScope(Dispatchers.Main).launch {
            user = withContext(Dispatchers.IO) {
                databaseManager.getUser(username).await()
            }

            usernameTextView.text = user.username
            emailTextView.text = user.email
        }

        changePasswordButton.setOnClickListener {
            displayChangePasswordAlertDialog()
        }

        changeEmailButton.setOnClickListener {
            displayChangeEmailAlertDialog()
        }

        removeAccountButton.setOnClickListener {
            displayRemoveAccountAlertDialog()
        }

        logOutButton.setOnClickListener {
            Toast.makeText(context, "You have logged out", Toast.LENGTH_SHORT).show()
            activityManager.startActivityWithResources("", LoginActivity::class.java)
        }

        navigationBarListButton.setOnClickListener {
            if (previousActivityName == "ProductListActivity") {
                activityManager.startActivityWithResources(username, shoppingListName, ProductListActivity::class.java)
            }
            else {
                activityManager.startActivityWithResources(username, ShoppingListActivity::class.java)
            }
        }
    }

    private fun displayChangePasswordAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        builder.setTitle("Set your new password")
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val newUser = UserModel(user.username, user.email, input.text.toString())

            databaseManager.updateUser(user, newUser)

            Toast.makeText(context, "New password has been set", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun displayChangeEmailAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        builder.setTitle("Set your new email address")
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val newUser = UserModel(user.username, input.text.toString(), user.password)

            databaseManager.updateUser(user, newUser)

            Toast.makeText(context, "New email address: ${input.text}", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun displayRemoveAccountAlertDialog() {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Deletion of account cannot be undone. Proceed?")

        builder.setPositiveButton("OK") { _, _ ->
            databaseManager.removeUser(username)

            Toast.makeText(context, "Account has been deleted", Toast.LENGTH_SHORT).show()

            activityManager.startActivityWithResources("", LoginActivity::class.java)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}