package com.example.shoppinglist.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.manager.DatabaseManager
import com.example.shoppinglist.model.ProductModel
import com.google.android.material.snackbar.Snackbar

class ProductListAdapter(
    private val context: Context,
    private val productModels: ArrayList<ProductModel>,
    private val username: String,
    private val shoppingListName: String)
    : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    private val databaseManager: DatabaseManager = DatabaseManager()

    private val limeColor = "#B0FFC2"
    private val whiteColor = "#FFFFFF"

    private val undoMessage: String = context.getString(R.string.adapter_message_undo)
    private val addedMessage: String = context.getString(R.string.adapter_message_added)
    private val removedMessage: String = context.getString(R.string.adapter_message_removed)

    inner class ProductViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val productBoughtCheckBox: CheckBox
        val productName: TextView
        val productQuantity: TextView
        val productTrashBinIcon: ImageView

        init {
            productBoughtCheckBox = itemView.findViewById(R.id.checkBox_bought)
            productName = itemView.findViewById(R.id.textView_name)
            productQuantity = itemView.findViewById(R.id.textView_quantity)
            productTrashBinIcon = itemView.findViewById(R.id.imageView_trash_bin_icon)

            productBoughtCheckBox.scaleX = 1.5F
            productBoughtCheckBox.scaleY = 1.5F

            productTrashBinIcon.setOnClickListener {
                val removedProduct = productModels[adapterPosition]

                databaseManager.removeProduct(username, shoppingListName, removedProduct.productName)

                Snackbar
                    .make(itemView, "$removedMessage " + removedProduct.productName, Snackbar.LENGTH_LONG)
                    .setAction(undoMessage) { insertItem(removedProduct) }
                    .show()
            }

            productBoughtCheckBox.setOnClickListener {
                val updatedProduct = productModels[adapterPosition]

                updatedProduct.bought = !updatedProduct.bought

                databaseManager.updateProduct(username, shoppingListName, updatedProduct)

                if (updatedProduct.bought) {
                    itemView.setBackgroundColor(Color.parseColor(limeColor))
                }
                else {
                    itemView.setBackgroundColor(Color.parseColor(whiteColor))
                }
            }
        }
    }

    fun insertItem(item: ProductModel) {
        databaseManager.writeProduct(item)

        Toast.makeText(context, "$addedMessage ${item.productName}", Toast.LENGTH_SHORT).show()
    }

    fun removeItem(position: Int) {
        val product = productModels[position]

        databaseManager.removeProduct(username, shoppingListName, product.productName)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProductViewHolder {
        val productView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product_item, viewGroup, false)

        return ProductViewHolder(productView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productModels[position]

        holder.productBoughtCheckBox.isChecked = false
        holder.itemView.setBackgroundColor(Color.parseColor(whiteColor))

        if (product.bought) {
            holder.productBoughtCheckBox.isChecked = true
            holder.itemView.setBackgroundColor(Color.parseColor(limeColor))
        }

        holder.productName.text = product.productName
        holder.productQuantity.text = product.quantity
    }

    override fun getItemCount() = productModels.size

}