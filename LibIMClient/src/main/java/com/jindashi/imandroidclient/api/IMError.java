package com.jindashi.imandroidclient.api;

/**
 * @ClassName: IMError
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/17 16:00
 * @Version: 1.0
 */
public enum IMError {
    authError(1, "登录认证失败"),
    dataFormatError(2, "消息格式解析错误"),
    imUnInitOption(3, "尚未初始化设置IMConnectOption"),
    imUserInfoInitError(4, "用户信息初始化失败"),
    imSendMessageError(5, "消息发送失败"),
    imNetWorkServiceError(6, "网络监控服务尚未初始化");
    private int errorCode;
    private String errorMessage;

    IMError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
