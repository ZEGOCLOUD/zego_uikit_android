package com.zegocloud.uikit.service.defines;

import java.util.HashMap;

public interface ZegoUIKitSignalingPluginRoomPropertyQueriedCallback {

    void onSignalingPluginRoomPropertyQueried(HashMap<String, String> attributes, int errorCode, String errorMessage);

}
