package com.jindashi.imandroidclient.model;

import java.io.Serializable;

/**
 * @ClassName: AuthBean
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/16 10:34
 * @Version: 1.0
 */
public class AuthBean  extends BaseEnty {
    private int msg_id;//1,
    private String cmd;// "auth2",
    private String authCode;// "bcd",  //签名值
    private String userId;// "111" ,   //用户唯一标识ID
    private String room_id;//"12"

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }
}
