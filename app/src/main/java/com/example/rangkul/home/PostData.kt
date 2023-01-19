package com.example.rangkul.home

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class PostData (
        val userName: String,
        val category: String,
        val time: String,
        val writing: String
): Parcelable
