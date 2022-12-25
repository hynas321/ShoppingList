package com.example.shoppinglist.adapter

import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.content.Context
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.activity.ProductListActivity
import com.example.shoppinglist.manager.ActivityManager
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.ProductModel
import com.example.shoppinglist.model.ShoppingListModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ShoppingListAdapter(
    private val context: Context,
    private val shoppingListModels: ArrayList<ShoppingListModel>,
    private val username: String
    ) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private val databaseManager: DatabaseManager = DatabaseManager()
    private val activityManager: ActivityManager = ActivityManager(context)

    private val undoMessage: String = context.getString(R.string.adapter_message_undo)
    private val addedMessage: String = context.getString(R.string.adapter_message_added)
    private val removedMessage: String = context.getString(R.string.adapter_message_removed)
    private val renamedMessage: String = context.getString(R.string.adapter_message_renamed)
    private val positiveButtonAlertDialog: String = context.getString(R.string.shopping_list_alertDialog_positive_button)
    private val negativeButtonAlertDialog: String = context.getString(R.string.shopping_list_alertDialog_negative_button)
    private val renameShoppingListTitleAlertDialog: String = context.getString(R.string.adapter_alertDialog_rename_shopping_list)
    private val nameShoppingListHintAlertDialog: String = context.getString(R.string.adapter_alertDialog_new_shopping_list_name)

    inner class ShoppingListViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val shoppingListIcon: ImageView
        val shoppingListName: TextView
        val shoppingListExtensionIcon: ImageView

        init {
            shoppingListIcon = itemView.findViewById(R.id.imageView_icon)
            shoppingListName = itemView.findViewById(R.id.textView_name)
            shoppingListExtensionIcon = itemView.findViewById(R.id.imageView_trash_bin_icon)

            itemView.setOnClickListener {
                activityManager.startActivityWithResources(username, shoppingListName.text.toString(), ProductListActivity::class.java)
            }

            shoppingListExtensionIcon.setOnClickListener {
                showPopup(shoppingListExtensionIcon, adapterPosition)
            }
        }
    }

    private fun showPopup(itemView: View, position: Int) {
        val popup = PopupMenu(context, itemView)
        val inflater: MenuInflater = popup.menuInflater

        inflater.inflate(R.menu.shopping_list_menu, popup.menu)
        popup.show()

        popup.setOnMenuItemClickListener { item ->
            onMenuItemClick(item, itemView, position)
        }
    }

    private fun onMenuItemClick (item: MenuItem, itemView: View, position: Int): Boolean {
        when (item.itemId) {

            R.id.shopping_list_menu_delete -> {
                val removedShoppingList = shoppingListModels[position]
                var removedProductsInShoppingList: ArrayList<ProductModel> = ArrayList()

                CoroutineScope(Dispatchers.Main).launch {
                    removedProductsInShoppingList = withContext(Dispatchers.IO) {
                        databaseManager.getAllProducts(username, removedShoppingList.shoppingListName).await()
                    }
                }

                removeItem(position)

                Snackbar
                    .make(itemView, "$removedMessage " + removedShoppingList.shoppingListName, Snackbar.LENGTH_LONG)
                    .setAction(undoMessage) { insertItem(removedShoppingList, removedProductsInShoppingList) }
                    .show()

                return true
            }

            R.id.shopping_list_menu_rename -> {
                val renamedShoppingList = shoppingListModels[position]

                showRenameShoppingListAlertDialog(position, itemView, renamedShoppingList)

                return true
            }

            else -> return false
        }
    }

    private fun showRenameShoppingListAlertDialog(position: Int, itemView: View, shoppingList: ShoppingListModel) {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)

        builder.setTitle(renameShoppingListTitleAlertDialog)
        input.hint = nameShoppingListHintAlertDialog
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(positiveButtonAlertDialog) { _, _ ->
            val changedShoppingList = shoppingListModels[position]

            changedShoppingList.shoppingListName = input.text.toString()

            databaseManager.updateShoppingList(username, shoppingList, changedShoppingList)

            Snackbar
                .make(itemView, "$renamedMessage " + shoppingList.shoppingListName, Snackbar.LENGTH_LONG)
                .setAction(undoMessage) { updateItem(shoppingList, position) }
                .show()

        }

        builder.setNegativeButton(negativeButtonAlertDialog) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    fun insertItem(item: ShoppingListModel, products: ArrayList<ProductModel>?) {
        databaseManager.writeShoppingList(item)

        if (products != null) {
            for (product in products) {
                databaseManager.writeProduct(product)
            }
        }

        Toast.makeText(context, "$addedMessage ${item.shoppingListName}", Toast.LENGTH_SHORT).show()
    }

    private fun removeItem(position: Int) {
        val shoppingList = shoppingListModels[position]

        databaseManager.removeShoppingList(username, shoppingList.shoppingListName)
    }

    private fun updateItem(item: ShoppingListModel, position: Int) {
        val shoppingList = shoppingListModels[position]

        databaseManager.updateShoppingList(username, shoppingList, item)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val shoppingListView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shopping_list_item, viewGroup, false)

        return ShoppingListViewHolder(shoppingListView)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingListModel = shoppingListModels[position]

        holder.shoppingListName.text = shoppingListModel.shoppingListName
        holder.shoppingListIcon.setImageResource(R.drawable.ic_shopping_cart)
        holder.shoppingListExtensionIcon.setImageResource(R.drawable.ic_vertical_dots)

    }

    override fun getItemCount() = shoppingListModels.size

}