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
        return state.firstItemOrNull()?.timeWithMillis!!.toInt()
    }

    override suspend fun load(params: LoadParams<Int>): PagingSource.LoadResult<Int, Message> = withContext(
        Dispatchers.IO
    ) {
        val page = params.key ?: 0
        Log.d("mohamed", "page $page")
        val pageSize = 50 //params.loadSize
        val offset = page * pageSize
        val messages = dao.getMessages(pageSize, offset, roomId)
        val prevKey = if (page == 0) null else page - 1
        val nextKey = if (messages.size < pageSize) null else page + 1
        LoadResult.Page(messages, prevKey, nextKey)
    }


}
