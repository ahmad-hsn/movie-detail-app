package com.moviesdetailapp

import android.net.ConnectivityManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity: AppCompatActivity() {
    var snackbar: Snackbar? = null

    protected fun showSnackBar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        snackbar = Snackbar.make(view, message, duration)
        snackbar?.show()
    }

    fun dismissSnackbar() {
        snackbar?.dismiss()
    }

    fun isOnline(): Boolean {
        return try {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}