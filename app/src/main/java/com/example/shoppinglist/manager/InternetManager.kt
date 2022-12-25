package com.example.shoppinglist.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.widget.Toast
import com.example.shoppinglist.R

@Suppress("DEPRECATION")
class InternetManager(private val context: Context) {
    private val noInternetToastMessage = context.getString(R.string.internet_manager_no_internet_access)

    fun checkInternetConnection(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        if (activeNetwork?.type == TYPE_WIFI || activeNetwork?.type == ConnectivityManager.TYPE_MOBILE) {
            return true
        }


        Toast.makeText(context, noInternetToastMessage, Toast.LENGTH_SHORT).show()
        return false
    }
}