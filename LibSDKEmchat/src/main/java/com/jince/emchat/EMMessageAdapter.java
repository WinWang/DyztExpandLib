package com.jince.emchat;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMGroupReadAck;
import com.hyphenate.chat.EMMessage;

import java.util.List;

class EMMessageAdapter implements EMMessageListener {
    @Override
    public void onMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onGroupMessageRead(List<EMGroupReadAck> list) {

    }

    @Override
    public void onReadAckForGroupMessageUpdated() {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}
