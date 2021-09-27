package com.jindashi.imandroidclient.api;

/**
 * @ClassName: IMConnectListener
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/17 15:20
 * @Version: 1.0
 */
public interface IMConnectListener {
    void linking();

    void connectFailure();

    void connectSuccess();
}
