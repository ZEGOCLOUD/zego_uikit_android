package com.zegocloud.uikit.service.defines;

import com.zegocloud.uikit.components.message.ZegoInRoomMessageState;

public class ZegoInRoomMessage {

    public String message;
    public long messageID;
    public long timestamp;
    public ZegoUIKitUser user;
    public ZegoInRoomMessageState state = ZegoInRoomMessageState.SUCCESS;
}
