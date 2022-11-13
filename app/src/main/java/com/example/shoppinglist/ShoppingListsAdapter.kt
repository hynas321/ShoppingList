package com.example.shoppinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShoppingListsAdapter(private val shoppingListModels: ArrayList<ShoppingListModel>) :
    RecyclerView.Adapter<ShoppingListsAdapter.ShoppingListViewHolder>() {

    private val clickListener: ClickListener? = null

    inner class ShoppingListViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        val shoppingListIcon: ImageView
        val shoppingListName: TextView
        val shoppingListDeleteIcon: ImageView

        init {
            shoppingListIcon = view.findViewById(R.id.shopping_list_item_icon)
            shoppingListName = view.findViewById(R.id.shopping_list_item_textView_name)
            shoppingListDeleteIcon = view.findViewById(R.id.shopping_list_item_delete_icon)

            view.setOnClickListener(this)
            view.setOnLongClickListener(this)

        }

        override fun onClick(v: View?) {
            clickListener?.onItemClick(adapterPosition, v)
        }

        override fun onLongClick(v: View?): Boolean {
            clickListener?.onItemLongClick(adapterPosition, v)
            return false
        }
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View?)
        fun onItemLongClick(position: Int, v: View?)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val shoppingListView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shopping_list_item, viewGroup, false)

        return ShoppingListViewHolder(shoppingListView)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingListModel = shoppingListModels[position]

        holder.shoppingListName.text = shoppingListModel.getName()
        holder.shoppingListIcon.setImageResource(shoppingListModel.getIcon())
        holder.shoppingListDeleteIcon.setImageResource(shoppingListModel.getDeleteIcon())
    }

    override fun getItemCount() = shoppingListModels.size

}