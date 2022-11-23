package com.example.shoppinglist.product

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.ItemEventListener
import com.example.shoppinglist.R

class ProductListAdapter(private val productModels: ArrayList<ProductModel>)
    : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    private lateinit var listener: ItemEventListener

    inner class ProductViewHolder(itemView: View, listener: ItemEventListener)
        : RecyclerView.ViewHolder(itemView) {

        val productCategoryIcon: ImageView
        val productName: TextView
        val productQuantity: TextView
        val productExtensionIcon: ImageView

        init {
            productCategoryIcon = itemView.findViewById(R.id.imageView_icon)
            productName = itemView.findViewById(R.id.textView_name)
            productQuantity = itemView.findViewById(R.id.textView_quantity)
            productExtensionIcon = itemView.findViewById(R.id.imageView_vertical_dots_icon)

            itemView.setOnClickListener {
                listener.onClick(adapterPosition)
            }

            productExtensionIcon.setOnClickListener {

            }
        }
    }

    fun setOnItemClickListener(listener: ItemEventListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProductViewHolder {
        val productView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product_item, viewGroup, false)

        return ProductViewHolder(productView, listener)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val shoppingListModel = productModels[position]

        holder.productCategoryIcon.setImageResource(shoppingListModel.categoryIcon)
        holder.productName.text = shoppingListModel.name
        holder.productQuantity.text = shoppingListModel.quantity
    }

    override fun getItemCount() = productModels.size

}