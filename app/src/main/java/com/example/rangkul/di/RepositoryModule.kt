package com.example.rangkul.di

import android.content.SharedPreferences
import com.example.rangkul.data.repository.AuthRepository
import com.example.rangkul.data.repository.AuthRepositoryImp
import com.example.rangkul.data.repository.PostRepository
import com.example.rangkul.data.repository.PostRepositoryImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(
        database: FirebaseFirestore,
        appPreferences: SharedPreferences,
        gson: Gson): PostRepository {
        return PostRepositoryImp(database, appPreferences, gson)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(database, auth, appPreferences, gson)
    }
}