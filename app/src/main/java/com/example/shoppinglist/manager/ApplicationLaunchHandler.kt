package com.example.shoppinglist.manager

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class ApplicationLaunchHandler : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}