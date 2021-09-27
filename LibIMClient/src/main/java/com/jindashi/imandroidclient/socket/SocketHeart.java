package com.jindashi.imandroidclient.socket;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jindashi.imandroidclient.model.BaseMsg;
import com.jindashi.imandroidclient.model.CmdEnum;
import com.jindashi.imandroidclient.utils.LogUtils;
import com.jindashi.imandroidclient.utils.SocketUtils;

import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by jince on 2018/8/10.
 */

public class SocketHeart implements Callable<Integer> {
    /**
     * 心跳包间隔
     */
    private long HEART_BEAT_INTERVAL = 10 * 1000;
    private String TAG = "SocketHeart";
    private OutputStream out;
    private String heart;
    private AtomicBoolean TASK_RUNNING = new AtomicBoolean(false);

    public SocketHeart(OutputStream out) {
        this.out = out;
    }

    @Override
    public Integer call() throws Exception {
        LogUtils.e(TAG, "start SocketHeart thread");
        while (SocketClient.getInstance().getState() == SocketState.OK) {
            Thread.sleep(HEART_BEAT_INTERVAL);
            try {
                if (SocketClient.getInstance().getState() == SocketState.OK) {
                    if (TextUtils.isEmpty(getHeart()))
                        break;
                    SocketUtils.writeTo(out, getHeart());
                    out.flush();
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "socket heart error", e);
                TASK_RUNNING.set(false);
                SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CLOSE);
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "end SocketHeart thread");
        return SocketClient.getInstance().getState();
    }

    private String getHeart() {
        if (TextUtils.isEmpty(heart)) {
            BaseMsg heartMsg = new BaseMsg();
            heartMsg.setCmd(CmdEnum.CMD_HEART.getCmd());
            return heart = JSON.toJSONString(heartMsg);
        }
        return heart;
    }

    public AtomicBoolean getTASK_RUNNING() {
        return TASK_RUNNING;
    }

    public void setTASK_RUNNING(AtomicBoolean TASK_RUNNING) {
        this.TASK_RUNNING = TASK_RUNNING;
    }

    /**
     * 设置心跳间隔
     *
     * @param HEART_BEAT_INTERVAL
     */
    public void setHEART_BEAT_INTERVAL(long HEART_BEAT_INTERVAL) {
        this.HEART_BEAT_INTERVAL = HEART_BEAT_INTERVAL;
    }
}
