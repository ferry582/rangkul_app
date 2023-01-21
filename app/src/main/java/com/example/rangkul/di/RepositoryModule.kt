package com.example.rangkul.di

import com.example.rangkul.data.repository.PostRepository
import com.example.rangkul.data.repository.PostRepositoryImp
import com.google.firebase.firestore.FirebaseFirestore
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
    fun providePostRepository(database: FirebaseFirestore): PostRepository {
        return PostRepositoryImp(database)
    }
}