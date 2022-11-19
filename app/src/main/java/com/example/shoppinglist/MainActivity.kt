package com.example.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var shoppingListsAdapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context: MainActivity = this
        val shoppingListModels = getShoppingLists()

        linearLayoutManager = LinearLayoutManager(this)
        shoppingListsAdapter = ShoppingListAdapter(shoppingListModels)
        recyclerView = findViewById(R.id.main_recyclerView_shopping_list)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListsAdapter

        val onItemClickListener = object : ItemEventListener {

            override fun onClick(position: Int) {
                val intent = Intent(context, ProductListActivity::class.java)

                intent.putExtra("id", "1")
                startActivity(intent)
            }

            override fun onLongClick(position: Int) {

            }

            override fun onDeleteClick(position: Int) {
                val removedItem = shoppingListModels[position]

                shoppingListModels.removeAt(position)
                shoppingListsAdapter.notifyItemRemoved(position)

                Snackbar.make(recyclerView, "Deleted " + removedItem.name, Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
                            shoppingListModels.add(position, removedItem)

                            shoppingListsAdapter.notifyItemInserted(position)
                        }).show()
            }
        }

        shoppingListsAdapter.setOnItemClickListener(onItemClickListener)
    }

    private fun getShoppingLists(): ArrayList<ShoppingListModel> {
        val list = ArrayList<ShoppingListModel>()

        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "a", R.drawable.trash_bin_icon)
        )
        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "b", R.drawable.trash_bin_icon)
        )
        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "c", R.drawable.trash_bin_icon)
        )
        return list
    }
}