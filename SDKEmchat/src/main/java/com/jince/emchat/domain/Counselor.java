package com.jince.emchat.domain;

public class Counselor {

    private String uid;
    private String nickname;
    private String head_portrait;
    private String ukey;
    private int is_youke;
    private String hx_username;
    private String hx_pass;

    // 内置字段，不能删除。
    // 客服
    private CustomService custom_service;


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

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public int getIs_youke() {
        return is_youke;
    }

    public void setIs_youke(int is_youke) {
        this.is_youke = is_youke;
    }

    public String getHx_username() {
        return hx_username;
    }

    public void setHx_username(String hx_username) {
        this.hx_username = hx_username;
    }

    public String getHx_pass() {
        return hx_pass;
    }

    public void setHx_pass(String hx_pass) {
        this.hx_pass = hx_pass;
    }

    public CustomService getCustom_service() {
        return custom_service;
    }

    @Override
    public String toString() {
        return "Counselor{" +
                "uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", head_portrait='" + head_portrait + '\'' +
                ", ukey='" + ukey + '\'' +
                ", is_youke=" + is_youke +
                ", hx_username='" + hx_username + '\'' +
                ", hx_pass='" + hx_pass + '\'' +
                ", custom_service=" + custom_service +
                '}';
    }
}
