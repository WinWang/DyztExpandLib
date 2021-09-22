package com.jince.emchat.domain;

import java.util.List;

public class ChatServiceBeanWrapper {

    private List<ChatServiceBean> aggregate;
    private List<ChatServiceBean> zsyk;

    public List<ChatServiceBean> getZsyk() {
        return zsyk;
    }

    public void setZsyk(List<ChatServiceBean> zsyk) {
        this.zsyk = zsyk;
    }

    public List<ChatServiceBean> getAggregate() {
        return aggregate;
    }

    public void setAggregate(List<ChatServiceBean> aggregate) {
        this.aggregate = aggregate;
    }

    public static class ChatServiceBean implements ChatService {


        private String service_uid;
        private String nick_name;
        private String head_portrait;
        private String channel_type;
        private String service_type;
        private int type;
        private String show_name;

        private int unread_num;
        private ChatMessage latest_unread_msg;

        public String getService_uid() {
            return service_uid;
        }

        public void setService_uid(String service_uid) {
            this.service_uid = service_uid;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getHead_portrait() {
            return head_portrait;
        }

        public void setHead_portrait(String head_portrait) {
            this.head_portrait = head_portrait;
        }

        public String getChannel_type() {
            return channel_type;
        }

        public void setChannel_type(String channel_type) {
            this.channel_type = channel_type;
        }

        public String getService_type() {
            return service_type;
        }

        public void setService_type(String service_type) {
            this.service_type = service_type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getShow_name() {
            return show_name;
        }

        public void setShow_name(String show_name) {
            this.show_name = show_name;
        }

        public int getUnread_num() {
            return unread_num;
        }

        public void setUnread_num(int unread_num) {
            this.unread_num = unread_num;
        }

        public ChatMessage getLatest_unread_msg() {
            return latest_unread_msg;
        }

        public void setLatest_unread_msg(ChatMessage latest_unread_msg) {
            this.latest_unread_msg = latest_unread_msg;
        }

        @Override
        public String getChatServiceName() {
            return nick_name;
        }

        @Override
        public String getChatServiceId() {
            return service_uid;
        }

        @Override
        public String getChatServiceAvatarUrl() {
            return head_portrait;
        }

        @Override
        public int getChatServiceType() {
            return type;
        }
    }


}
