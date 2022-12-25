package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Secure
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppinglist.R
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.manager.InternetManager
import com.example.shoppinglist.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    private lateinit var databaseManager: DatabaseManager
    private lateinit var activityManager: ActivityManager
    private lateinit var internetManager: InternetManager

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var loginWithoutAccountButton: Button
    private lateinit var editTextsWatcher: TextWatcher

    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        overridePendingTransition(0, 0)

        databaseManager = DatabaseManager()
        activityManager = ActivityManager(this)
        internetManager = InternetManager(this)

        usernameEditText = findViewById(R.id.login_editText_username)
        passwordEditText = findViewById(R.id.login_editText_password)
        loginButton = findViewById(R.id.login_button_login)
        signupButton = findViewById(R.id.login_button_signup)
        loginWithoutAccountButton = findViewById(R.id.login_button_offline)

        editTextsWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                loginButton.isEnabled =
                    usernameEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        }

        usernameEditText.addTextChangedListener(editTextsWatcher)
        passwordEditText.addTextChangedListener(editTextsWatcher)

        loginButton.setOnClickListener {
            if (!internetManager.checkInternetConnection()) {
                return@setOnClickListener
            }

            logInWithAccount()
        }

        signupButton.setOnClickListener {
            if (!internetManager.checkInternetConnection()) {
                return@setOnClickListener
            }

            activityManager.startActivity(SignupActivity::class.java)
        }

        loginWithoutAccountButton.setOnClickListener {
            logInWithoutAccount()
        }
    }

    private fun logInWithAccount() {

        CoroutineScope(Dispatchers.Main).launch {
            val authenticated = withContext(Dispatchers.IO) {
                databaseManager.authenticateUserAccount(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                ).await()
            }

            if (authenticated) {
                Toast.makeText(context, "Nice to see you ${usernameEditText.text}", Toast.LENGTH_SHORT).show()

                activityManager.startActivityWithResources(usernameEditText.text.toString(), ShoppingListActivity::class.java)
            } else {
                Toast.makeText(context, "Incorrect credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logInWithoutAccount() {
        val androidId = getAndroidId()

        val user = UserModel(
            androidId,
            "No email",
            "No password"
        )

        CoroutineScope(Dispatchers.Main).launch {
            val userExists = withContext(Dispatchers.IO) {
                databaseManager.checkIfUserExists(androidId).await()
            }

            if (userExists) {
                Toast.makeText(context, "Nice to see you", Toast.LENGTH_SHORT).show()
                activityManager.startActivityWithResources(user.username, ShoppingListActivity::class.java)
            }
            else {
                databaseManager.writeUser(user)
                    .addOnCompleteListener {
                        Toast.makeText(context, "Nice to see you", Toast.LENGTH_SHORT).show()
                        activityManager.startActivityWithResources(user.username, ShoppingListActivity::class.java)
                    }.addOnCanceledListener {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId(): String {
        return Secure.getString(contentResolver, Secure.ANDROID_ID)
    }

    override fun onBackPressed() {
        moveTaskToBack(true);
    }
}