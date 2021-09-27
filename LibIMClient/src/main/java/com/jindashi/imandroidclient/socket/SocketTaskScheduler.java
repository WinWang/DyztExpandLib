package com.jindashi.imandroidclient.socket;

import com.jindashi.imandroidclient.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Created by jince on 2018/8/10.
 */
public class SocketTaskScheduler {
    private static SocketTaskScheduler scheduler;
    private static ExecutorService mExecutor;
    /*任务集合*/
    private static Map<String, Future<?>> tasks = new HashMap<>();

    public static final String CONNECT = "CONNECT";
    public static final String WRITE = "WRITE";
    public static final String CLOSE = "CLOSE";
    public static final String READ = "READ";
    public static final String HEART = "HEART";
    private SocketClient socketClient;

    /**
     * 获取单例
     *
     * @return
     */
    public static SocketTaskScheduler getInstance() {
        if (scheduler == null)
            synchronized (SocketTaskScheduler.class) {
                if (scheduler == null) {
                    scheduler = new SocketTaskScheduler();
                }
            }
        return scheduler;
    }

    private SocketTaskScheduler() {
        mExecutor = Executors.newFixedThreadPool(3, new ThreadFactory() {
            int index = 0;

            @Override
            public Thread newThread(Runnable runnable) {
                //重写线程名称
                return new Thread(runnable, "cheeses-runnable" + index++);
            }
        });
        socketClient = SocketClient.getInstance();
    }

    public void addTask(String task) {
        Future future = tasks.get(task);
        if (future != null) {
            if ((future.isDone() || future.isCancelled())) {
                LogUtils.e("SocketTaskScheduler", "last task is done:" + task);
                // 任务已经执行过，移除任务和重写执行
                // tasks.remove(future);
            } else {
                LogUtils.e("SocketTaskScheduler", "task is running:" + task);
                // 任务正在进行
                if (task.equals(READ) || task.equals(HEART)) {
                    tasks.remove(future);
                    future.cancel(true);
                } else
                    return;
            }
        }
        // 当任务正在执行或者等待执行则不重复添加任务
        switch (task) {
            case WRITE:
                // 执行最频繁的任务
                write();
                break;
            case READ:
                // 每次连接成功后执行阻塞式任务
                read();
                break;
            case CONNECT:
                Future closeFuture = tasks.get(SocketTaskScheduler.CLOSE);
                if (null != closeFuture) {
                    if (!(closeFuture.isDone() || closeFuture.isCancelled())) {
                        return;//连接线程调度前，互斥正在执行的关闭线程
                    }
                }
                // 连接或断开后重连
                if (!socketClient.getTASK_CONNECT().compareAndSet(false, true)) {
                    LogUtils.e("SocketTaskScheduler", "task connect already running");
                    return;
                }
                connect();
                break;
            case CLOSE:
                Future connectFuture = tasks.get(SocketTaskScheduler.CONNECT);
                if (null != connectFuture) {
                    if (!(connectFuture.isDone() || connectFuture.isCancelled())) {
                        return;//连接线程调度前，互斥正在执行的关闭线程
                    }
                }
                if (!socketClient.getTASK_CLOSE().compareAndSet(false, true)) {
                    LogUtils.e("SocketTaskScheduler", "task close already running");
                    return;
                }
                // 连接断开或处理数据异常，关闭连接
                close();
                break;
            case HEART:
                if (null != socketClient.getHeart() && !socketClient.getHeart().getTASK_RUNNING().compareAndSet(false, true)) {
                    LogUtils.e("SocketTaskScheduler", "task heart already running");
                    return;
                }
                heart();
                break;

        }
    }

    /**
     * 临时线程，执行完即退出。
     */
    public void connect() {
        tasks.put(CONNECT, mExecutor.submit(socketClient));
        LogUtils.e("SocketTaskScheduler", "submit task:" + CONNECT);
    }

    /**
     * 临时线程，执行完即退出。
     */
    public void close() {
        tasks.put(CLOSE, mExecutor.submit(socketClient));
        LogUtils.e("SocketTaskScheduler", "submit task:" + CLOSE);
    }

    /**
     * 临时线程，执行完即退出。
     */
    public void write() {
        if (socketClient.getWriter() != null && SocketClient.getInstance().getState() == SocketState.OK) {
            tasks.put(WRITE, mExecutor.submit(socketClient.getWriter()));
            LogUtils.e("SocketTaskScheduler", "submit task:" + WRITE);
        }
    }

    /**
     * Socket上下文生命期，读线程是常驻的，因为是个死循环。
     * socket创建后生命期内只会添加一次。
     */
    public void read() {
        if (socketClient.getReader() != null && SocketClient.getInstance().getState() == SocketState.OK) {
            tasks.put(READ, mExecutor.submit(socketClient.getReader()));
            LogUtils.e("SocketTaskScheduler", "submit task:" + READ);
        }
    }

    /**
     * 心跳线程，常驻线程
     */
    private void heart() {
        if (socketClient.getHeart() != null) {
            tasks.put(HEART, mExecutor.submit(socketClient.getHeart()));
            LogUtils.e("SocketTaskScheduler", "submit task:" + HEART);
        }
    }
}
