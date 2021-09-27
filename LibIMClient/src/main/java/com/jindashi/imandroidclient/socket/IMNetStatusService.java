package com.jindashi.imandroidclient.socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jindashi.imandroidclient.api.IMClient;
import com.jindashi.imandroidclient.model.AutoLinkMode;
import com.jindashi.imandroidclient.utils.LogUtils;


/**
 * Created by jince on 2018/8/13.
 */

public class IMNetStatusService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SocketClient.getInstance();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE"); // "android.net.conn.CONNECTIVITY_CHANGE"
        registerReceiver(mConnectivityChanged, intentFilter);
        LogUtils.e("SocketNetStatusService", "服务启动，广播绑定");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnectivityChanged != null)
            unregisterReceiver(mConnectivityChanged);
        LogUtils.e("SocketNetStatusService", "服务关闭，广播解绑");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 检测当前是否有可用连接，包括GPRS、wifi、3G、4G网络。
     */
    private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean hasConnectivity = false;
            ConnectivityManager manager = (ConnectivityManager) context
                    .getApplicationContext().getSystemService(
                            Context.CONNECTIVITY_SERVICE);

            if (manager == null) {
                hasConnectivity = false;
            } else {
                NetworkInfo networkinfo = manager.getActiveNetworkInfo();
                if (networkinfo != null && (networkinfo.isAvailable() || networkinfo.isConnectedOrConnecting())) {
                    hasConnectivity = true;
                }
            }
            if (hasConnectivity) {
                LogUtils.e("SocketNetStatusService", "网络已连接");
                // 网络开启，启动行情
                // 应用在前台执行的前提下
                if (SocketClient.getInstance().isAutoCheckNetChange() && IMClient.getInstance().getNetWorkChangeInter().isActivitiesResume()) {
                    LogUtils.e("SocketNetStatusService", "app foreground");
                    if (SocketClient.getInstance().getAutoLinkMode() == AutoLinkMode.MODE_ACTIVITY) {
                        if (TextUtils.isEmpty(IMClient.getInstance().getNetWorkChangeInter().getTargetActivity()))
                            return;
                        if (SocketClient.getInstance().getAutoLinkActivityName().equals(IMClient.getInstance().getNetWorkChangeInter().getTargetActivity())) {
                            LogUtils.e("SocketNetStatusService", "app foreground,AutoLinkMode.MODE_ACTIVITY ,targetActivity:" + IMClient.getInstance().getNetWorkChangeInter().getTargetActivity());
                            connect();
                        }
                    } else {
                        connect();
                    }
                }
            } else {
                LogUtils.e("SocketNetStatusService", "网络断开");
            }
        }
    };

    private void connect() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SocketTaskScheduler.getInstance().addTask(SocketTaskScheduler.CONNECT);
            }
        }, 2000);
    }

}
