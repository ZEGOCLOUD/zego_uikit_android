package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoUIKitSignalingPluginRoomAttributesOperatedCallback {

    void onSignalingPluginRoomAttributesOperated(int errorCode, String errorMessage, List<String> errorKeys);

}
