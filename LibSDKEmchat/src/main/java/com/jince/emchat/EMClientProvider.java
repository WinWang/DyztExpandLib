package com.jince.emchat;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.jince.emchat.domain.ChatMessage;
import com.jince.emchat.domain.ChatService;
import com.jince.emchat.domain.Counselor;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.content.Context.ACTIVITY_SERVICE;

public final class EMClientProvider {

    private static final String TAG = "EaseIMProvider";
    private static final String ACTIVITY = "com.jince.customer.CustomerChatActivity";

    public static final String CMD_WECHAT = "wechat";
    public static final String CMD_SERVICE = "service";
    public static final String CMD_IM = "im";
    public static final String CMD_LINK4SALES = "externalUserWechat";

    private WeakReference<Context> mReference;
    private NotificationManager mNotificationManager;

    private EMMessageAdapter mMessageAdapter;
    private final List<Integer> mIds = new ArrayList<>();
    private final List<EMUnreadMessageListener> mUnreadMessageListeners = new ArrayList<>();
    private final List<MessageCallback> mChatMessageCallbacks = new ArrayList<>();

    private final EMClientConnectionListener mConnectionListener = new EMClientConnectionListener();

    private boolean mChatIsForeground = false;
    // 客服
    private Counselor mCounselor;
    // 基础客服（随机/专属） + 当前聊天客服，确保实时消息到达时可以被匹配
    private final Set<String> mCurrentCMDs = new HashSet<>(2);
    // 默认为随机客服
    private int mCurrentChatServiceType = 6;
    // 聊天客服列表(随机/专属) + 当前聊天客服
    // 在任意页面联系客服时，需要将当前客服切换为“专属”客服
    private final SparseArray<ChatService> mServiceMap = new SparseArray<>();

    private EMClientProvider(){}

    private static final class EaseImProviderHolder {
        private static final EMClientProvider INSTANCE = new EMClientProvider();
    }


    public static EMClientProvider getInstance() {
        return EaseImProviderHolder.INSTANCE;
    }

    public static void createInstance(Context context, boolean debug) {
        if (context == null) {
            throw new IllegalArgumentException("Arguments context must not be null");
        }
        EaseImProviderHolder.INSTANCE.init(context.getApplicationContext(), debug);
    }


