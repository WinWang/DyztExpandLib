package com.jindashi.imandroidclient.api;

/**
 * @ClassName: SocketCallBack
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/17 14:44
 * @Version: 1.0
 */
public interface SocketCallBack {
    void linking();

    void connectFailure();

    void connectSuccess();

    void close();

    void acceptMessage(byte[] data);

    void error(IMError imError);

}
