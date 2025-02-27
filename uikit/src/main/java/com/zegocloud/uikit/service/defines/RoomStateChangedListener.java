package com.zegocloud.uikit.service.defines;

import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import org.json.JSONObject;

public interface RoomStateChangedListener {

    void onRoomStateChanged(String roomID, ZegoRoomStateChangedReason reason, int errorCode,
        JSONObject jsonObject);
}
