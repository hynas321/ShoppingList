package com.example.shoppinglist.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
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
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.*
import java.util.regex.Pattern

class SettingsActivity : AppCompatActivity() {
    private val context: AppCompatActivity = this

    private lateinit var activityManager: ActivityManager
    private lateinit var databaseManager: DatabaseManager
    private lateinit var user: UserModel

    private lateinit var username: String
    private lateinit var shoppingListName: String
    private lateinit var previousActivityName: String

    private lateinit var toolbar: View
    private lateinit var toolbarTitle: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var removeAccountButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var changeEmailButton: Button
    private lateinit var logOutButton: Button
    private lateinit var navigationBar: ConstraintLayout
    private lateinit var navigationBarListButton: View

    private lateinit var logOutToastMessage: String
    private lateinit var passwordTitleAlertDialog: String
    private lateinit var passwordHintAlertDialog: String
    private lateinit var emailTitleAlertDialog: String
    private lateinit var emailHintAlertDialog: String
    private lateinit var accountDeletionAlertDialog: String
    private lateinit var positiveButtonAlertDialog: String
    private lateinit var negativeButtonAlertDialog: String
    private lateinit var passwordSetToastMessage: String
    private lateinit var emailSetToastMessage: String
    private lateinit var accountDeletedToastMessage: String
    private lateinit var dataAccessErrorToastMessage: String
    private lateinit var noEmail: String
    private lateinit var incorrectEmailFormatToastMessage: String
    private lateinit var incorrectPasswordLengthToastMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        overridePendingTransition(0, 0)

        activityManager = ActivityManager(this)
        databaseManager = DatabaseManager()

        username = intent.getStringExtra("string1").toString()
        shoppingListName = intent.getStringExtra("string2").toString()
        previousActivityName = intent.getStringExtra("string3").toString()

        toolbar = findViewById(R.id.settings_toolbar)
        toolbarTitle = toolbar.toolbar_title
        usernameTextView = findViewById(R.id.settings_username_set_textView)
        emailTextView = findViewById(R.id.settings_email_set_textView)
        changePasswordButton = findViewById(R.id.settings_change_password_button)
        changeEmailButton = findViewById(R.id.settings_change_email_button)
        removeAccountButton = findViewById(R.id.settings_remove_account_button)
        logOutButton = findViewById(R.id.settings_button_log_out)
        navigationBar = findViewById(R.id.custom_navigation_bar_2)
        navigationBarListButton = navigationBar.list_icon

        logOutToastMessage = getString(R.string.settings_toast_log_out)
        passwordTitleAlertDialog = getString(R.string.settings_alertDialog_password_title)
        passwordHintAlertDialog = getString(R.string.settings_alertDialog_password_hint)
        emailTitleAlertDialog = getString(R.string.settings_alertDialog_email_title)
        emailHintAlertDialog = getString(R.string.settings_alertDialog_email_hint)
        accountDeletionAlertDialog = getString(R.string.settings_alertDialog_account_deletion)
        positiveButtonAlertDialog = getString(R.string.settings_alertDialog_positive_button)
        negativeButtonAlertDialog = getString(R.string.settings_alertDialog_negative_button)
        passwordSetToastMessage = getString(R.string.settings_toast_password_set)
        emailSetToastMessage = getString(R.string.settings_toast_email_set)
        accountDeletedToastMessage = getString(R.string.settings_toast_account_deleted)
        dataAccessErrorToastMessage = getString(R.string.settings_toast_data_access_error)
        noEmail = getString(R.string.login_no_email_object_value)
        incorrectEmailFormatToastMessage = getString(R.string.signup_toast_incorrect_email_format)
        incorrectPasswordLengthToastMessage = getString(R.string.settings_toast_incorrect_password_length)

        toolbarTitle.text = getString(R.string.settings_title)

        CoroutineScope(Dispatchers.Main).launch {
            user = withContext(Dispatchers.IO) {
                databaseManager.getUser(username).await()
            }

            usernameTextView.text = user.username
            emailTextView.text = user.email

            if (user.email == noEmail) {
                changePasswordButton.isEnabled = false
                changeEmailButton.isEnabled = false
            }
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
            Toast.makeText(context, logOutToastMessage, Toast.LENGTH_SHORT).show()
            activityManager.startActivityWithResources("", LoginActivity::class.java)
        }

        navigationBarListButton.setOnClickListener {
            switchBackActivity()
        }
    }

    private fun displayChangePasswordAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        input.hint = passwordHintAlertDialog
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.height = 150
        input.gravity = Gravity.CENTER

        builder.setTitle(passwordTitleAlertDialog)
        builder.setView(input)

        builder.setPositiveButton(positiveButtonAlertDialog) { _, _ ->
            val newPassword = input.text.toString()
            val newUser = UserModel(user.username, user.email, newPassword)

            if (newPassword.length in 5..15) {
                databaseManager.updateUser(user, newUser)

                Toast.makeText(context, passwordSetToastMessage, Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, incorrectPasswordLengthToastMessage, Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(negativeButtonAlertDialog) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun displayChangeEmailAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        input.hint = emailHintAlertDialog
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.height = 150
        input.gravity = Gravity.CENTER

        builder.setTitle(emailTitleAlertDialog)
        builder.setView(input)

        builder.setPositiveButton(positiveButtonAlertDialog) { _, _ ->
            val newEmail = input.text.toString()
            val newUser = UserModel(user.username, newEmail, user.password)

            if (isValidEmail(newEmail)) {
                databaseManager.updateUser(user, newUser)

                Toast.makeText(context, "$emailSetToastMessage: ${input.text}", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, incorrectEmailFormatToastMessage, Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(negativeButtonAlertDialog) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun displayRemoveAccountAlertDialog() {
        val builder = AlertDialog.Builder(context)

        builder.setTitle(accountDeletionAlertDialog)

        builder.setPositiveButton(positiveButtonAlertDialog) { _, _ ->
            databaseManager.removeUser(username)

            Toast.makeText(context, accountDeletedToastMessage, Toast.LENGTH_SHORT).show()

            activityManager.startActivityWithResources("", LoginActivity::class.java)
        }

        builder.setNegativeButton(negativeButtonAlertDialog) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
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

    private fun switchBackActivity() {
        if (previousActivityName == "ProductListActivity") {
            activityManager.startActivityWithResources(username, shoppingListName, ProductListActivity::class.java)
        }
        else {
            activityManager.startActivityWithResources(username, ShoppingListActivity::class.java)
        }
    }

    override fun onBackPressed() {
        switchBackActivity()
    }
}