package com.jindashi.imandroidclient.model;

import java.io.Serializable;

/**
 * @ClassName: UserBean
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/16 10:41
 * @Version: 1.0
 */
public class UserBean extends BaseEnty {
    private String uid;//  "123456789",
    private String nickname;//  "xxxxxb",
    private String icon;// "aaa"
    private Integer userType;//1观众、2助理、3主持人、4老师

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
}
