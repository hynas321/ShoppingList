package com.example.shoppinglist.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.shoppinglist.model.Model

class ActivityManager(private val context: Context) {

    fun <T> startActivityWithResources(model: Model?, activity: Class<T>) {
        val intent = Intent(context, activity)

        if (model != null) {
            val extras = Bundle()
            extras.putSerializable("model", model)
            intent.putExtras(extras)
        }

        context.startActivity(intent)
    }

    fun <T> startActivityWithResources(string: String, activity: Class<T>) {
        val intent = Intent(context, activity)

        if (string != null) {
            val extras = Bundle()
            extras.putSerializable("string", string)
            intent.putExtras(extras)
        }

        context.startActivity(intent)
    }
}