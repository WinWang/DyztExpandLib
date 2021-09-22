package com.jince.emchat;

import androidx.annotation.Nullable;

import com.jince.emchat.domain.ChatMessage;

public interface EMUnreadMessageListener {

    @Deprecated
    void onUnreadMessageCount(int count);

    void onUnreadChatMessage(@Nullable ChatMessage message);

}