    private void init(Context context, boolean debug) {
        if (context instanceof Application) {
            mReference = new WeakReference<>(context);
        } else {
            mReference = new WeakReference<>(context.getApplicationContext());
        }
        EMOptions options = new EMOptions();
        //默认添加好友时，是不需要验证的，改成需要验证
        // options.setAcceptInvitationAlways(true);
        options.setAutoLogin(false);
        //初始化
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(context, pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        //EMPushConfig.Builder builder = new EMPushConfig.Builder(context);
        // 离线推送证书相关信息配置在AndroidManifest.xml中enableVivoPush()
        // builder.enableMiPush(XIAOMI_APP_ID, XIAOMI_APP_KEY)
        //        .enableHWPush(); //开发者需要调用该方法来开启华为推送
        // options.setPushConfig(builder.build());
        EMClient.getInstance().init(context, options);
        Log.d(TAG,  "EMClient init");
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(debug);
        EMClient.getInstance().addConnectionListener(mConnectionListener);
        registerMessageListener();
    }

    private void registerMessageListener() {
        if (mMessageAdapter == null) {
            mMessageAdapter = new EMMessageAdapter() {

                @Override
                public void onMessageReceived(List<EMMessage> list) {
                    for (EMMessage message : list) {
                        notifyMessageReceived(message);
                    }
                }
            };
            EMClient.getInstance().chatManager().addMessageListener(mMessageAdapter);
        }
    }

    private void notifyMessageReceived(EMMessage message) {
        if (message == null) {
            return;
        }
        EMMessage.ChatType chatType = message.getChatType();
        if (chatType == EMMessage.ChatType.Chat) {
            ChatMessage chatMessage = handleChatMessage(message);
            if (chatMessage == null) {
                return;
            }
//            String cmd = chatMessage.getCmd();
//            if (!mCurrentCMDs.contains(cmd)) {
//                // CMD与当前客服类型不匹配
//                return;
//            }
            Context context = mReference.get();
            // Check foreground chat ui.
            // Refresh chat ui if exists, send notification and show red dot tips otherwise.
            if (!mChatIsForeground || !isForegroundActivity(ACTIVITY, context)) {
                //sendNotification(chatMessage);
                int count = EMClient.getInstance().chatManager().getUnreadMessageCount();
                Log.d(TAG, "Get unread message count: " + count);
                for (EMUnreadMessageListener listener : mUnreadMessageListeners) {
                    listener.onUnreadMessageCount(count);
                    listener.onUnreadChatMessage(chatMessage);
                }
            } else {
                // 聊天页在最前台展示
                clearUnreadMessageStatus();
            }
            for (MessageCallback callback : mChatMessageCallbacks) {
                callback.onChatMessage(chatMessage);
            }
        } else if (chatType == EMMessage.ChatType.ChatRoom) {
            // Chatroom message
        } else {
            // ELSE DO NOTHING
        }
    }

    private void sendNotification(ChatMessage message) {
        Context context = mReference.get();
        if (context == null) {
            return;
        }
        if (TextUtils.equals(CMD_WECHAT, message.getCmd())
                || TextUtils.equals(CMD_LINK4SALES, message.getCmd())) {
            // 来自企业号|外部联系人的回复
            return;
        }
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id;
        try {
            id = Integer.parseInt(message.getUid());
        } catch (NumberFormatException e) {
            id = new Random().nextInt(10000);
        }
        Intent intent = new Intent();
        intent.setClassName(context, ACTIVITY);
        // Use CLEAR_TOP instead of NEW_TASK, which causes more than 1 activity will be created when notification message(s) clicked.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        boolean flag = false;
        if (manager != null && !manager.getRunningTasks(Integer.MAX_VALUE).isEmpty()) {
            // Check if MainActivity exists in stack.
            List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo info : tasks) {
                String className = info.topActivity.getClassName();
                Log.d(TAG, "RunningTask: " + className);
                if (TextUtils.equals("com.jindashi.stockcircle.business.home.activity.HomeTabActivity", className)
                        || TextUtils.equals(ACTIVITY, className)) {
                    flag = true;
                    break;
                }
            }
        }

        PendingIntent pendingIntent;
        if (flag) {
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            // 通过此种方式创建PendingIntent，在应用程序不可见（放置在后台），配合ParentActivity属性可以将MainActivity唤起。
            pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.notification_channel_3_id), context.getString(R.string.notification_channel_3_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.notification_channel_3_desc));
            channel.enableLights(true);
            channel.enableVibration(true);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "103");
        builder.setContentTitle(message.getNickname())
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), getStatusBarSmallIcon(context, Build.VERSION.SDK_INT >= 21 ? 0x7f100056 : 0x7f100050)))
                .setDefaults(Notification.DEFAULT_SOUND);

        if (ChatMessage.MESSAGE_TYPE_TEXT.equals(message.getCategory())) {
            builder.setContentText(message.getContent());
            builder.setTicker(message.getContent());
        } else if (ChatMessage.MESSAGE_TYPE_IMAGE.equals(message.getCategory())) {
            builder.setContentText(context.getString(R.string.receive_image));
            builder.setTicker(context.getString(R.string.receive_image));
        } else if (ChatMessage.MESSAGE_TYPE_AUDIO.equals(message.getCategory())) {
            builder.setContentText(context.getString(R.string.receive_audio));
            builder.setTicker(context.getString(R.string.receive_audio));
        } else if (ChatMessage.MESSAGE_TYPE_VIDEO.equals(message.getCategory())) {
            builder.setContentText(context.getString(R.string.receive_video));
            builder.setTicker(context.getString(R.string.receive_video));
        } else if (ChatMessage.MESSAGE_TYPE_FILE.equals(message.getCategory())) {
            builder.setContentText(context.getString(R.string.receive_file));
            builder.setTicker(context.getString(R.string.receive_file));
        } else {
            builder.setContentText(context.getString(R.string.receive_message));
            builder.setTicker(context.getString(R.string.receive_message));
        }

        Notification notification = builder.build();
        mNotificationManager.notify(id, notification);
        mIds.add(id);
    }

    public void cancelNotification() {
        Context context = mReference.get();
        if (context != null) {
            for (int i = 0; i < mIds.size(); i++) {
                if (mNotificationManager != null) mNotificationManager.cancel(mIds.get(i));
            }
        }
    }

    /**
     * 登录环信
     * @param uid           环信uid
     * @param pass          环信pass
     * @param checkLogin    校验登录
     * @param listener      登录监听
     */
    public void login(final String uid, final String pass, final boolean checkLogin, final EMLoginListener listener) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(pass)) {
            return;
        }
        boolean result = false;
        if (checkLogin) {
            String currentUser = EMClient.getInstance().getCurrentUser();
            // 当前在线的环信用户为空时直接进行登录。
            result = !TextUtils.isEmpty(currentUser);
        }

        if (result) {
            // 已登录，先退出，再进行登录
            EMClient.getInstance().logout(true, new EMCallBack() {
                @Override
                public void onSuccess() {
                    login(uid, pass, false, listener);
                }

                @Override
                public void onError(int i, String s) {
                    Log.d(TAG, "CRM logout error: " + s + ", " + i);
                    // 退出失败，提示用户
                    if (listener != null) {
                        listener.onLoginFailed(i, s);
                    }
                }

                @Override
                public void onProgress(int i, String s) { /*DO NOTHING*/ }
            });
            return;
        }

        registerMessageListener();
        Log.d(TAG, "NAME: " + uid + ", PASS: " + pass);
        EMClient.getInstance().login(uid, pass, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                Log.d(TAG, "login Success " + uid);
                if (listener != null) listener.onLoginSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login onProgress " + status);
            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "login fail! " + "code=" + code + ",message=" + message);
                if (listener != null) listener.onLoginFailed(code, message);
            }
        });

    }

    public void logout() {
        EMClient.getInstance().chatManager().removeMessageListener(mMessageAdapter);
        mMessageAdapter = null;
        mCounselor = null;
        //使用异步方法 http://docs-im.easemob.com/im/android/sdk/basic
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public void saveCurrentServiceCmd(String cmd) {
        mCurrentCMDs.add(cmd);
    }

    public void saveCurrentChatService(ChatService service) {
        this.mCurrentChatServiceType = service.getChatServiceType();
        addChatServiceByType(service);
    }

    public ChatService getCurrentChatService() {
        return getChatServiceByType(mCurrentChatServiceType);
    }

    public <T extends ChatService> void addChatServiceByType(T service) {
        mServiceMap.put(service.getChatServiceType(), service);
    }

    public ChatService getChatServiceByType(int type) {
        return mServiceMap.get(type);
    }

    /**
     * 移除所有内存中保存的客服对象
     */
    public void removeAllService() {
        mCurrentCMDs.clear();
        mServiceMap.clear();
        mCurrentChatServiceType = 6;
    }

    /**
     * 将当前客服强制修改为专属/随机客服
     */
    public void switchToDefaultChatService() {
        int size = mServiceMap.size();
        for (int i = 0; i < size; i++) {
            int key = mServiceMap.keyAt(i);

            if ((key == 0) && key != mCurrentChatServiceType) {
                mCurrentChatServiceType = key;
                break;
            }
        }
    }

    public void setCounselor(Counselor counselor) {
        this.mCounselor = counselor;
    }

    public Counselor getCounselor() {
        return mCounselor;
    }

    // 设置当前聊天是否在前台显示的标志位。
    public void setChatIsForeground(boolean foreground) {
        this.mChatIsForeground = foreground;
    }

    public void addChatMessageListener(MessageCallback callback) {
        if (!mChatMessageCallbacks.contains(callback)) {
            mChatMessageCallbacks.add(callback);
        }
    }

    public void removeChatMessageListener(MessageCallback callback) {
        mChatMessageCallbacks.remove(callback);
    }

    public void addUnreadMessageListener(EMUnreadMessageListener listener) {
        if (!mUnreadMessageListeners.contains(listener)) {
            mUnreadMessageListeners.add(listener);
        }
    }

    public void removeUnreadMessageListener(EMUnreadMessageListener listener) {
        mUnreadMessageListeners.remove(listener);
    }

    public int getUnreadMessageCount() {
        if (!EMClient.getInstance().isLoggedInBefore()) {
            return 0;
        }
        return EMClient.getInstance().chatManager().getUnreadMessageCount();
    }

    public void clearUnreadMessageStatus() {
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
        for (EMUnreadMessageListener listener : mUnreadMessageListeners) {
            listener.onUnreadMessageCount(0);
        }
    }

    public void disconnect() {
        EMClient.getInstance().removeConnectionListener(mConnectionListener);
    }

    private static String getAppName(Context context, int pID) {
        String processName;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (am == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : list) {
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                Log.e("Process", "Error>> " + e.getMessage(), e);
            }
        }
        return null;
    }

    private static boolean isForegroundActivity(String activityName, Context context) {
        if (context == null || TextUtils.isEmpty(activityName)) {
            return false;
        }
        final ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && !list.isEmpty()) {
            final ComponentName cpn = list.get(0).topActivity;
            return activityName.equals(cpn.getClassName());
        }
        return false;
    }

    private static ChatMessage handleChatMessage(EMMessage message) {
        try {
            EMTextMessageBody msgBody = (EMTextMessageBody) message.getBody();
            JSONObject object = new JSONObject(msgBody.getMessage());
            String cmd = null;
            if (object.has("cmd")) {
                cmd = object.optString("cmd");
            }
            if (cmd == null || TextUtils.isEmpty(cmd)) {
                return null;
            }
            ChatMessage result = null;
            switch (cmd) {
                case CMD_SERVICE:
                case CMD_WECHAT:
                case CMD_LINK4SALES:
                case CMD_IM:
                    if (object.has("result")) {
                        result = new Gson().fromJson(object.optString("result"), ChatMessage.class);
                        result.setCmd(cmd);
                    }
                    break;

                default:
                    return null;
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "format json error: " + e.getMessage(), e);
        }
        return null;
    }

    private static class EMClientConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected");
        }

        @Override
        public void onDisconnected(int i) {
            Log.d(TAG, "onDisconnected");
        }
    }


    public interface MessageCallback {

        void onChatMessage(ChatMessage message);

    }


}
