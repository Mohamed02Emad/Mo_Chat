package com.example.paymentapp.data.source.homeDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mo_chatting.chatapp.data.source.Converters


@TypeConverters(Converters::class)
@Database(entities = [com.mo_chatting.chatapp.data.models.Room::class], version = 1)
abstract class HomeDataBase : RoomDatabase() {

    abstract fun myDao(): HomeDao

    companion object {
        private var instancee: HomeDataBase? = null

        private const val DB_NAME = "mainRoom"

        fun getInstance(context: Context): HomeDataBase {

            return instancee ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context,
                    HomeDataBase::class.java,
                    DB_NAME
                ).build()
                instancee = instance
                instance
            }
        }
    }
}