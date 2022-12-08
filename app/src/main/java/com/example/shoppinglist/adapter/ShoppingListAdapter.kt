package com.example.shoppinglist.adapter

import android.app.AlertDialog.Builder
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.activity.ProductListActivity
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.ShoppingListModel
import com.example.shoppinglist.model.UserModel
import com.google.android.material.snackbar.Snackbar


class ShoppingListAdapter(
    private val context: Context,
    private val shoppingListModels: ArrayList<ShoppingListModel>,
    private val user: UserModel
    ) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private val databaseManager: DatabaseManager = DatabaseManager()

    inner class ShoppingListViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val shoppingListIcon: ImageView
        val shoppingListName: TextView
        val shoppingListExtensionIcon: ImageView

        init {
            shoppingListIcon = itemView.findViewById(R.id.imageView_icon)
            shoppingListName = itemView.findViewById(R.id.textView_name)
            shoppingListExtensionIcon = itemView.findViewById(R.id.imageView_vertical_dots_icon)

            itemView.setOnClickListener {
                openProductListActivity()
            }

            shoppingListExtensionIcon.setOnClickListener {
                showPopup(shoppingListExtensionIcon, adapterPosition)
            }
        }
    }

    fun createItemAlertDialog() {
        val builder = Builder(context)
        val input = EditText(context)

        builder.setTitle("Set name of your new shopping list")
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val shoppingList = ShoppingListModel(input.text.toString(), R.id.imageView_icon)

            insertItem(shoppingList)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun openProductListActivity() {
        val intent = Intent(context, ProductListActivity::class.java)

        intent.putExtra("id", "1")
        startActivity(context, intent, null)
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

                removeItem(position)

                Snackbar
                    .make(itemView, "Deleted " + removedShoppingList.shoppingListName, Snackbar.LENGTH_LONG)
                    .setAction("Undo") { insertItem(removedShoppingList) }
                    .show()

                return true
            }

            R.id.shopping_list_menu_rename -> {
                val renamedShoppingList = shoppingListModels[position]

                Snackbar
                    .make(itemView, "Renamed " + renamedShoppingList.shoppingListName, Snackbar.LENGTH_LONG)
                    .setAction("Undo") { insertItem(renamedShoppingList) }
                    .show()

                return true
            }

            R.id.shopping_list_menu_share -> {

                return true
            }

            else -> return false
        }
    }

    private fun insertItem(item: ShoppingListModel) {
        databaseManager.writeShoppingList(user.username, item)

        Toast.makeText(context, "Added ${item.shoppingListName}", Toast.LENGTH_SHORT).show()
    }

    private fun removeItem(position: Int) {
        val shoppingList = shoppingListModels[position]

        databaseManager.removeShoppingList(user.username, shoppingList.shoppingListName)
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