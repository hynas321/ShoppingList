package com.example.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var shoppingListsAdapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addListButton: Button

    private val shoppingListModels = getShoppingLists()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context: MainActivity = this

        linearLayoutManager = LinearLayoutManager(this)
        shoppingListsAdapter = ShoppingListAdapter(shoppingListModels)
        recyclerView = findViewById(R.id.main_recyclerView_shopping_list)
        addListButton = findViewById(R.id.main_button_add_list)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = shoppingListsAdapter

        addListButton.setOnClickListener {
            shoppingListModels.add(ShoppingListModel(
                R.drawable.ic_launcher_background, "New Product", R.drawable.ellipsis)
            )

            shoppingListsAdapter.notifyItemInserted(shoppingListModels.size - 1)
        }

        val onItemClickListener = object : ItemEventListener {

            override fun onClick(position: Int) {
                val intent = Intent(context, ProductListActivity::class.java)

                intent.putExtra("id", "1")
                startActivity(intent)
            }
        }

        shoppingListsAdapter.setOnItemClickListener(onItemClickListener)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when (item.groupId) {

            R.id.shopping_list_menu_delete -> {
                val removedItem = shoppingListModels[item.itemId]

                shoppingListModels.removeAt(item.itemId)
                shoppingListsAdapter.notifyItemRemoved(item.itemId)

                Snackbar.make(recyclerView, "Deleted " + removedItem.name, Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
                            shoppingListModels.add(item.itemId, removedItem)

                            shoppingListsAdapter.notifyItemInserted(item.itemId)
                        }).show()

                return true
            }

            R.id.shopping_list_menu_copy -> {
                val copiedItem = shoppingListModels[item.itemId]

                shoppingListModels.add(item.itemId, copiedItem)
                shoppingListsAdapter.notifyItemInserted(item.itemId)

                Snackbar.make(recyclerView, "Copied " + copiedItem.name, Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
                            shoppingListModels.add(item.itemId + 1, copiedItem)

                            shoppingListsAdapter.notifyItemInserted(item.itemId)
                        }).show()

                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    private fun getShoppingLists(): ArrayList<ShoppingListModel> {
        val list = ArrayList<ShoppingListModel>()

        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "Product 1", R.drawable.ellipsis)
        )
        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "Product 2", R.drawable.ellipsis)
        )
        list.add(ShoppingListModel(
            R.drawable.ic_launcher_background, "Product 3", R.drawable.ellipsis)
        )
        return list
    }
}