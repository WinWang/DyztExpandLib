package com.jindashi.imandroidclient.socket;

import com.jindashi.imandroidclient.api.SocketCallBack;
import com.jindashi.imandroidclient.utils.LogUtils;
import com.jindashi.imandroidclient.utils.SocketUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;


/**
 * Created by jince on 2018/8/10.
 */

public class SocketReader implements Callable<Integer> {
    private String TAG = "SocketReader";

    private InputStream in;

    private static final int BLOCK_SIZE = 1024 * 100;

    private byte[] by = new byte[BLOCK_SIZE];//建立100k的固定缓冲区

    private ByteArrayOutputStream baos = new ByteArrayOutputStream(BLOCK_SIZE * 3);//缓冲读取到的字节数组

    private int total, block, read;//记录已经读取到的数据总长度
    private SocketCallBack socketCallBack;

    public SocketReader(InputStream in, SocketCallBack socketCallBack) {
        this.in = in;
        this.socketCallBack = socketCallBack;
    }

    @Override
    public Integer call() {
        LogUtils.e(TAG, "start SocketReader thread");
        try {
            while (SocketClient.getInstance().getState() == SocketState.OK) {
                if (SocketClient.getInstance().getState() == SocketState.OK) {
                    //读取数据包内容长度
                    int len = readLength();
                    if (len <= 0) {
                        break;
                    }
                    if (readContent(len) == -1) {
                        break;
                    }
                    if (len > 0) {
                        if (null != socketCallBack)
                            socketCallBack.acceptMessage(baos.toByteArray());
                    }
                }
            }
        } catch (SocketTimeoutException e) {
            LogUtils.e(TAG, "socket read timeout", e);
        } catch (Exception e) {
            LogUtils.e(TAG, "other socketexception", e);
        } finally {
            LogUtils.e(TAG, "finally SocketReader thread");
            SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CLOSE);
        }
        LogUtils.e(TAG, "end SocketReader thread");
        return SocketClient.getInstance().getState();
    }

    public int readLength() throws IOException {
        byte[] b = new byte[4];
        // 此处抛的异常交给SocketClient处理
        read = in.read(b);
        if (read == 0) {
            //日志上看，不会发生，无需关闭链接
            String log = "SocketReader readLength() readByteLength[" + read + "], end recieveThread";
            LogUtils.e(TAG, log);
            return 0;//没有数据，直接返回0
        } else if (read < 0) {
            //日志上看，经常发生，若发生关闭链接
            String log = "SocketReader readLength() readByteLength[" + read + "], end recieveThread";
            LogUtils.e(TAG, log);
            return -1;
        }
        int len = SocketUtils.bytesToInt(b);
        if (len > 5242880 || len < 0) {
            // 日志上看，不会执行到这里，若发生，关闭链接
            // 可能会发生内存溢出的异常，则舍弃数据
            // 超过5m丢弃，防止内存溢出
            String log = "SocketReader readLength() parseByteLength[" + len + "], end recieveThread";
            LogUtils.e(TAG, log);
            //因为输入流错乱了，跳过剩余的数据
            return -1;
        } else if (len == 0) {
            //日志上看，经常发生，不关闭链接
            String log = "SocketReader readLength() parseByteLength[" + len + "], no end recieveThread";
            LogUtils.e(TAG, log);
        }
        return len;
    }

    public int readContent(int len) throws IOException {
        baos.reset();//重置数据缓冲区
        total = 0;//重置读取的总长度
        block = len < BLOCK_SIZE ? len : BLOCK_SIZE;//设置读取块的大小,如果小于默认块则直接使用实际长度
        read = 0;
        while (total < len && (read = in.read(by, 0, block)) > 0) {
            total += read;
            baos.write(by, 0, read);
            if (len - total < block)
                block = len - total;
        }

        if (total < len) {
            String log = "SocketReader readContent(" + len + ") readTotal[" + total + "], end recieveThread";
            LogUtils.e(TAG, log);
            //数据内容未读完整
            return -1;
        }
        return 0;
    }
}
