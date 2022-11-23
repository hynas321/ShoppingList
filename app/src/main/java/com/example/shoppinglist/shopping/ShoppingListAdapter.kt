package com.example.shoppinglist.shopping

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.activities.ProductListActivity


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

    fun openProductListActivity() {
        val intent = Intent(context, ProductListActivity::class.java)

        intent.putExtra("id", "1")
        startActivity(context, intent, null)
    }

    fun showPopup(itemView: View, position: Int) {
        val popup = PopupMenu(context, itemView)
        val inflater: MenuInflater = popup.menuInflater

        inflater.inflate(R.menu.shopping_list_menu, popup.menu)
        popup.show()

        popup.setOnMenuItemClickListener { item ->
            onMenuItemClick(item, position)
        }
    }

    private fun onMenuItemClick (item: MenuItem, position: Int): Boolean {
        when (item.itemId) {

            R.id.shopping_list_menu_delete -> {
                shoppingListModels.removeAt(position)
                notifyItemRemoved(position)

                return true
            }

            R.id.shopping_list_menu_copy -> {
                val copiedItem = shoppingListModels[position]

                shoppingListModels.add(position + 1, copiedItem)
                notifyItemInserted(position + 1)

                return true
            }

            else -> return false
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val shoppingListView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shopping_list_item, viewGroup, false)

        return ShoppingListViewHolder(shoppingListView)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingListModel = shoppingListModels[position]

        holder.shoppingListName.text = shoppingListModel.name
        holder.shoppingListIcon.setImageResource(shoppingListModel.iconImageViewId)
        holder.shoppingListExtensionIcon.setImageResource(R.drawable.ic_vertical_dots)

    }

    override fun getItemCount() = shoppingListModels.size

}