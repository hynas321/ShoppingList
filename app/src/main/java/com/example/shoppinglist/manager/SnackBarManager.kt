package com.example.shoppinglist.manager

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar

class SnackBarManager(private var view: View) {

    fun displayMessage(message: String) {
        Snackbar
            .make(view, message, Snackbar.LENGTH_LONG)
            .setAnimationMode(ANIMATION_MODE_SLIDE)
            .show()
    }
}