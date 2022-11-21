package com.example.shoppinglist

interface ItemEventListener {
    fun onClick(position: Int)
    fun onCreateMenuClick(position: Int)
}