package com.example.rangkul.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.rangkul.data.model.*
import com.example.rangkul.utils.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.*


class ProfileRepositoryImp(
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
): ProfileRepository {

    val TAG = "ProfileRepositoryImp"

    override fun getUserData(uid: String, result: (UiState<UserData?>) -> Unit) {
        // Get user data
        database.collection(FirestoreCollection.USER)
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(UserData::class.java)
                result.invoke(
                    UiState.Success(user)
                )

            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun getSessionData(result: (UserData?) -> Unit) {
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (userStr == null) {
            result.invoke(null)
        } else {
            val user = gson.fromJson(userStr, UserData::class.java)
            result.invoke(user)
        }
    }

    override fun addFollowData(currentUId: String, followedUId: String, result: (UiState<String>) -> Unit) {
        addToFollowing(currentUId, followedUId).apply {
            addToFollowers(currentUId, followedUId).apply {
                result.invoke(
                    UiState.Success("Task is completed")
                )
            }
        }
    }

    private fun addToFollowing(currentUId: String, followedUId: String) {
        val followData = FollowData()
        followData.userId = followedUId
        followData.followedAt = Date()

       database.collection(FirestoreCollection.USER).document(currentUId)
            .collection(FirestoreCollection.FOLLOWING).document(followedUId).set(followData)
            .addOnSuccessListener {
                Log.w(TAG, "FollowData has been added at following collection")
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    private fun addToFollowers(currentUId: String, followedUId: String) {
        val followData = FollowData()
        followData.userId = currentUId
        followData.followedAt = Date()

        database.collection(FirestoreCollection.USER).document(followedUId)
            .collection(FirestoreCollection.FOLLOWERS).document(currentUId).set(followData)
            .addOnSuccessListener {
                Log.w(TAG, "FollowData has been added at followers collection")
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    override fun removeFollowData(currentUId: String, followedUId: String, result: (UiState<String>) -> Unit) {
        removeFromFollowing(currentUId, followedUId).apply {
            removeFromFollowers(currentUId, followedUId).apply {
                result.invoke(
                    UiState.Success("Task is completed")
                )
            }
        }
    }

    private fun removeFromFollowing(currentUId: String, followedUId: String) {
        database.collection(FirestoreCollection.USER).document(currentUId)
            .collection(FirestoreCollection.FOLLOWING).document(followedUId)
            .delete()
            .addOnSuccessListener {
                Log.w(TAG, "FollowData has been removed at following collection")
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    private fun removeFromFollowers(currentUId: String, followedUId: String) {
        database.collection(FirestoreCollection.USER).document(followedUId)
            .collection(FirestoreCollection.FOLLOWERS).document(currentUId)
            .delete()
            .addOnSuccessListener {
                Log.w(TAG, "FollowData has been removed at followers collection")
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    override fun isUserBeingFollowed(currentUId: String, targetUId: String, result: (Boolean) -> Unit) {
        database.collection(FirestoreCollection.USER).document(currentUId)
            .collection(FirestoreCollection.FOLLOWING).document(targetUId).get()
            .addOnSuccessListener {
                result.invoke(it.exists())
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    override fun getProfileCountData(uid: String, postType: String, result: (UiState<ProfileCountData>) -> Unit) {
        val profileCountData = ProfileCountData()

        getPostCount(uid, postType) { post ->
            profileCountData.post = post
            getFollowingCount(uid) { following ->
                profileCountData.following = following
                getFollowersCount(uid) { followers ->
                    profileCountData.followers = followers
                    result.invoke(
                        UiState.Success(profileCountData)
                    )
                }
            }
        }
    }

    private fun getPostCount(uid: String, postType: String, result: (Long) -> Unit) {
        val document =
            if (postType == "Diary") {
                database.collection(FirestoreCollection.USER).document(uid)
                    .collection(FirestoreCollection.DIARY)
            } else {
                database.collection(FirestoreCollection.POST)
                    .whereEqualTo(FirestoreDocumentField.POST_CREATED_BY, uid)
                    .whereEqualTo(FirestoreDocumentField.POST_TYPE, postType)
            }

        document
            .count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener {
                result.invoke(it.count)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    private fun getFollowingCount(uid: String, result: (Long) -> Unit) {
        database.collection(FirestoreCollection.USER).document(uid)
            .collection(FirestoreCollection.FOLLOWING)
            .count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener {
                result.invoke(it.count)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    private fun getFollowersCount(uid: String, result: (Long) -> Unit) {
        database.collection(FirestoreCollection.USER).document(uid)
            .collection(FirestoreCollection.FOLLOWERS)
            .count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener {
                result.invoke(it.count)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    override fun getUserDataList(uid: String, collection: String, result: (UiState<List<UserData>>) -> Unit) {
        if (collection == "Following") {
            getFollowingList(uid) {
                getUserList(it) { data ->
                    result.invoke(
                        UiState.Success (data)
                    )
                }
            }
        } else {
            getFollowersList(uid) {
                getUserList(it) { data ->
                    result.invoke(
                        UiState.Success (data)
                    )
                }
            }
        }
    }

    private fun getUserList (userList: List<FollowData>, result: (List<UserData>) -> Unit) {
        val tasks: MutableList<Task<DocumentSnapshot>> = ArrayList()

        for (doc in userList) {
            tasks.add(database.collection(FirestoreCollection.USER).document(doc.userId).get())
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
            .addOnSuccessListener {
                val list = arrayListOf<UserData>()
                for (document in it) {
                    val data = document.toObject(UserData::class.java)
                    data?.let { it1 -> list.add(it1) }
                }
                result.invoke(list)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    private fun getFollowingList (uid: String, result: (List<FollowData>) -> Unit) {
        database.collection(FirestoreCollection.USER).document(uid)
            .collection(FirestoreCollection.FOLLOWING)
            .get()
            .addOnSuccessListener {
                val following = arrayListOf<FollowData>()
                for (document in it) {
                    val data = document.toObject(FollowData::class.java)
                    following.add(data)
                }
                result.invoke(following)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

    private fun getFollowersList (uid: String, result: (List<FollowData>) -> Unit) {
        database.collection(FirestoreCollection.USER).document(uid)
            .collection(FirestoreCollection.FOLLOWERS)
            .get()
            .addOnSuccessListener {
                val followers = arrayListOf<FollowData>()
                for (document in it) {
                    val data = document.toObject(FollowData::class.java)
                    followers.add(data)
                }
                result.invoke(followers)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e(TAG, it1) }
            }
    }

}