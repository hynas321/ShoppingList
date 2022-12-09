package com.example.shoppinglist.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.shoppinglist.model.Model

class ActivityManager(private val context: Context) {

    fun <T> startActivity(activity: Class<T>) {
        val intent = Intent(context, activity)

        context.startActivity(intent)
    }

    fun <T> startActivityWithResources(string: String, activity: Class<T>) {
        val intent = Intent(context, activity)

        val extras = Bundle()
        extras.putSerializable("string", string)
        intent.putExtras(extras)

        context.startActivity(intent)
    }

    fun <T> startActivityWithResources(string1: String, string2: String, activity: Class<T>) {
        val intent = Intent(context, activity)

        val extras = Bundle()
        extras.putSerializable("string1", string1)
        extras.putSerializable("string2", string2)
        intent.putExtras(extras)

        context.startActivity(intent)
    }
}