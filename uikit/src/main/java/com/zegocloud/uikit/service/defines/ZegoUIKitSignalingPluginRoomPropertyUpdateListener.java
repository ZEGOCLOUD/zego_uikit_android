package com.zegocloud.uikit.service.defines;

import java.util.HashMap;
import java.util.List;

public interface ZegoUIKitSignalingPluginRoomPropertyUpdateListener {

    void onRoomPropertyUpdated(String key,String oldValue, String newValue);

    void onRoomPropertiesFullUpdated(List<String> updateKeys, HashMap<String, String> oldProperties, HashMap<String, String> properties);

}
