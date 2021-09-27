package com.jindashi.imandroidclient.model;

import java.io.Serializable;

/**
 * @ClassName: BaseMsg
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/16 10:39
 * @Version: 1.0
 */
public class BaseMsg<T>  extends BaseEnty {
    private String msg_id;
    private String cmd;
    private T result;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
