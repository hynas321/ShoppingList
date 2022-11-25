package com.example.shoppinglist.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.widget.Toast


@Suppress("DEPRECATION")
class InternetManager(private val context: Context) {

    fun checkInternetConnection(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork?.type == TYPE_WIFI || activeNetwork?.type == ConnectivityManager.TYPE_MOBILE) {
            return true
        }

        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        return false
    }
}