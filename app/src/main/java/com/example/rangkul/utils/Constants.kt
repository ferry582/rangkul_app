package com.example.rangkul.utils

object FirestoreCollection {
    const val POST = "posts"
    const val COMMENT = "comments"
    const val LIKE = "likes"
    const val USER = "users"
    const val CATEGORY_CONTENT = "contents"
}

object FirestoreDocumentField {
    const val COMMENTS_COUNT = "commentsCount"
    const val LIKES_COUNT = "likesCount"
    const val POST_TYPE = "type"
    const val POST_CATEGORY = "category"
    const val CONTENT_TYPE = "type"
    const val CONTENT_CATEGORY = "category"
}

object SharedPrefConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "user_session"
}