package com.zegocloud.uikit.service.defines;

import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomCommandMessage;
import java.util.List;

public interface ZegoUIKitSignalingPluginInRoomCommandMessageListener {

    void onInRoomCommandMessageReceived(List<ZegoSignalingInRoomCommandMessage> messages, String roomID);
}
