package com.example.shoppinglist.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
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
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ShoppingListActivity : AppCompatActivity() {
    private val context: AppCompatActivity = this
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

    private lateinit var databaseManager: DatabaseManager
    private lateinit var activityManager: ActivityManager

    private lateinit var shoppingListModels: ArrayList<ShoppingListModel>
    private lateinit var username: String

    private lateinit var toolbar: View
    private lateinit var toolbarTitle: TextView
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addListButton: Button
    private lateinit var noItemsTextView: TextView
    private lateinit var microphoneImageView: ImageView
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

        toolbar = findViewById(R.id.shopping_list_toolbar)
        toolbarTitle = toolbar.toolbar_title
        recyclerView = findViewById(R.id.product_list_recyclerView_product)
        addListButton = findViewById(R.id.shopping_list_button_add_list)
        noItemsTextView = findViewById(R.id.shopping_list_editText_empty)
        microphoneImageView = findViewById(R.id.shopping_list_microphone_imageView)
        navigationBar = findViewById(R.id.custom_navigation_bar_1)
        navigationBarSettingsButton = navigationBar.settings_icon

        titleAlertDialog = getString(R.string.shopping_list_alertDialog_title)
        nameHintAlertDialog = getString(R.string.shopping_list_alertDialog_name_hint)
        positiveButtonAlertDialog = getString(R.string.shopping_list_alertDialog_positive_button)
        negativeButtonAlertDialog = getString(R.string.shopping_list_alertDialog_negative_button)
        noShoppingListNameToastMessage = getString(R.string.shopping_list_toast_no_shopping_list_name)
        shoppingListExistsToastMessage = getString(R.string.shopping_list_toast_shopping_list_exists)
        dataAccessErrorToastMessage = getString(R.string.shopping_list_data_access_error)

        toolbarTitle.text = getString(R.string.shopping_list_title)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListAdapter

        addListButton.setOnClickListener {
            createItemAlertDialog()
        }

        navigationBarSettingsButton.setOnClickListener {
            activityManager.startActivityWithResources(username, "", "ShoppingListActivity", SettingsActivity::class.java)
        }

        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onResults(bundle: Bundle?) {}
            override fun onPartialResults(bundle: Bundle?) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(recognitionListener)

        microphoneImageView.setOnClickListener {
            val newIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            newIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            newIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            speechRecognizer.startListening(newIntent)
            startActivityForResult(newIntent, 0)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            0 -> {
                if (resultCode == RESULT_OK && data != null) {
                    val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val result = results?.get(0).toString()

                    shoppingListAdapter.insertItem(ShoppingListModel(username, result), null)
                }
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}