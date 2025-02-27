package com.zegocloud.uikit.service.internal;

import android.text.TextUtils;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import im.zego.zegoexpress.entity.ZegoStream;
import java.util.HashMap;
import java.util.Objects;

public class UIKitCoreUser {

    public String userID;
    public String userName;
    public String mainStreamID;
    public String shareStreamID;
    public boolean isCameraOpen = false;
    public boolean isMicOpen = false;
    public float soundLevel;
    public HashMap<String, String> attributes;

    public UIKitCoreUser() {

    }

    public UIKitCoreUser(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public static UIKitCoreUser createFromStream(ZegoStream stream) {
        UIKitCoreUser uiKitUser = new UIKitCoreUser();
        uiKitUser.userID = stream.user.userID;
        uiKitUser.userName = stream.user.userName;
        uiKitUser.setStreamID(stream.streamID);
        return uiKitUser;
    }

    public ZegoUIKitUser getUIKitUser() {
        ZegoUIKitUser zegoUIKitUser = new ZegoUIKitUser(userID, userName);
        zegoUIKitUser.isCameraOn = isCameraOpen;
        zegoUIKitUser.isMicrophoneOn = isMicOpen;
        zegoUIKitUser.inRoomAttributes = attributes;
        if (attributes != null && attributes.containsKey("avatar")) {
            zegoUIKitUser.avatar = attributes.get("avatar");
        }
        return zegoUIKitUser;
    }

    public String getMainStreamID() {
        return mainStreamID;
    }

    public void setStreamID(String streamID) {
        if (streamID.contains("main")) {
            mainStreamID = streamID;
        } else {
            shareStreamID = streamID;
        }
    }

    public void deleteStream(String streamID) {
        if (streamID.contains("main")) {
            mainStreamID = null;
        } else {
            shareStreamID = null;
        }
    }

    public boolean hasStream() {
        return !TextUtils.isEmpty(mainStreamID) || !TextUtils.isEmpty(shareStreamID);
    }

    public boolean hasMainStream() {
        return !TextUtils.isEmpty(mainStreamID);
    }

    public String getShareStreamID() {
        return shareStreamID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UIKitCoreUser userInfo = (UIKitCoreUser) o;
        return Objects.equals(userID, userInfo.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}