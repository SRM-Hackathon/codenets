package com.example.sairam.xraybot;

import android.os.Parcel;
import android.os.Parcelable;


public class UserMessage implements Parcelable {
    public String msg;
    public boolean isMe;

    private int mData;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mData);
    }

    public static final Parcelable.Creator<UserMessage> CREATOR
            = new Parcelable.Creator<UserMessage>() {
        public UserMessage createFromParcel(Parcel in) {
            return new UserMessage(in);
        }

        public UserMessage[] newArray(int size) {
            return new UserMessage[size];
        }
    };

    @Override
    public String toString() {
        return msg + isMe;
    }

    /** recreate object from parcel */
    private UserMessage(Parcel in) {
        mData = in.readInt();
    }
}
