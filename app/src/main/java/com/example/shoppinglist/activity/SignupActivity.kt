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
import java.util.regex.Pattern

class SignupActivity: AppCompatActivity()  {
    private val context: AppCompatActivity = this

    private lateinit var databaseManager: DatabaseManager
    private lateinit var activityManager: ActivityManager

    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var editTextsWatcher: TextWatcher

    private lateinit var userExistsToastMessage: String
    private lateinit var registrationSuccessfulToastMessage: String
    private lateinit var registrationErrorToastMessage: String
    private lateinit var incorrectEmailFormatToastMessage: String

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

        userExistsToastMessage = getString(R.string.signup_toast_user_exists)
        registrationSuccessfulToastMessage = getString(R.string.signup_toast_registration_successful)
        registrationErrorToastMessage = getString(R.string.signup_toast_registration_error)
        incorrectEmailFormatToastMessage = getString(R.string.signup_toast_incorrect_email_format)

        editTextsWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                signupButton.isEnabled =
                    emailEditText.text.isNotEmpty() && usernameEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() &&
                            usernameEditText.text.length >= 5 && usernameEditText.text.length <= 15 &&
                            passwordEditText.text.length >= 5 && passwordEditText.text.length <= 15
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

            if (!isValidEmail(emailEditText.text.toString())) {
                Toast.makeText(context, incorrectEmailFormatToastMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val userExists = withContext(Dispatchers.IO) {
                    databaseManager.checkIfUserExists(user.username).await()
                }

                if (userExists) {
                    Toast.makeText(context, userExistsToastMessage, Toast.LENGTH_SHORT).show()
                }
                else {
                    databaseManager.writeUser(user)
                        .addOnCompleteListener {
                            Toast.makeText(context, "$registrationSuccessfulToastMessage ${user.username}!", Toast.LENGTH_SHORT).show()
                            activityManager.startActivityWithResources(user.username, ShoppingListActivity::class.java)
                        }.addOnFailureListener {
                            Toast.makeText(context, registrationErrorToastMessage, Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
        )
        return pattern.matcher(email).matches()
    }
}