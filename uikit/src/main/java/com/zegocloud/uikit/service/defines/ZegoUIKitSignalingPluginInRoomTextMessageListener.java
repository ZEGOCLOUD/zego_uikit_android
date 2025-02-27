package com.zegocloud.uikit.service.defines;

import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomTextMessage;
import java.util.List;

public interface ZegoUIKitSignalingPluginInRoomTextMessageListener {

    void onInRoomTextMessageReceived(List<ZegoSignalingInRoomTextMessage> messages, String roomID);
}
