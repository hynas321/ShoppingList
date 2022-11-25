package com.example.shoppinglist.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.model.ProductModel
import com.google.android.material.snackbar.Snackbar

class ProductListAdapter(
    private val context: Context,
    private val productModels: ArrayList<ProductModel>)
    : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val productCategoryIcon: ImageView
        val productName: TextView
        val productQuantity: TextView
        val productExtensionIcon: ImageView

        init {
            productCategoryIcon = itemView.findViewById(R.id.imageView_categoryIcon)
            productName = itemView.findViewById(R.id.textView_name)
            productQuantity = itemView.findViewById(R.id.textView_quantity)
            productExtensionIcon = itemView.findViewById(R.id.imageView_vertical_dots_icon)

            productExtensionIcon.setOnClickListener {
                showPopup(productExtensionIcon, adapterPosition)
            }
        }
    }

    fun createItemAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val inputName = EditText(context)
        val inputQuantity = EditText(context)

        builder.setTitle("Set name and quantity of your new product")

        inputName.setHint("Enter name")
        inputName.inputType = InputType.TYPE_CLASS_TEXT

        //inputQuantity.setHint("Enter quantity")
        //inputQuantity.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(inputName)
        //builder.setView(inputQuantity)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener
        {
            dialog, which ->
            val name = inputName.text.toString()
            //val quantity = inputQuantity.text.toString()
            val newModel = ProductModel(name, R.id.imageView_categoryIcon, "1")

            productModels.add(newModel)
            notifyItemInserted(productModels.size - 1)
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener
        {
                dialog, which -> dialog.cancel()
        })

        builder.show()
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
                val removedItem = productModels[position]

                removeItem(position)

                Snackbar
                    .make(itemView, "Deleted " + removedItem.productName, Snackbar.LENGTH_LONG)
                    .setAction("Undo", View.OnClickListener { insertItem(position, removedItem) })
                    .show()

                return true
            }

            R.id.shopping_list_menu_rename -> {

                changeNameAlertDialog(position)
                return true
            }

            R.id.shopping_list_menu_copy -> {
                val copiedItem = productModels[position]
                val copiedItemPosition = position + 1

                productModels.add(copiedItemPosition, copiedItem)
                notifyItemInserted(copiedItemPosition)

                Snackbar
                    .make(itemView, "Copied " + copiedItem.productName, Snackbar.LENGTH_LONG)
                    .setAction("Undo", View.OnClickListener { removeItem(copiedItemPosition) })
                    .show()

                return true
            }

            else -> return false
        }
    }

    private fun changeNameAlertDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)
        val productModel = productModels[position]

        builder.setTitle("Rename \"${productModel.productName}\" product")

        input.setHint("Enter Text")
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener
        {
            dialog, which -> productModel.productName = input.text.toString()
            notifyItemChanged(position)
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener
        {
            dialog, which -> dialog.cancel()
        })

        builder.show()
    }

    private fun insertItem(position: Int, model: ProductModel) {
        productModels.add(position, model)
        notifyItemInserted(position)
    }

    private fun removeItem(position: Int) {
        productModels.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProductViewHolder {
        val productView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product_item, viewGroup, false)

        return ProductViewHolder(productView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val shoppingListModel = productModels[position]

        holder.productCategoryIcon.setImageResource(shoppingListModel.categoryIcon)
        holder.productName.text = shoppingListModel.productName
        holder.productQuantity.text = shoppingListModel.quantity
    }

    override fun getItemCount() = productModels.size

}