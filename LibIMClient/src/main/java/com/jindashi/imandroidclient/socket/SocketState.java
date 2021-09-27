package com.jindashi.imandroidclient.socket;

public interface SocketState {
    /**
     * 链接成功
     */
    int OK = 0x00000010;
    /**
     * 未链接
     */
    int NO_LINK = 0x00000020;
    /**
     * 链接中
     */
    int LINKING = 0x00000040;
    /**
     * 连接负载均衡
     */
    int LINKING_LB = 0x00008000;
    /**
     * 重连成功
     */
    int RECONN_OK = 0x00000080;
    /**
     * socket已关闭
     */
    int CLOSE = 0x00000100;
    /**
     * socket链接失败
     */
    int FAIL = 0x00000200;
    /**
     * 没有网络
     */
    int NO_NETWORK = 0x00000400;
    /**
     * 等待链接
     */
    int WAIT = 0x00000800;
    /**
     * 数据异常
     */
    int DATAFORMAT_ERROR = 0x00001000;
    /**
     * 请求超时
     */
    int TIME_OUT = 0x00002000;
    /**
     * 地址错误
     */
    int URL_EXCEPTION = 0x00004000;
    /**
     * 消息接收
     */
    int ACCEPT_MSG = 0x00005000;
}
