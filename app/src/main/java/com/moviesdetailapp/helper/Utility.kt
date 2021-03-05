package com.moviesdetailapp.helper

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.moviesdetailapp.R
import com.moviesdetailapp.interfaces.OnFilterItemSelectListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utility {
    companion object {
        fun showLog(msg: String = "No Msg Found"){
            Log.d(Constants.LOG_TAG, msg)
        }

        fun hideKeyboard(activity: Activity) {
            if(activity != null) {
                val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view: View? = activity.currentFocus
                if (view == null) {
                    view = View(activity)
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }

        fun getFormattedDate(date: String?): String {
            val accepted = SimpleDateFormat("yyyy-MM-dd")
            val returnFormat = SimpleDateFormat("MMM dd, yyyy")
            var dateObj: Date
            try {
                dateObj = accepted.parse(date)
                return returnFormat.format(dateObj)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return ""
        }

        fun createFilterDialog(context: Context, listItems: List<String>, onFilterItemSelectListener: OnFilterItemSelectListener) {
            val builderSingle: AlertDialog.Builder = AlertDialog.Builder(context)
            builderSingle.setIcon(R.mipmap.ic_launcher)
            builderSingle.setTitle("Select One Filter:-")

            val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item)
            arrayAdapter.addAll(listItems)

            builderSingle.setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
            builderSingle.setAdapter(arrayAdapter) { dialog, which ->
                onFilterItemSelectListener.onFilterItemSelected(which, arrayAdapter.getItem(which)!!)
                dialog.dismiss()
            }

            builderSingle.show()
        }
    }
}