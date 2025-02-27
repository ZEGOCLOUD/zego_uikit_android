package com.zegocloud.uikit.components.notice;

import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

class InRoomNotification {

    static int type_JOIN = 0;
    static int type_LEAVE = 1;
    static int type_MESSAGE = 2;
    int type;
    ZegoUIKitUser uiKitUser;
    ZegoInRoomMessage inRoomMessage;

    public InRoomNotification(int type) {
        this.type = type;
    }

}
