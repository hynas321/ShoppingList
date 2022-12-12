package com.example.shoppinglist.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.shoppinglist.R
import com.example.shoppinglist.manager.ActivityManager
import kotlinx.android.synthetic.main.activity_product_list.view.*
import kotlinx.android.synthetic.main.custom_navigation_bar_1.view.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var activityManager: ActivityManager

    private lateinit var username: String
    private lateinit var shoppingListName: String
    private lateinit var previousActivityName: String

    private lateinit var logOutButton: Button
    private lateinit var navigationBar: ConstraintLayout
    private lateinit var navigationBarListButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        overridePendingTransition(0, 0)

        activityManager = ActivityManager(this)

        username = intent.getStringExtra("string1").toString()
        shoppingListName = intent.getStringExtra("string2").toString()
        previousActivityName = intent.getStringExtra("string3").toString()

        logOutButton = findViewById(R.id.settings_button_log_out)
        navigationBar = findViewById(R.id.custom_navigation_bar_2)
        navigationBarListButton = navigationBar.list_icon

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