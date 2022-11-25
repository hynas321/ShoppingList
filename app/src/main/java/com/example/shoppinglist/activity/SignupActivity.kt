package com.example.shoppinglist.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppinglist.*
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.UserModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class SignupActivity: AppCompatActivity()  {
    private lateinit var databaseManager: DatabaseManager
    //private lateinit var snackBarManager: SnackBarManager
    private lateinit var activityManager: ActivityManager

    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var editTextsWatcher: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        overridePendingTransition(0, 0)

        databaseManager = DatabaseManager()
        activityManager = ActivityManager(this)

        emailEditText = findViewById(R.id.signup_editText_email)
        usernameEditText = findViewById(R.id.signup_editText_username)
        passwordEditText = findViewById(R.id.signup_editText_password)
        signupButton = findViewById(R.id.signup_button)

        editTextsWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                signupButton.isEnabled =
                    emailEditText.text.isNotEmpty() && usernameEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        }

        usernameEditText.addTextChangedListener(editTextsWatcher)
        passwordEditText.addTextChangedListener(editTextsWatcher)

        signupButton.setOnClickListener {
            val userModel = UserModel(
                usernameEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )

            val writeSuccessful = databaseManager.writeUser(userModel)

            if (writeSuccessful) {
                activityManager.startActivityWithResources(userModel, ShoppingListActivity::class.java)
            }
            else {
                activityManager.startActivityWithResources(userModel, LoginActivity::class.java)
            }
        }
    }
}