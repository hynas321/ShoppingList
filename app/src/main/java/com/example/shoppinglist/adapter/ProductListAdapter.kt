package com.example.shoppinglist.adapter

import android.content.Context
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

    inner class ProductViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val productBoughtCheckBox: CheckBox
        val productCategoryIcon: ImageView
        val productName: TextView
        val productQuantity: TextView
        val productTrashBinIcon: ImageView

        init {
            productBoughtCheckBox = itemView.findViewById(R.id.checkBox_bought)
            productCategoryIcon = itemView.findViewById(R.id.imageView_categoryIcon)
            productName = itemView.findViewById(R.id.textView_name)
            productQuantity = itemView.findViewById(R.id.textView_quantity)
            productTrashBinIcon = itemView.findViewById(R.id.imageView_trash_bin_icon)

            productTrashBinIcon.setOnClickListener {
                val removedProduct = productModels[adapterPosition]

                databaseManager.removeProduct(username, shoppingListName, removedProduct.productName)

                Snackbar
                    .make(itemView, "Deleted " + removedProduct.productName, Snackbar.LENGTH_LONG)
                    .setAction("Undo") { insertItem(removedProduct) }
                    .show()
            }

            productBoughtCheckBox.setOnClickListener {
                val updatedProduct = productModels[adapterPosition]

                updatedProduct.bought = !updatedProduct.bought

                databaseManager.updateProduct(username, shoppingListName, updatedProduct)
            }
        }
    }

    fun insertItem(item: ProductModel) {
        databaseManager.writeProduct(username, shoppingListName, item)

        Toast.makeText(context, "Added ${item.productName}", Toast.LENGTH_SHORT).show()
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

        if (product.bought) {
            holder.productBoughtCheckBox.isChecked = true
        }

        holder.productCategoryIcon.setImageResource(R.drawable.ic_shopping_bag)
        holder.productName.text = product.productName
        holder.productQuantity.text = product.quantity
    }

    override fun getItemCount() = productModels.size

}