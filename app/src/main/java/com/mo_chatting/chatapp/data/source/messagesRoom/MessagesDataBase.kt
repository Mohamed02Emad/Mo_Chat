package com.mo_chatting.chatapp.data.source.messagesRoom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mo_chatting.chatapp.data.source.Converters


@TypeConverters(Converters::class)
@Database(entities = [com.mo_chatting.chatapp.data.models.Message::class], version = 1)
abstract class MessagesDataBase : RoomDatabase() {

    abstract fun myDao(): MessageDao

    companion object {
        private var instancee: MessagesDataBase? = null

        private const val DB_NAME = "messages_db"

        fun getInstance(context: Context): MessagesDataBase {

            return instancee ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context,
                    MessagesDataBase::class.java,
                    DB_NAME
                ).build()
                instancee = instance
                instance
            }
        }
    }
}