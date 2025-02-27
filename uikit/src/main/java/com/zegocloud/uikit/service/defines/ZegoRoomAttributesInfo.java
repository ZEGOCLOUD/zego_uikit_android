package com.zegocloud.uikit.service.defines;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class ZegoRoomAttributesInfo implements Parcelable {

    private boolean isSet;
    private HashMap<String, String> attributes;

    public ZegoRoomAttributesInfo(boolean isSet, HashMap<String, String> attributes) {
        this.isSet = isSet;
        this.attributes = attributes;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    protected ZegoRoomAttributesInfo(Parcel in) {
        isSet = in.readByte() != 0;
    }

    public static final Creator<ZegoRoomAttributesInfo> CREATOR = new Creator<ZegoRoomAttributesInfo>() {
        @Override
        public ZegoRoomAttributesInfo createFromParcel(Parcel in) {
            return new ZegoRoomAttributesInfo(in);
        }

        @Override
        public ZegoRoomAttributesInfo[] newArray(int size) {
            return new ZegoRoomAttributesInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (isSet ? 1 : 0));
    }
}
