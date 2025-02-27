package com.zegocloud.uikit.service.defines;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;

public class ZegoUserInRoomAttributesInfo implements Parcelable {

    private String userID;
    private HashMap<String, String> attributes;

    public ZegoUserInRoomAttributesInfo(String userID, HashMap<String, String> attributes) {
        this.userID = userID;
        this.attributes = attributes;
    }

    protected ZegoUserInRoomAttributesInfo(Parcel in) {
        userID = in.readString();
    }

    public static final Creator<ZegoUserInRoomAttributesInfo> CREATOR = new Creator<ZegoUserInRoomAttributesInfo>() {
        @Override
        public ZegoUserInRoomAttributesInfo createFromParcel(Parcel in) {
            return new ZegoUserInRoomAttributesInfo(in);
        }

        @Override
        public ZegoUserInRoomAttributesInfo[] newArray(int size) {
            return new ZegoUserInRoomAttributesInfo[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userID);
    }
}
