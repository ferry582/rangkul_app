package com.example.rangkul.di

import android.content.SharedPreferences
import com.example.rangkul.data.repository.*
import com.example.rangkul.data.retrofit.ApiInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
        gson: Gson,
        storageReference: FirebaseStorage,
        retrofitInstance: ApiInterface
    ): PostRepository {
        return PostRepositoryImp(database, appPreferences, gson, storageReference, retrofitInstance)
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

    @Provides
    @Singleton
    fun provideCategoryRepository(
        database: FirebaseFirestore,
    ): CategoryContentRepository {
        return CategoryContentRepositoryImp(database)
    }

    @Provides
    @Singleton
    fun provideOptionsRepository(
        database: FirebaseFirestore,
        appPreferences: SharedPreferences,
        gson: Gson,
        storageReference: FirebaseStorage
    ): OptionsRepository {
        return OptionsRepositoryImp(database, appPreferences, gson, storageReference)
    }

    @Provides
    @Singleton
    fun provideEditProfileRepository(
        database: FirebaseFirestore,
        appPreferences: SharedPreferences,
        gson: Gson,
        storageReference: FirebaseStorage
    ): EditProfileRepository {
        return EditProfileRepositoryImp(database, appPreferences, gson, storageReference)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        database: FirebaseFirestore,
        appPreferences: SharedPreferences,
        gson: Gson
    ): ProfileRepository {
        return ProfileRepositoryImp(database, appPreferences, gson)
    }
}