package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoUserUpdateListener {

    void onUserJoined(List<ZegoUIKitUser> userInfoList);

    void onUserLeft(List<ZegoUIKitUser> userInfoList);
}
