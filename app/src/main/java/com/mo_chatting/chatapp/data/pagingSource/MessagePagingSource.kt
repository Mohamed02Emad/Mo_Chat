package com.mo_chatting.chatapp.data.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.source.messagesRoom.MessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessagePagingSource(private val dao: MessageDao, private val roomId: String) :
    PagingSource<Int, Message>() {
    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.firstItemOrNull()?.timeWithMillis!!.toInt()
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> = withContext(
        Dispatchers.IO
    ) {
        val page = params.key ?: 0
        Log.i("mohamed", "page = "+page)
        val pageSize = params.loadSize
        Log.i("mohamed", "pageSize = "+pageSize)
        val offset = page * pageSize
        Log.i("mohamed", "offset = "+offset)

        //val messages = dao.getMessagesByRoomID(roomId)
        val messages = dao.getMessages(pageSize, offset, roomId)
        //val messages = dao.getMessagesByRoomID()

        Log.i("mohamed", "load: size = "+messages.size)
        val prevKey = if (page == 0) null else page - 1
        Log.i("mohamed", "prevKey = "+prevKey)
        val nextKey = if (messages.size < pageSize) null else page + 1
        Log.i("mohamed", "nextKey = "+nextKey+"\n\n")

        LoadResult.Page(messages, prevKey, nextKey)

    }


}
