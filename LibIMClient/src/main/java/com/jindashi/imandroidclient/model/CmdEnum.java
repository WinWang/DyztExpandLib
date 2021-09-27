package com.jindashi.imandroidclient.model;

/**
 * @ClassName: ChatOrder
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/16 10:42
 * @Version: 1.0
 */
public  enum CmdEnum {
    CMD_AUTH1("auth1"),
    CMD_AUTH2("auth2"),
    CMD_CHAT("wd_chat"),
    CMD_HEART("wd_heartbeat"),
    CMD_ROOM_PEOPLES("wd_room");
    private String cmd;

    CmdEnum(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public static CmdEnum getBuyCmd(String cmd) {
        for (CmdEnum cmdEnum : values()) {
            if (cmdEnum.getCmd().equals(cmd))
                return cmdEnum;
        }
        return null;
    }
}

