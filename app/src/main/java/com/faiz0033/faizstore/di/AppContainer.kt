package com.faiz0033.faizstore.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.room.Room
import com.faiz0033.faizstore.BuildConfig
import com.faiz0033.faizstore.data.local.database.LaptopDatabase
import com.faiz0033.faizstore.data.local.preferences.AuthPreferences
import com.faiz0033.faizstore.data.remote.api.ImgBBApiService
import com.faiz0033.faizstore.data.remote.api.LaptopApiService
import com.faiz0033.faizstore.data.repository.AuthRepositoryImpl
import com.faiz0033.faizstore.data.repository.ImageUploadRepositoryImpl
import com.faiz0033.faizstore.data.repository.LaptopRepositoryImpl
import com.faiz0033.faizstore.domain.repository.AuthRepository
import com.faiz0033.faizstore.domain.repository.ImageUploadRepository
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer {
    val authRepository: AuthRepository
    val laptopDatabase: LaptopDatabase
    val laptopApiService: LaptopApiService
    val imgBBApiService: ImgBBApiService
    val imageUploadRepository: ImageUploadRepository
    val laptopRepository: LaptopRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    
    private val authPreferences: AuthPreferences by lazy {
        AuthPreferences(context)
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(credentialManager, authPreferences)
    }
    
    override val laptopDatabase: LaptopDatabase by lazy {
        Room.databaseBuilder(
            context,
            LaptopDatabase::class.java,
            "laptop_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.MOCK_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    override val laptopApiService: LaptopApiService by lazy {
        retrofit.create(LaptopApiService::class.java)
    }

    private val imgBBRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    override val imgBBApiService: ImgBBApiService by lazy {
        imgBBRetrofit.create(ImgBBApiService::class.java)
    }

    override val imageUploadRepository: ImageUploadRepository by lazy {
        ImageUploadRepositoryImpl(imgBBApiService)
    }

    override val laptopRepository: LaptopRepository by lazy {
        LaptopRepositoryImpl(laptopDatabase.laptopDao(), laptopApiService, imageUploadRepository)
    }
}
