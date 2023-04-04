package com.mo_chatting.chatapp.data.source.homeRoom

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mo_chatting.chatapp.data.models.Room
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {
    @Query("SELECT * FROM Room")
    fun getAllToObserve(): Flow<List<Room>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg dataModel: Room)

    @Query("delete from Room")
    fun deleteAll()

    @Delete
    fun delete(dataModel: Room)

    @Query("DELETE FROM Room WHERE roomId = :id")
    suspend fun deleteById(id: String)

    @Update
    fun update(dataModel: Room)

}