package com.example.shoppinglist.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppinglist.*
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity: AppCompatActivity()  {
    private val context: AppCompatActivity = this

    private lateinit var databaseManager: DatabaseManager
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
            val user = UserModel(
                usernameEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )

            CoroutineScope(Dispatchers.Main).launch {
                val userExists = withContext(Dispatchers.IO) {
                    databaseManager.checkIfUserExists(user.username).await()
                }

                if (userExists) {
                    Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()
                }
                else {
                    databaseManager.writeUser(user)
                        .addOnCompleteListener {
                            Toast.makeText(context, "Welcome ${user.username}!", Toast.LENGTH_SHORT).show()
                            activityManager.startActivityWithResources(user.username, ShoppingListActivity::class.java)
                        }.addOnCanceledListener {
                            Toast.makeText(context, "Registration error", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Registration error", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}