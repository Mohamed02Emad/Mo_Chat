package com.mo_chatting.chatapp.data.source.messagesRoom

import androidx.room.*
import com.mo_chatting.chatapp.data.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM Message ORDER BY timeWithMillis DESC LIMIT :limit OFFSET :offset")
    fun getMessages(limit: Int, offset: Int): Flow<List<Message>>

    @Query("SELECT * FROM Message where messageRoom == :roomId ORDER BY timeWithMillis")
    fun getMessagesByRoomID(roomId: String): List<Message>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert( messages: Message)

    @Query("delete from Message where messageRoom == :roomId")
    fun deleteAll(roomId: String)

    @Delete
    fun delete(dataModel: Message)

    @Update
    fun update(message: Message)

}