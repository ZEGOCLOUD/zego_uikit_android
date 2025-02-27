package com.zegocloud.uikit.service.internal.interfaces;

import com.zegocloud.uikit.service.defines.RoomStateChangedListener;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomCommandListener;
import com.zegocloud.uikit.service.defines.ZegoRoomPropertyUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoScreenSharingUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoSendInRoomCommandCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitRoom;
import java.util.ArrayList;
import java.util.Map;

public interface IRoomService {

    void joinRoom(String roomID, ZegoUIKitCallback callback);

    void joinRoom(String roomID, boolean markAsLargeRoom, ZegoUIKitCallback callback);

    void leaveRoom();

    ZegoUIKitRoom getRoom();

    void setRoomProperty(String key, String value);

    void updateRoomProperties(Map<String, String> map);

    Map<String, String> getRoomProperties();

    void addRoomPropertyUpdateListener(ZegoRoomPropertyUpdateListener listener);

    void removeRoomPropertyUpdateListener(ZegoRoomPropertyUpdateListener listener);

    void addAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener);

    void removeAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener);

    void addScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener);

    void removeScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener);

    void sendInRoomCommand(String command, ArrayList<String> toUserList, ZegoSendInRoomCommandCallback callback);

    void addInRoomCommandListener(ZegoInRoomCommandListener listener);

    void removeInRoomCommandListener(ZegoInRoomCommandListener listener);

    void addRoomStateUpdatedListener(RoomStateChangedListener listener);

    void removeRoomStateUpdatedListener(RoomStateChangedListener listener);
}
