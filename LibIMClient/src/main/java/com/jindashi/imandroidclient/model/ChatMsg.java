package com.jindashi.imandroidclient.model;

import java.io.Serializable;

/**
 * @ClassName: ChatMsg
 * @Description: 标准的IM wd_chat 消息格式（与大师相随格式不一致）
 * @Author: xxy
 * @CreateDate: 2019/7/16 10:42
 * @Version: 1.0
 */
public class ChatMsg  extends BaseEnty {
    private String message;// "123",
    private UserBean user;//


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
