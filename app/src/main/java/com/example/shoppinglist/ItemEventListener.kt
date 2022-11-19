package com.example.shoppinglist

interface ItemEventListener {
    fun onClick(position: Int)
    fun onLongClick(position: Int)
    fun onDeleteClick(position: Int)
}