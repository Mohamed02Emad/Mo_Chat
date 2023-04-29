package com.mo_chatting.chatapp.data.source.messagesRoom

import android.media.Image
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.*
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.MessageType
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM Message where messageRoom == :roomId ORDER BY messageid DESC LIMIT :limit OFFSET :offset")
    fun getMessages(limit: Int, offset: Int , roomId: String): List<Message>
    @Query("SELECT * FROM Message where messageRoom == :roomId ORDER BY messageid")
    fun getMessagesByRoomID(roomId: String): List<Message>
    @Query("SELECT * FROM Message where (messageRoom == :roomId)&(messageOwnerId == :userId)&(timeWithMillis==:timeInMillis) ")
    fun getExactMessage(roomId: String,userId:String,timeInMillis : String): List<Message>
    @Query("SELECT * FROM Message where (messageRoom == :roomId )ORDER BY messageid")
    fun getMessagesPagingData(roomId: String): PagingSource<Int,Message>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert( messages: Message)

    @Query("delete from Message where messageRoom == :roomId")
    fun deleteAll(roomId: String)

    @Delete
    fun delete(dataModel: Message)

    @Update
    fun update(message: Message)


}