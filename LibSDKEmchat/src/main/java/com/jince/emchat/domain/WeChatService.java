package com.jince.emchat.domain;

@Deprecated
public class WeChatService implements ChatService{
    private String qy_id;
    private String wx_id;
    private String agent_id;
    private String agent_name;
    private String service_type;
    private String service_uid;
    private String head_portrait;


    public String getQy_id() {
        return qy_id;
    }

    public void setQy_id(String qy_id) {
        this.qy_id = qy_id;
    }

    public String getWx_id() {
        return wx_id;
    }

    public void setWx_id(String wx_id) {
        this.wx_id = wx_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getAgent_name() {
        return agent_name;
    }

    public void setAgent_name(String agent_name) {
        this.agent_name = agent_name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getService_uid() {
        return service_uid;
    }

    public void setService_uid(String service_uid) {
        this.service_uid = service_uid;
    }

    public String getHead_portrait() {
        return head_portrait;
    }

    public void setHead_portrait(String head_portrait) {
        this.head_portrait = head_portrait;
    }

    @Override
    public String toString() {
        return "WeChatService{" +
                "qy_id='" + qy_id + '\'' +
                ", wx_id='" + wx_id + '\'' +
                ", agent_id='" + agent_id + '\'' +
                ", agent_name='" + agent_name + '\'' +
                ", service_type='" + service_type + '\'' +
                ", service_uid='" + service_uid + '\'' +
                ", head_portrait='" + head_portrait + '\'' +
                '}';
    }

    @Override
    public String getChatServiceName() {
        return agent_name;
    }

    @Override
    public String getChatServiceId() {
        return service_uid;
    }

    @Override
    public String getChatServiceAvatarUrl() {
        return head_portrait;
    }

    @Override
    public int getChatServiceType() {
        return 0;
    }
}
