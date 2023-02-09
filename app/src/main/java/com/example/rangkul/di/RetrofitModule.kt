package com.example.rangkul.di

import com.example.rangkul.data.retrofit.ApiInterface
import com.example.rangkul.utils.RetrofitConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): ApiInterface {

        val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(RetrofitConstants.PROFANITY_CHECK_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: ApiInterface by lazy {
            retrofit.create(ApiInterface::class.java)
        }

        return api
    }

}