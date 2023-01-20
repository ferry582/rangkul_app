package com.example.rangkul.home

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception

class PostRepository (private var onFirestoreTaskComplete: OnFirestoreTaskComplete) {

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val postRef = firebaseFirestore.collection("posts")

    fun getPostData() {
        postRef.get().addOnCompleteListener(){
            OnCompleteListener<QuerySnapshot> {
                if (it.isSuccessful) {
                    onFirestoreTaskComplete.postDataAdded(it.result.toObjects(PostData::class.java))
                } else {
                    onFirestoreTaskComplete.onError(it.exception)
                }
            }
        }
    }

    interface OnFirestoreTaskComplete {
        fun postDataAdded(postDataList: List<PostData>)
        fun onError(e: Exception?)
    }


}