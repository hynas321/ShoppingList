package com.example.shoppinglist.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.shoppinglist.R
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.UserModel
import kotlinx.android.synthetic.main.custom_navigation_bar_1.view.*
import kotlinx.coroutines.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var activityManager: ActivityManager
    private lateinit var databaseManager: DatabaseManager
    private lateinit var user: UserModel

    private lateinit var username: String
    private lateinit var shoppingListName: String
    private lateinit var previousActivityName: String

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var changeUsernameButton: Button
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

        usernameTextView = findViewById(R.id.settings_username_textView)
        emailTextView = findViewById(R.id.settings_email_textView)
        changeUsernameButton = findViewById(R.id.settings_change_username_button)
        changePasswordButton = findViewById(R.id.settings_change_password_button)
        changeEmailButton = findViewById(R.id.settings_change_email_button)
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

        logOutButton.setOnClickListener {
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
}