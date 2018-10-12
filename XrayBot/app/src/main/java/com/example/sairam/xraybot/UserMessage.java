package com.example.sairam.xraybot;

public class UserMessage {
    public String msg;
    public boolean isMe;

    public UserMessage(String msg, boolean isMe){
        this.msg = msg;
        this.isMe = isMe;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
