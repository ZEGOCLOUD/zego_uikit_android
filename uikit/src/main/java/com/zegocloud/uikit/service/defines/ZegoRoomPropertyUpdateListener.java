package com.zegocloud.uikit.service.defines;

import java.util.List;
import java.util.Map;

public interface ZegoRoomPropertyUpdateListener {

    void onRoomPropertyUpdated(String key,String oldValue, String newValue);

    void onRoomPropertiesFullUpdated(List<String> updateKeys, Map<String, String> oldProperties,Map<String, String> properties);
}
