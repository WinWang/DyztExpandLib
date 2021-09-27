package com.jindashi.imandroidclient.model;

/**
 * @ClassName: AutoLinkMode
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/30 16:42
 * @Version: 1.0
 */
public enum AutoLinkMode {
    MODE_APP(1),
    MODE_ACTIVITY(2);
    private int mode;

    AutoLinkMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
