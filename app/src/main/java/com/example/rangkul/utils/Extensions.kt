package com.example.rangkul.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.*

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun Fragment.toast(msg: String?){
    Toast.makeText(requireContext(),msg,Toast.LENGTH_LONG).show()
}

fun Activity.toast(msg: String?){
    Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar {str ->
    if (str.isLowerCase()) str.titlecase(
        Locale.getDefault()
    ) else str.toString()
} }

fun String.getFirstWord(): String =
    if(this.contains(" ")) {
        val firstSpace: Int = this.indexOf(" ") // detect the first space character
        this.substring(0,firstSpace)
    } else {
        this
    }

fun String.limitTextLength(): String =
    if (this.length > 20) {
        "${this.substring(0,20)}..."
    } else {
        this
    }



