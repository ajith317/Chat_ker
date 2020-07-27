package com.example.chat_ker.managers.interfaces;

import com.example.chat_ker.model.chat.Chats;

import java.util.List;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chats> list);
    void onReadFailed();
}
