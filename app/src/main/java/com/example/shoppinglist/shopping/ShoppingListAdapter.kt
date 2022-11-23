package com.example.shoppinglist.shopping

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.ItemEventListener
import com.example.shoppinglist.R

class ShoppingListAdapter(private val shoppingListModels: ArrayList<ShoppingListModel>)
    : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private lateinit var listener: ItemEventListener

    inner class ShoppingListViewHolder(itemView: View, listener: ItemEventListener)
        : RecyclerView.ViewHolder(itemView) {

        val shoppingListIcon: ImageView
        val shoppingListName: TextView
        val shoppingListExtensionIcon: ImageView

        init {
            shoppingListIcon = itemView.findViewById(R.id.imageView_icon)
            shoppingListName = itemView.findViewById(R.id.textView_name)
            shoppingListExtensionIcon = itemView.findViewById(R.id.imageView_vertical_dots_icon)

            itemView.setOnClickListener {
                listener.onClick(adapterPosition)
            }

            shoppingListExtensionIcon.setOnClickListener {

            }
        }
    }

    fun setOnItemClickListener(listener: ItemEventListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val shoppingListView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shopping_list_item, viewGroup, false)

        return ShoppingListViewHolder(shoppingListView, listener)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingListModel = shoppingListModels[position]

        holder.shoppingListName.text = shoppingListModel.name
        holder.shoppingListIcon.setImageResource(shoppingListModel.iconImageViewId)
        holder.shoppingListExtensionIcon.setImageResource(R.drawable.ic_vertical_dots)
    }

    override fun getItemCount() = shoppingListModels.size

}