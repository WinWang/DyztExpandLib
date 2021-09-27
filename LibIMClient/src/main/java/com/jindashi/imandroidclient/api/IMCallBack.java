package com.jindashi.imandroidclient.api;

import com.jindashi.imandroidclient.model.BaseMsg;

/**
 * @ClassName: IMCallBack
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/17 15:23
 * @Version: 1.0
 */
public interface IMCallBack {
    void close();

    void acceptMessage(BaseMsg baseMsg);

    void error(int errorCode, String errorMessage);
}
