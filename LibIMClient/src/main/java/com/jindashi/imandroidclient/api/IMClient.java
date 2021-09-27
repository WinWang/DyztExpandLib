package com.jindashi.imandroidclient.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jindashi.imandroidclient.model.Auth1;
import com.jindashi.imandroidclient.model.AuthBean;
import com.jindashi.imandroidclient.model.AutoLinkMode;
import com.jindashi.imandroidclient.model.BaseMsg;
import com.jindashi.imandroidclient.model.CmdEnum;
import com.jindashi.imandroidclient.socket.IMNetStatusService;
import com.jindashi.imandroidclient.socket.SocketClient;
import com.jindashi.imandroidclient.socket.SocketState;
import com.jindashi.imandroidclient.socket.SocketTaskScheduler;
import com.jindashi.imandroidclient.socket.SocketWriter;
import com.jindashi.imandroidclient.utils.LogUtils;
import com.jindashi.imandroidclient.utils.MD5Utils;
import com.jindashi.imandroidclient.utils.SocketUtils;
import com.jindashi.imandroidclient.utils.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @ClassName: IMClient
 * @Description: java类作用描述
 * @Author: xxy
 * @CreateDate: 2019/7/16 15:23
 * @Version: 1.0
 */
public class IMClient {
    private IMConnectOption imConnectOption;
    private IMCallBack imCallBack;
    private IMConnectListener imConnectListener;
    private volatile static IMClient imClient;
    private int reconnectCount = 0;//尝试重新连接次数
    private static final int RE_CONNECT_COUNT = 3;//尝试重新连接次数
    private NetWorkChangeInter netWorkChangeInter;
    private boolean pauseState = false;

    public static IMClient getInstance() {
        if (null == imClient) {
            synchronized (IMClient.class) {
                if (null == imClient) {
                    imClient = new IMClient();
                }
            }
        }
        return imClient;
    }

    public NetWorkChangeInter getNetWorkChangeInter() {
        return netWorkChangeInter;
    }

    public void setNetWorkChangeInter(NetWorkChangeInter netWorkChangeInter) {
        this.netWorkChangeInter = netWorkChangeInter;
    }

    /**
     * 初始化
     */
    public void init(String ip, int port) {
        SocketClient.getInstance().setIpAndPort(ip, port);
    }

