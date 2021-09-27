package com.jindashi.imandroidclient.socket;

import com.jindashi.imandroidclient.api.IMConnectOption;
import com.jindashi.imandroidclient.api.SocketCallBack;
import com.jindashi.imandroidclient.model.AutoLinkMode;
import com.jindashi.imandroidclient.utils.LogUtils;
import com.jindashi.imandroidclient.utils.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jince on 2018/8/10.
 */

public class SocketClient implements Callable<Integer> {

    private volatile static SocketClient socketClient;

    private Socket socket;

    private SocketWriter writer;

    private SocketReader reader;

    private SocketHeart heart;

    private AtomicBoolean TASK_CONNECT = new AtomicBoolean(false);

    private AtomicBoolean TASK_CLOSE = new AtomicBoolean(false);

    private AtomicBoolean CLOSING = new AtomicBoolean(false);

    /*Socket链接状态*/
    private AtomicInteger state = new AtomicInteger(SocketState.NO_LINK);

    private boolean autoCheckNetChange = false;
    private IMConnectOption imConnectOption;
    private SocketCallBack socketCallBack;
    private AutoLinkMode autoLinkMode = AutoLinkMode.MODE_APP;
    private String autoLinkActivityName;
    private String Ip;
    private int Port;
    private String roomId;//记录当前连接的房间id

    private SocketClient() {
        state.set(SocketState.NO_LINK);
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static SocketClient getInstance() {
        if (socketClient == null)
            synchronized (SocketClient.class) {
                if (socketClient == null) {
                    socketClient = new SocketClient();
                }
            }
        return socketClient;
    }

    public void setState(int state) {
        this.state.set(state);
    }

    public int getState() {
        return state.get();
    }

    public void setImConnectOption(IMConnectOption imConnectOption) {
        this.imConnectOption = imConnectOption;
    }

    public void setIpAndPort(String ip, int port) {
        Ip = ip;
        Port = port;
    }

    /**
     * 链接socket。
     */
    private void connect() {
        if (state.get() == SocketState.OK) {
            LogUtils.e("SocketClient", "connect, do not reconnect");
            //已经连接不触发
            return;
        }
        state.set(SocketState.NO_LINK);
        if (!StringUtils.isEmpty(Ip)) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(Ip, Port);
            connect(inetSocketAddress);
        }

    }

    /**
     * 链接行情服务器，这是个长连接。
     */
    private void connect(InetSocketAddress address) {
        try {
            state.set(SocketState.LINKING);
            LogUtils.d("SocketClient", "conneting...[" + address.toString() + "]");
            if (null != socketCallBack)
                socketCallBack.linking();
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);//2h空闲时发送确认连接包，若服务端无响应，则close socket
            socket.setSoTimeout(imConnectOption.getSOCKET_READ_TIME_OUT());//设置读数据超时，默认一直等待；设置180秒无反馈关闭socket
            socket.setTrafficClass(0x04 | 0x10);
            socket.setSoLinger(true, 0);//close会立刻关闭socket,同时也会立刻返回
            socket.connect(address, imConnectOption.getCONNECT_TIME_OUT());
            state.set(SocketState.OK);
            reader = new SocketReader(socket.getInputStream(), socketCallBack);
            writer = new SocketWriter(socket.getOutputStream());
            heart = new SocketHeart(socket.getOutputStream());
            heart.setHEART_BEAT_INTERVAL(imConnectOption.getHEART_BEAT_INTERVAL());
            SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.READ);
            SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.HEART);
        } catch (Exception e) {
            LogUtils.e("SocketClient", "connet error", e);
            state.set(SocketState.FAIL);// socket链接失败
            if (null != socketCallBack)
                socketCallBack.connectFailure();
        }
        if (state.get() == SocketState.OK) {
            LogUtils.d("SocketClient", "conneted successful");
            // 清空等待发送队列
            writer.clear();
            // 通知业务逻辑模块，进行连接成功后的操作
            if (null != socketCallBack)
                socketCallBack.connectSuccess();
        }
    }

    private void close() {
        // 如果已经被关闭，不重复关闭
        if (state.get() != SocketState.CLOSE) {
            state.set(SocketState.CLOSE);
            if (socket != null) {
                LogUtils.d("SocketClient", "close link");
                // 关闭已经打开的socket
                try {
                    if (null != socketCallBack)
                        socketCallBack.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Integer call() throws Exception {
        LogUtils.d("SocketClient", "start socketclient thread");//当极快速开关连接的时候，极有可能发生上个连接线程还在执行中，未重置连接线程的状态的情况下，close线程执行.此时代码逻辑会走到链接方法，结果导致close线程永远没有机会释放状态，永远无法调用起关闭线程
        if (TASK_CONNECT.get()) {//解决方案，1线程执行类分开，相互独立，使得各自有机会释放状态  2线程调度时，使得两者互斥（最终采取此方案，减少无谓的线程调度，尝试连接和关闭）
            LogUtils.d("SocketClient", "connect()");
            connect();
            TASK_CONNECT.set(false);
        } else if (TASK_CLOSE.get()) {
            LogUtils.d("SocketClient", "close()");
            close();
            TASK_CLOSE.set(false);
        }
        LogUtils.d("SocketClient", "end socketclient thread");
        return state.get();
    }


    public void send(SocketWriter.DataSender data) {
        if (writer != null && state.get() == SocketState.OK)
            writer.send(data);
    }

    public void flush() {
        if (writer != null && state.get() == SocketState.OK)
            writer.flush();
        else
            SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CONNECT);
    }


    public AtomicBoolean getTASK_CONNECT() {
        return TASK_CONNECT;
    }

    public AtomicBoolean getTASK_CLOSE() {
        return TASK_CLOSE;
    }

    public SocketWriter getWriter() {
        return writer;
    }

    public SocketReader getReader() {
        return reader;
    }

    public SocketHeart getHeart() {
        return heart;
    }

    public void shutdown() {
        try {
            close();
            socketClient = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAutoCheckNetChange() {
        return autoCheckNetChange;
    }

    public void setAutoCheckNetChange(boolean autoCheckNetChange) {
        this.autoCheckNetChange = autoCheckNetChange;
    }

    public void setSocketCallBack(SocketCallBack socketCallBack) {
        this.socketCallBack = socketCallBack;
    }

    public AutoLinkMode getAutoLinkMode() {
        return autoLinkMode;
    }

    public void setAutoLinkMode(AutoLinkMode autoLinkMode) {
        this.autoLinkMode = autoLinkMode;
    }

    public String getAutoLinkActivityName() {
        return autoLinkActivityName;
    }

    public void setAutoLinkActivityName(String autoLinkActivityName) {
        this.autoLinkActivityName = autoLinkActivityName;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
