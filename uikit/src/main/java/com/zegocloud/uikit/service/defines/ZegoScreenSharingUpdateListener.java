package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoScreenSharingUpdateListener {

    void onScreenSharingAvailable(List<ZegoUIKitUser> userList);

    void onScreenSharingUnAvailable(List<ZegoUIKitUser> userList);
}
