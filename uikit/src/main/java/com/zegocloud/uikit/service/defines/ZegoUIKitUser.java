package com.zegocloud.uikit.service.defines;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Objects;

public class ZegoUIKitUser implements Parcelable {

    public String userID;
    public String userName;
    public boolean isCameraOn;
    public boolean isMicrophoneOn;
    public String avatar;
    public HashMap<String, String> inRoomAttributes;

    public ZegoUIKitUser(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public ZegoUIKitUser(String userID) {
        this.userID = userID;
        this.userName = "";
    }

    protected ZegoUIKitUser(Parcel in) {
        userID = in.readString();
        userName = in.readString();
        isCameraOn = in.readByte() != 0;
        isMicrophoneOn = in.readByte() != 0;
    }

    public static final Creator<ZegoUIKitUser> CREATOR = new Creator<ZegoUIKitUser>() {
        @Override
        public ZegoUIKitUser createFromParcel(Parcel in) {
            return new ZegoUIKitUser(in);
        }

        @Override
        public ZegoUIKitUser[] newArray(int size) {
            return new ZegoUIKitUser[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZegoUIKitUser userInfo = (ZegoUIKitUser) o;
        return Objects.equals(userID, userInfo.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(userName);
        dest.writeByte((byte) (isCameraOn ? 1 : 0));
        dest.writeByte((byte) (isMicrophoneOn ? 1 : 0));
    }


    @Override
    public String toString() {
        return "ZegoUIKitUser{" +
            "userID='" + userID + '\'' +
            ", userName='" + userName + '\'' +
            ", isCameraOn=" + isCameraOn +
            ", isMicrophoneOn=" + isMicrophoneOn +
            ", avatar='" + avatar + '\'' +
            ", inRoomAttributes=" + inRoomAttributes +
            '}';
    }
}