    /**
     * 心跳检测
     */
    private final Handler heartHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://心跳
                    LogUtils.d("---------IM心跳在双倍心跳时间间隔内都未收到回调，怀疑Socket已断开，重新连接---------");
                    conncet();
                    break;
            }
        }
    };

    /**
     * 重连
     */
    private Handler reConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            {
                conncet();
            }
        }
    };


    /**
     * 初始化IM连接参数
     *
     * @param imConnectOption
     */
    public void setImConnectOption(IMConnectOption imConnectOption) {
        this.imConnectOption = imConnectOption;
        SocketClient.getInstance().setImConnectOption(imConnectOption);
    }

    /**
     * 错误
     *
     * @param imError
     */
    private void error(IMError imError) {
        new android.os.Handler(Looper.getMainLooper()).post(new UIRunnable(UIRunnable.error, null, imError));
    }

    /**
     * Socket连接
     */
    public void conncet() {
        if (null == imConnectOption) {
            error(IMError.imUnInitOption);
            return;
        }
        if (StringUtils.isEmpty(imConnectOption.getUid()) || imConnectOption.getUserType() < 1) {
            error(IMError.imUserInfoInitError);
            return;
        }
//        if (StringUtils.isEmpty(imConnectOption.getUid()) || StringUtils.isEmpty(imConnectOption.getNickname()) || imConnectOption.getUserType() < 1) {
//            error(IMError.imUserInfoInitError);
//            return;
//        }
        LogUtils.e("---------task connect init---------");
        SocketClient.getInstance().setSocketCallBack(new SocketCallBack() {
            @Override
            public void linking() {
                LogUtils.d("---------IM连接中---------");
                new android.os.Handler(Looper.getMainLooper()).post(new UIRunnable(UIRunnable.linking, null, null));
            }

            @Override
            public void connectFailure() {
                reconnectCount++;
                LogUtils.d("---------IM连接失败---------连接次数：" + reconnectCount);
                if (reconnectCount < RE_CONNECT_COUNT) {
                    reConnectHandler.sendEmptyMessageDelayed(1, 3000L);//连接失败，再次尝试连接
                }
                new android.os.Handler(Looper.getMainLooper()).post(new UIRunnable(UIRunnable.connectFailure, null, null));
            }

            @Override
            public void connectSuccess() {
                LogUtils.d("---------IM连接成功---------");
                reconnectCount = 0;
                reConnectHandler.removeCallbacksAndMessages(null);
                new android.os.Handler(Looper.getMainLooper()).post(new UIRunnable(UIRunnable.connectSuccess, null, null));
            }

            @Override
            public void acceptMessage(byte[] data) {
                LogUtils.d("---------IM接收消息---------");
                try {
                    String result = new String(data, "UTF-8");
                    if (StringUtils.isEmpty(result))
                        return;
                    BaseMsg<String> realBaseMsg = JSON.parseObject(result, new TypeReference<BaseMsg<String>>() {
                    });
                    if (null == realBaseMsg) {
                        error(IMError.dataFormatError);
                    }
                    //心跳
                    if (realBaseMsg.getCmd().equals(CmdEnum.CMD_HEART.getCmd())) {
                        LogUtils.d("---------IM心跳---------");
                        heartHandler.removeCallbacksAndMessages(null);
                        if (SocketClient.getInstance().getHeart().getTASK_RUNNING().get()) {
                            heartHandler.sendEmptyMessageDelayed(1, imConnectOption.getHEART_BEAT_INTERVAL() * 2);
                        }
                    }
                    //登录认证
                    else if (realBaseMsg.getCmd().equals(CmdEnum.CMD_AUTH1.getCmd())) {
                        LogUtils.d("---------IM登录认证---------");
                        auth(result);
                    }
                    //其他消息
                    else {
                        if (pauseState)
                            return;
                        new android.os.Handler(Looper.getMainLooper()).post(new UIRunnable(UIRunnable.acceptMessage, realBaseMsg, null));
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                    error(IMError.dataFormatError);
                }

            }

            @Override
            public void close() {
                LogUtils.d("---------IM连接关闭---------");
                new android.os.Handler(Looper.getMainLooper()).post(new UIRunnable(UIRunnable.close, null, null));
            }


            @Override
            public void error(IMError imError) {
                LogUtils.d("---------IMError---------errorCode=" + imError.getErrorCode());
                new android.os.Handler(Looper.getMainLooper()).post(new UIRunnable(UIRunnable.error, null, imError));
            }
        });
        SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CONNECT);

    }


    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (SocketClient.getInstance().getState() == SocketState.OK) {
            heartHandler.removeCallbacksAndMessages(null);
            SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CLOSE);
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnect(String roomId) {
        if (SocketClient.getInstance().getState() == SocketState.OK) {
            if (SocketClient.getInstance().getRoomId().equals(roomId)) {
                heartHandler.removeCallbacksAndMessages(null);
                SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CLOSE);
            }
        }
    }


    /**
     * 暂停消息分发，socket状态不影响
     */
    public void pause() {
        this.pauseState = true;
    }

    public void resume() {
        if (null == imConnectOption)
            return;
        if (SocketClient.getInstance().getState() != SocketState.OK) {
            reConnectHandler.sendEmptyMessage(1);
        }
        this.pauseState = false;
    }

    /**
     * 发送自定义格式消息
     *
     * @param message
     */
    public void sendMessage(final String message) {
        if (StringUtils.isEmpty(message)) {
            error(IMError.imSendMessageError);
            return;
        }
        SocketClient.getInstance().send(new SocketWriter.DataSender() {
            @Override
            public void write(OutputStream out) throws IOException {
                SocketUtils.writeTo(out, message);
            }
        });
        SocketClient.getInstance().flush();
    }

