package com.jindashi.imandroidclient.socket;

import com.jindashi.imandroidclient.utils.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by jince on 2018/8/10.
 */

public class SocketWriter implements Callable<Integer> {
    private String TAG = "SocketWriter";
    private OutputStream out;

    private static LinkedBlockingQueue<DataSender> mWritingQueue = new LinkedBlockingQueue<>();

    public interface DataSender {
        public void write(OutputStream out) throws IOException;
    }

    public SocketWriter(OutputStream out) {
        this.out = out;
    }


    public void send(DataSender dataSender) {
        LogUtils.d("SocketClient", "send()");
        mWritingQueue.add(dataSender);
    }

    private void write() throws Exception {
        DataSender msg = null;
        while (null != (msg = mWritingQueue.poll())) {
            msg.write(out);
            out.flush();
            // 经过测试，客户端不能够连续发送数据过快，否则服务端处理不了
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void flush() {
        int state = SocketClient.getInstance().getState();
        LogUtils.d("SocketClient", "flush() state[" + state + "]");
        if (state != SocketState.OK) {
            // 通知未连接的状态
            LogUtils.e("SocketClient", "notconnected reconnect state[" + state + "]");
            mWritingQueue.clear();
            SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CONNECT);
            return;
        }

        if (mWritingQueue.size() > 100) {
            LogUtils.e("SocketClient", "too many data in writingQueue , clear it");
            mWritingQueue.clear();
            return;
        }

        SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.WRITE);
    }

    public void clear() {
        mWritingQueue.clear();
    }

    @Override
    public Integer call() throws Exception {
        LogUtils.d("SocketWriter", "start SocketWriter thread");
        try {
            write();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d("SocketWriter", "end SocketWriter thread");
        return SocketClient.getInstance().getState();
    }
}
