package com.mo_chatting.chatapp.data.fireBaseDataSource

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.appClasses.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FireBaseRoomsDataSource(
    val firebaseAuth: FirebaseAuth,
    val firebaseFirestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage,
    val appContext: Context
) {

    fun setUpRoomsListener(): Flow<QuerySnapshot> = callbackFlow {
        val listenerRegistration = firebaseFirestore
            .collection(Constants.roomsCollection)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                } else if (snapshot != null) {
                    try {
                        trySend(snapshot).isSuccess
                    } catch (_: Exception) {
                    }
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

}