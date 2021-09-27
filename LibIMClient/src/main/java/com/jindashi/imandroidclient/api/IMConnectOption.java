package com.jindashi.imandroidclient.api;

import com.jindashi.imandroidclient.model.UserBean;

/**
 * @ClassName: IMConnectOption
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/16 15:25
 * @Version: 1.0
 */
public class IMConnectOption {
    private int HEART_BEAT_INTERVAL = 5 * 1000;//心跳包间隔
    private int CONNECT_TIME_OUT = 5 * 1000;//连接超时时间
    private int SOCKET_READ_TIME_OUT = 100 * 1000;//socket的inputStream相关的read操作阻塞的等待时间（没有数据可读，线程一直在这等待）超时断开连接
    private String SCOKET_AUTHKEY;//登录认证秘钥
    private String uid;//  "123456789",
    private String nickname;//  "xxxxxb",
    private String icon;// "aaa"
    private Integer userType=1;//1观众、2助理、3主持人、4老师
    private String roomId;//直播房间号

    public int getHEART_BEAT_INTERVAL() {
        return HEART_BEAT_INTERVAL;
    }

    public void setHEART_BEAT_INTERVAL(int heartBeatInterval) {
        this.HEART_BEAT_INTERVAL = heartBeatInterval * 1000;
    }

    public int getCONNECT_TIME_OUT() {
        return CONNECT_TIME_OUT;
    }

    public void setCONNECT_TIME_OUT(int connectTimeOut) {
        this.CONNECT_TIME_OUT = connectTimeOut * 1000;
    }

    public int getSOCKET_READ_TIME_OUT() {
        return SOCKET_READ_TIME_OUT;
    }

    public void setSOCKET_READ_TIME_OUT(int socketReadTimeOut) {
        this.SOCKET_READ_TIME_OUT = socketReadTimeOut * 1000;
    }

    public String getSCOKET_AUTHKEY() {
        return SCOKET_AUTHKEY;
    }

    public void setSCOKET_AUTHKEY(String SCOKET_AUTHKEY) {
        this.SCOKET_AUTHKEY = SCOKET_AUTHKEY;
    }

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
