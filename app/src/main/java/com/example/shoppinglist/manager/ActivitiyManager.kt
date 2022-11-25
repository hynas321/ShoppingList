package com.example.shoppinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.example.shoppinglist.model.Model

class ActivityManager(private val context: Context) {

    fun <T> startActivityWithResources(model: Model, activity: Class<T>) {
        val intent = Intent(context, activity)
        val extras = Bundle()

        extras.putSerializable("model", model)
        startActivity(context, intent, extras)
    }
}