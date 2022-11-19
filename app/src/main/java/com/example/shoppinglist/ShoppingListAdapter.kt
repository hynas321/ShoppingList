package com.example.shoppinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShoppingListAdapter(private val shoppingListModels: ArrayList<ShoppingListModel>)
    : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private lateinit var listener: ItemEventListener

    inner class ShoppingListViewHolder(itemView: View, listener: ItemEventListener)
        : RecyclerView.ViewHolder(itemView){

        val shoppingListIcon: ImageView
        val shoppingListName: TextView
        val shoppingListDeleteIcon: ImageView

        init {
            shoppingListIcon = itemView.findViewById(R.id.shopping_list_item_icon)
            shoppingListName = itemView.findViewById(R.id.shopping_list_item_textView_name)
            shoppingListDeleteIcon = itemView.findViewById(R.id.shopping_list_item_delete_icon)

            itemView.setOnClickListener {
                listener.onClick(adapterPosition)
            }

            shoppingListDeleteIcon.setOnClickListener {
                listener.onDeleteClick(adapterPosition)
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
        holder.shoppingListDeleteIcon.setImageResource(shoppingListModel.deleteIconImageViewId)
    }

    override fun getItemCount() = shoppingListModels.size

}