package com.jince.emchat.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by weixuewu on 15/11/19.
 */
public class ChatMessage implements Serializable, Parcelable {
    public String id;
    public String uid;
    public String service_uid;
    public String from; // 1 代表发送的消息
    public String category;
    public String content;
    public String create_time;
    public String head_portrait;
    public String nickname;
    public String voice_time;
    public String device;
    private transient int status = 0;//0 加载中,1 成功,-1失败
    private transient CharSequence spannable;
    public transient int length;
    public transient int width;
    public transient int height;
    private transient boolean isPageTop = false;
    private transient boolean isLocal = false;
    private boolean isAudioPlay;
    //自动回复消息
    private AutoReplyContentBean autoReplyMessage; //自定义字段 为了存储 自动回复的消息
    private int msgType; // msgType == 1 属于自动回复的消息 自定义字段  为了区分是否是自动回复的消息

    private String cmd; // 对应环信实时消息外层结构的cmd.

    public final static String MESSAGE_TYPE_TEXT = "1"; //文本
    public final static String MESSAGE_TYPE_IMAGE = "2"; //图片
    public final static String MESSAGE_TYPE_AUDIO = "3"; //音频
    public final static String MESSAGE_TYPE_VIDEO = "4"; //视频
    public final static String MESSAGE_TYPE_FILE = "5"; //文件

    public final static int MEESAGE_TYPE_AUTO_REPLY = 1; //自动回复的消息类型

    /**
     * 是否是自动回复的消息
     *
     * @return
     */
    public boolean isAutoReplyMessage() {
        return msgType == 1;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * 是否来自发送的消息
     *
     * @return
     */
    public boolean isFromSend() {
        return "1".equals(from);
    }

    /**
     * 是否是文本
     *
     * @return
     */
    public boolean isText() {
        return MESSAGE_TYPE_TEXT.equals(category);
    }

    ChatMessage audioMessage;


    public boolean isAudioPlay() {
        return isAudioPlay;
    }

    public void setAudioPlay(boolean audioPlay) {
        isAudioPlay = audioPlay;
    }

    public static boolean isText(String category) {
        return MESSAGE_TYPE_TEXT.equals(category);
    }

    /**
     * 是否是音频
     *
     * @return
     */
    public boolean isAudio() {
        return MESSAGE_TYPE_AUDIO.equals(category);
    }

    public static boolean isAudio(String category) {
        return MESSAGE_TYPE_AUDIO.equals(category);
    }

    /**
     * 是否是图片
     *
     * @return
     */
    public boolean isImage() {
        return MESSAGE_TYPE_IMAGE.equals(category);
    }

    public static boolean isImage(String category) {
        return MESSAGE_TYPE_IMAGE.equals(category);
    }

    public ChatMessage() {
    }

    public ChatMessage(String id, String uid, String service_uid, String from, String category, String content, String createtime, String head_portrait, String nickname) {
        this.id = id;
        this.uid = uid;
        this.service_uid = service_uid;
        this.from = from;
        this.category = category;
        this.content = content;
        this.create_time = createtime;
        this.head_portrait = head_portrait;
        this.nickname = nickname;
    }

    public ChatMessage(Parcel in) {
        id = in.readString();
        uid = in.readString();
        service_uid = in.readString();
        from = in.readString();
        category = in.readString();
        content = in.readString();
        create_time = in.readString();
        head_portrait = in.readString();
        nickname = in.readString();
        device = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(service_uid);
        dest.writeString(from);
        dest.writeString(category);
        dest.writeString(content);
        dest.writeString(create_time);
        dest.writeString(head_portrait);
        dest.writeString(nickname);
        dest.writeString(device);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };


    public AutoReplyContentBean getAutoReplyMessage() {
        return autoReplyMessage;
    }

    public void setAutoReplyMessage(AutoReplyContentBean autoReplyMessage) {
        this.autoReplyMessage = autoReplyMessage;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHead_portrait() {
        return head_portrait;
    }

    public void setHead_portrait(String head_portrait) {
        this.head_portrait = head_portrait;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService_uid() {
        return service_uid;
    }

    public void setService_uid(String service_uid) {
        this.service_uid = service_uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public CharSequence getSpannable() {
        return spannable;
    }

    public void setSpannable(CharSequence spannable) {
        this.spannable = spannable;
    }

    public ChatMessage getAudioMessage() {
        return audioMessage;
    }

    public void setAudioMessage(ChatMessage audio) {
        this.audioMessage = audio;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getVoice_time() {
        return voice_time;
    }

    public void setVoice_time(String voice_time) {
        this.voice_time = voice_time;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isPageTop() {
        return isPageTop;
    }

    public void setPageTop(boolean pageTop) {
        isPageTop = pageTop;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                "cmd='" + cmd + '\'' +
                ", uid='" + uid + '\'' +
                ", service_uid='" + service_uid + '\'' +
                ", from='" + from + '\'' +
                ", category='" + category + '\'' +
                ", content='" + content + '\'' +
                ", create_time='" + create_time + '\'' +
                ", head_portrait='" + head_portrait + '\'' +
                ", voice_time='" + voice_time + '\'' +
                ", status=" + status +
                ", spannable=" + spannable +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", device=" + device +
                '}';
    }
}
