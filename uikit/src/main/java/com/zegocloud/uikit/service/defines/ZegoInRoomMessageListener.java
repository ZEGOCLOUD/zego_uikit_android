package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoInRoomMessageListener {

    void onInRoomMessageReceived(List<ZegoInRoomMessage> messageList);

    void onInRoomMessageSendingStateChanged(ZegoInRoomMessage inRoomMessage);
}
