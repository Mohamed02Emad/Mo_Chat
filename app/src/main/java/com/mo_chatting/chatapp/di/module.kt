package com.mo_chatting.chatapp.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.data.fireBaseDataSource.FireBaseRoomsDataSource
import com.mo_chatting.chatapp.data.repositories.DirectChatRepository
import com.mo_chatting.chatapp.data.repositories.MessagesRepository
import com.mo_chatting.chatapp.data.repositories.RoomsRepository
import com.mo_chatting.chatapp.data.repositories.SearchUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object module {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideRoomsRepository(
        firebaseFireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        @ApplicationContext appContext: Context,
        fireBaseRoomsDataSource: FireBaseRoomsDataSource
    ): RoomsRepository = RoomsRepository(
        firebaseFireStore, firebaseAuth,
        appContext,
        fireBaseRoomsDataSource
    )

    @Singleton
    @Provides
    fun provideDirectChatRepository(
        firebaseFireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        @ApplicationContext appContext: Context,
        fireBaseRoomsDataSource: FireBaseRoomsDataSource
    ): DirectChatRepository = DirectChatRepository(
        firebaseFireStore, firebaseAuth,
        appContext, fireBaseRoomsDataSource
    )

    @Singleton
    @Provides
    fun provideSearchUserRepository(
        firebaseFireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        @ApplicationContext appContext: Context
    ): SearchUserRepository = SearchUserRepository(
        firebaseFireStore, firebaseAuth,
        appContext
    )

    @Singleton
    @Provides
    fun provideChatRepository(
        firebaseFireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        @ApplicationContext appContext: Context
    ): MessagesRepository =
        MessagesRepository(firebaseFireStore, firebaseAuth, appContext)

    @Singleton
    @Provides
    fun provideFireBaseDataSource(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        @ApplicationContext appContext: Context
    ): FireBaseRoomsDataSource =
        FireBaseRoomsDataSource(firebaseAuth, firebaseFirestore, firebaseStorage, appContext)

    @Singleton
    @Provides

    fun provideFirebaseFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance().also {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        it.firestoreSettings = settings
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext appContext: Context
    ): DataStoreImpl {
        return DataStoreImpl(appContext)
    }

}