//    /**
//     * 发送聊天消息
//     *
//     * @param message
//     */
//    public void sendChatMessage(String message) {
//        if (!StringUtils.isEmpty(message)) {
//            UserBean userBean = new UserBean();
//            userBean.setUid(imConnectOption.getUid());
//            userBean.setUserType(imConnectOption.getUserType());
//            userBean.setIcon(imConnectOption.getIcon());
//            userBean.setNickname(imConnectOption.getNickname());
//            ChatMsg chatMsg = new ChatMsg();
//            chatMsg.setMessage(message);
//            chatMsg.setUser(userBean);
//            sendMessage(JSON.toJSONString(chatMsg));
//        }
//    }

    /**
     * 是否开启网络变化监听
     *
     * @param action true：开启监听服务，false 关闭监听服务
     */
    public void setAutoCheckNetChange(Context context, boolean action) {
        if (null == context)
            return;
        if (null == netWorkChangeInter && action) {
            error(IMError.imNetWorkServiceError);
            return;
        }
        SocketClient.getInstance().setAutoCheckNetChange(action);
        try {
            Intent intent = new Intent(context, IMNetStatusService.class);
            if (action)
                context.startService(intent);
            else
                context.stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置自动连接模式
     *
     * @param autoLinkMode
     */
    public void setAutoLinkMode(AutoLinkMode autoLinkMode, String ActivityName) {
        if (null == autoLinkMode)
            return;
        switch (autoLinkMode) {
            case MODE_ACTIVITY:
                SocketClient.getInstance().setAutoLinkMode(AutoLinkMode.MODE_ACTIVITY);
                SocketClient.getInstance().setAutoLinkActivityName(ActivityName);
                break;
            case MODE_APP:
                SocketClient.getInstance().setAutoLinkMode(AutoLinkMode.MODE_APP);
                break;
        }
    }

    /**
     * socket认证随机码
     *
     * @param response
     * @return
     */
    private void auth(String response) {
        try {
            if (null == imConnectOption || StringUtils.isEmpty(imConnectOption.getUid()) || StringUtils.isEmpty(imConnectOption.getSCOKET_AUTHKEY()) || StringUtils.isEmpty(imConnectOption.getRoomId())) {
                error(IMError.authError);
            }
//            BaseMsg<Auth1> realBaseMsg = new Gson().fromJson(response, new TypeToken<BaseMsg<Auth1>>() {
            BaseMsg<Auth1> realBaseMsg = JSON.parseObject(response, new TypeReference<BaseMsg<Auth1>>() {
            });
            if (null == realBaseMsg || null == realBaseMsg.getResult()) {
                error(IMError.authError);
                return;
            }
            String token = realBaseMsg.getResult().getSeed();
            if (!StringUtils.isEmpty(token)) {
                token = token + imConnectOption.getSCOKET_AUTHKEY();
                AuthBean authBean = new AuthBean();
                authBean.setCmd(CmdEnum.CMD_AUTH2.getCmd());
                authBean.setMsg_id(1);
                authBean.setAuthCode(MD5Utils.encode(token));
                authBean.setUserId(imConnectOption.getUid());
                authBean.setRoom_id(imConnectOption.getRoomId());
                String authStr = JSON.toJSONString(authBean);
                LogUtils.d("---------IM登录认证信息---------" + authStr);
                sendMessage(authStr);
                SocketClient.getInstance().setRoomId(imConnectOption.getRoomId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            error(IMError.authError);
        }
    }

    /**
     * 注册Socket连接回调
     */
    public void setOnIMConnectListener(IMConnectListener listener) {
        this.imConnectListener = listener;
    }

    /**
     * 注册Im数据回调
     *
     * @param imCallBack
     */
    public void setOnIMCallBack(IMCallBack imCallBack) {
        this.imCallBack = imCallBack;
    }


    /**
     * 连接runnable
     */
    private class UIRunnable implements Runnable {
        private int type = 0;
        public static final int linking = 1;
        public static final int connectFailure = 2;
        public static final int connectSuccess = 3;
        public static final int acceptMessage = 4;
        public static final int close = 5;
        public static final int error = 6;
        private BaseMsg<String> realMsg;
        private IMError imError;

        public UIRunnable(int type, BaseMsg<String> realMsg, IMError imError) {
            this.type = type;
            this.realMsg = realMsg;
            this.imError = imError;
        }

        @Override
        public void run() {
            switch (type) {
                case linking:
                    if (null != imConnectListener)
                        imConnectListener.linking();
                    break;
                case connectFailure:
                    if (null != imConnectListener)
                        imConnectListener.connectFailure();
                    break;
                case connectSuccess:
                    if (null != imConnectListener)
                        imConnectListener.connectSuccess();
                    break;
                case acceptMessage:
                    if (null != imCallBack) {
                        imCallBack.acceptMessage(realMsg);
                    }
                    break;
                case close:
                    if (null != imCallBack) {
                        imCallBack.close();
                    }
                    break;
                case error:
                    if (null != imCallBack) {
                        imCallBack.error(imError.getErrorCode(), imError.getErrorMessage());
                    }
                    break;
            }
        }
    }
}
