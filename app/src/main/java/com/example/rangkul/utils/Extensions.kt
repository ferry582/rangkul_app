package com.example.rangkul.utils

import android.app.Activity
import android.view.View
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

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar {str ->
    if (str.isLowerCase()) str.titlecase(
        Locale.getDefault()
    ) else str.toString()
} }
