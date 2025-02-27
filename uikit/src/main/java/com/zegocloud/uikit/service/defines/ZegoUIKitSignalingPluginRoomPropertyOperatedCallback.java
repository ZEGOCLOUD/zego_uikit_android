package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoUIKitSignalingPluginRoomPropertyOperatedCallback {

    void onSignalingPluginRoomPropertyOperated(int errorCode, String errorMessage, List<String> errorKeys);

}
