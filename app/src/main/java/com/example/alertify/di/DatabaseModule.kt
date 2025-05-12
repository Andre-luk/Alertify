package com.example.alertify.di

import com.example.alertify.data.AlertifyRepository
import com.example.alertify.data.AlertifyRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAlertifyRepository(
        firestore: FirebaseFirestore
    ): AlertifyRepository =
        AlertifyRepositoryImpl(firestore)
}
