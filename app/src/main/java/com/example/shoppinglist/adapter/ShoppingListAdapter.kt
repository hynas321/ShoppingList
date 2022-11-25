package com.example.shoppinglist.adapter

import android.app.AlertDialog.Builder
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.activity.ProductListActivity
import com.example.shoppinglist.model.ShoppingListModel
import com.google.android.material.snackbar.Snackbar


class ShoppingListAdapter(
    private val context: Context,
    private val shoppingListModels: ArrayList<ShoppingListModel>)
    : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

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

        builder.setPositiveButton("OK", DialogInterface.OnClickListener
        {
                dialog, which ->
            val name = input.text.toString()
            val newModel = ShoppingListModel("A", R.id.imageView_icon)

            shoppingListModels.add(newModel)
            notifyItemInserted(shoppingListModels.size - 1)
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener
        {
                dialog, which -> dialog.cancel()
        })

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
                val removedItem = shoppingListModels[position]

                removeItem(position)

                Snackbar
                    .make(itemView, "Deleted " + removedItem.shoppingListName, Snackbar.LENGTH_LONG)
                    .setAction("Undo", View.OnClickListener { insertItem(position, removedItem) })
                    .show()

                return true
            }

            R.id.shopping_list_menu_rename -> {

                changeNameAlertDialog(position)
                return true
            }

            R.id.shopping_list_menu_copy -> {
                val copiedItem = shoppingListModels[position]
                val copiedItemPosition = position + 1

                shoppingListModels.add(copiedItemPosition, copiedItem)
                notifyItemInserted(copiedItemPosition)

                Snackbar
                    .make(itemView, "Copied " + copiedItem.shoppingListName, Snackbar.LENGTH_LONG)
                    .setAction("Undo", View.OnClickListener { removeItem(copiedItemPosition) })
                    .show()

                return true
            }

            else -> return false
        }
    }

    private fun changeNameAlertDialog(position: Int) {
        val builder = Builder(context)
        val input = EditText(context)
        val shoppingListModel = shoppingListModels[position]

        builder.setTitle("Rename \"${shoppingListModel.shoppingListName}\" shopping list")
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener
        {
            dialog, which -> shoppingListModel.shoppingListName = input.text.toString()
            notifyItemChanged(position)
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener
        {
            dialog, which -> dialog.cancel()
        })

        builder.show()
    }

    private fun insertItem(position: Int, model: ShoppingListModel) {
        shoppingListModels.add(position, model)
        notifyItemInserted(position)
    }

    private fun removeItem(position: Int) {
        shoppingListModels.removeAt(position)
        notifyItemRemoved(position)
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