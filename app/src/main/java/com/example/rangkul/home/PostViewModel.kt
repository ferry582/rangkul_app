package com.example.rangkul.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

class PostViewModel: ViewModel(), PostRepository.OnFirestoreTaskComplete {

    private val postRepository = PostRepository(this)
    private val postMutableLiveData: MutableLiveData<List<PostData>> = MutableLiveData()

    fun getPostMutableLiveData(): LiveData<List<PostData>> {
        return postMutableLiveData
    }

    init {
        postRepository.getPostData()
    }

    override fun postDataAdded(postDataList: List<PostData>) {
        postMutableLiveData.postValue(postDataList)
    }

    override fun onError(e: Exception?) {
        TODO("Not yet implemented")
    }


}