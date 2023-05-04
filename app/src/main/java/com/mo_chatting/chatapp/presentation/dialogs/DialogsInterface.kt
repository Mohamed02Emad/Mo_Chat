package com.mo_chatting.chatapp.presentation.dialogs

import com.mo_chatting.chatapp.data.models.Room

interface DialogsInterface {

    // creatteRoomDialog
    fun onDataPassedCreateRoom(room: Room)
    fun onRoomEditPassed(room: Room)

    //EnterPasswordDialog
    fun onPasswordReceive(room: Room)

    //JoinRoomDialog
    fun onDataPassedJoinRoom(roomId: String)

}