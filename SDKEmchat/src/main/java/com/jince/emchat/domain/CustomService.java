package com.jince.emchat.domain;

@Deprecated
public class CustomService implements ChatService{
    private String uid;
    private String nickname;
    private String head_portrait;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_portrait() {
        return head_portrait;
    }

    public void setHead_portrait(String head_portrait) {
        this.head_portrait = head_portrait;
    }

    @Override
    public String toString() {
        return "CustomService{" +
                "uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", head_portrait='" + head_portrait + '\'' +
                '}';
    }

    @Override
    public String getChatServiceName() {
        return nickname;
    }

    @Override
    public String getChatServiceId() {
        return uid;
    }

    @Override
    public String getChatServiceAvatarUrl() {
        return head_portrait;
    }

    @Override
    public int getChatServiceType() {
        return 0;
    }
}
