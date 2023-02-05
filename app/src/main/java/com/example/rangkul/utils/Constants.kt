package com.example.rangkul.utils

object FirestoreCollection {
    const val POST = "posts"
    const val DIARY = "diaries"
    const val COMMENT = "comments"
    const val LIKE = "likes"
    const val USER = "users"
    const val CATEGORY_CONTENT = "contents"
}

object FirestoreDocumentField {
    const val COMMENTS_COUNT = "commentsCount"
    const val COMMENT_OWNER = "commentedBy"
    const val COMMENT_PROFILE_PICTURE = "profilePicture"
    const val COMMENT_USER_NAME = "userName"
    const val LIKES_COUNT = "likesCount"
    const val CONTENT_TYPE = "type"
    const val CONTENT_CATEGORY = "category"
    const val CONTENT_PUBLISHED_DATE = "publishedAt"
    const val POST_CREATED_BY = "createdBy"
    const val POST_PROFILE_PICTURE = "profilePicture"
    const val POST_USER_NAME = "userName"
    const val POST_TYPE = "type"
    const val POST_CATEGORY = "category"
    const val POST_ID = "postId"
}

object SharedPrefConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "user_session"
}

object FirebaseStorageConstants {
    const val ROOT_DIRECTORY = "app"
    const val POST_IMAGE = "posts"
    const val USER_IMAGE = "users"
}