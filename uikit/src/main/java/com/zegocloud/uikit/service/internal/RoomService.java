package com.zegocloud.uikit.service.internal;

import android.text.TextUtils;
import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.service.defines.RoomStateChangedListener;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomCommandListener;
import com.zegocloud.uikit.service.defines.ZegoRoomPropertyUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoScreenSharingUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoSendInRoomCommandCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.express.ExpressEngineProxy;
import im.zego.uikit.libuikitreport.ReportUtil;
import im.zego.zegoexpress.callback.IZegoRoomLoginCallback;
import im.zego.zegoexpress.callback.IZegoRoomLogoutCallback;
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

class RoomService {

    private NotifyList<ZegoAudioVideoUpdateListener> mainStreamUpdateListeners = new NotifyList<>();
    private NotifyList<ZegoScreenSharingUpdateListener> shareStreamUpdateListeners = new NotifyList<>();
    private NotifyList<RoomStateChangedListener> roomStateChangedListeners = new NotifyList<>();
    private NotifyList<ZegoRoomPropertyUpdateListener> roomPropertyUpdateListeners = new NotifyList<>();
    private NotifyList<ZegoInRoomCommandListener> inRoomCommandListenerNotifyList = new NotifyList<>();

    public void addRoomStateUpdatedListener(RoomStateChangedListener listener, boolean weakRef) {
        roomStateChangedListeners.addListener(listener, weakRef);
    }

    public void removeRoomStateUpdatedListener(RoomStateChangedListener listener, boolean weakRef) {
        roomStateChangedListeners.removeListener(listener, weakRef);
    }

    public void addRoomPropertyUpdatedListener(ZegoRoomPropertyUpdateListener listener, boolean weakRef) {
        roomPropertyUpdateListeners.addListener(listener, weakRef);
    }

    public void removeRoomPropertyUpdatedListener(ZegoRoomPropertyUpdateListener listener, boolean weakRef) {
        roomPropertyUpdateListeners.removeListener(listener, weakRef);
    }

    public void addInRoomCommandListener(ZegoInRoomCommandListener listener, boolean weakRef) {
        inRoomCommandListenerNotifyList.addListener(listener, weakRef);
    }

    public void removeInRoomCommandListener(ZegoInRoomCommandListener listener, boolean weakRef) {
        inRoomCommandListenerNotifyList.removeListener(listener, weakRef);
    }

    public void addScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener, boolean weakRef) {
        shareStreamUpdateListeners.addListener(listener, weakRef);
    }

    public void removeScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener, boolean weakRef) {
        shareStreamUpdateListeners.removeListener(listener, weakRef);
    }

    public void clear() {
        clearOtherListeners();
        clearRoomStateListeners();
    }

    public void clearOtherListeners() {
        mainStreamUpdateListeners.clear();
        roomPropertyUpdateListeners.clear();
        inRoomCommandListenerNotifyList.clear();
        shareStreamUpdateListeners.clear();
    }

    public void clearRoomStateListeners() {
        roomStateChangedListeners.clear();
    }

    public void notifyStreamUpdate(String roomID, ZegoUpdateType zegoUpdateType, ArrayList<ZegoStream> streamList,
        JSONObject jsonObject) {

        List<ZegoStream> mainStreamList = GenericUtils.filter(streamList, stream -> stream.streamID.contains("main"));
        List<ZegoStream> shareStreamList = GenericUtils.filter(streamList, stream -> !stream.streamID.contains("main"));

        List<ZegoUIKitUser> mainUserList = GenericUtils.map(mainStreamList,
            stream -> new ZegoUIKitUser(stream.user.userID, stream.user.userName));
        List<ZegoUIKitUser> shareUserList = GenericUtils.map(shareStreamList,
            stream -> new ZegoUIKitUser(stream.user.userID, stream.user.userName));

        if (zegoUpdateType == ZegoUpdateType.ADD) {
            if (!mainUserList.isEmpty()) {
                mainStreamUpdateListeners.notifyAllListener(audioVideoUpdateListener -> {
                    audioVideoUpdateListener.onAudioVideoAvailable(mainUserList);
                });
            }

            if (!shareUserList.isEmpty()) {
                shareStreamUpdateListeners.notifyAllListener(screenSharingUpdateListener -> {
                    screenSharingUpdateListener.onScreenSharingAvailable(shareUserList);
                });
            }
        } else {
            if (!mainUserList.isEmpty()) {
                mainStreamUpdateListeners.notifyAllListener(audioVideoUpdateListener -> {
                    audioVideoUpdateListener.onAudioVideoUnAvailable(mainUserList);
                });
            }
            if (!shareUserList.isEmpty()) {
                shareStreamUpdateListeners.notifyAllListener(screenSharingUpdateListener -> {
                    screenSharingUpdateListener.onScreenSharingUnAvailable(shareUserList);
                });
            }
        }
    }

    public void notifyRoomStateUpdate(String roomID, ZegoRoomStateChangedReason reason, int errorCode,
        JSONObject jsonObject) {
        roomStateChangedListeners.notifyAllListener(roomStateChangedListener -> {
            roomStateChangedListener.onRoomStateChanged(roomID, reason, errorCode, jsonObject);
        });
    }

    public void addAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener, boolean weakRef) {
        mainStreamUpdateListeners.addListener(listener, weakRef);
    }

    public void removeAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener, boolean weakRef) {
        mainStreamUpdateListeners.removeListener(listener, weakRef);
    }

    public void joinRoom(String roomID, String token, ZegoUIKitCallback callback) {
        UIKitCoreUser localUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localUser != null) {
            ZegoUser user = new ZegoUser(localUser.userID, localUser.userName);
            ZegoRoomConfig config = new ZegoRoomConfig();
            // if you need limit participant count, you can change the max member count
            config.maxMemberCount = 0;
            config.isUserStatusNotify = true;
            if (!TextUtils.isEmpty(token)) {
                config.token = token;
            }
            long start_time = System.currentTimeMillis();
            UIKitCore.getInstance().getRoom().roomID = roomID;
            ExpressEngineProxy.loginRoom(roomID, user, config, new IZegoRoomLoginCallback() {
                @Override
                public void onRoomLoginResult(int errorCode, JSONObject jsonObject) {

                    HashMap<String, Object> commonParams = new HashMap<>();
                    commonParams.put("room_id", roomID);
                    commonParams.put("error", errorCode);
                    commonParams.put("msg", jsonObject.toString());
                    commonParams.put("start_time", start_time);
                    ReportUtil.reportEvent("loginRoom", commonParams);

                    ExpressEngineProxy.startSoundLevelMonitor();
                    if (callback != null) {
                        callback.onResult(errorCode);
                    }
                }
            });
        }
    }

    public void leaveRoom(IZegoRoomLogoutCallback callback) {
        ExpressEngineProxy.logoutRoom(new IZegoRoomLogoutCallback() {
            @Override
            public void onRoomLogoutResult(int errorCode, JSONObject extendedData) {

                String roomID = UIKitCore.getInstance().getRoom().roomID;
                HashMap<String, Object> commonParams = new HashMap<>();
                commonParams.put("room_id", roomID);
                commonParams.put("error", errorCode);
                commonParams.put("msg", extendedData.toString());
                ReportUtil.reportEvent("logoutRoom", commonParams);

                if (callback != null) {
                    callback.onRoomLogoutResult(errorCode, extendedData);
                }
            }
        });
    }

    public void setRoomProperty(String roomID, String key, String value, ZegoUIKitCallback callback) {
        ExpressEngineProxy.setRoomExtraInfo(roomID, key, value, errorCode -> {
            if (callback != null) {
                callback.onResult(errorCode);
            }
        });
    }

    public void notifyRoomPropertyUpdate(String key, String oldValue, String value) {
        roomPropertyUpdateListeners.notifyAllListener(roomPropertyUpdateListener -> {
            roomPropertyUpdateListener.onRoomPropertyUpdated(key, oldValue, value);
        });
    }

    public void notifyRoomPropertiesFullUpdated(List<String> keys, Map<String, String> oldProperties,
        Map<String, String> roomProperties) {
        roomPropertyUpdateListeners.notifyAllListener(roomPropertyUpdateListener -> {
            roomPropertyUpdateListener.onRoomPropertiesFullUpdated(keys, oldProperties, roomProperties);
        });
    }

    public void sendInRoomCommand(String roomID, String command, ArrayList<String> toUserList,
        ZegoSendInRoomCommandCallback callback) {

        ArrayList<ZegoUser> zegoUserList = new ArrayList<>(toUserList.size());
        for (String userID : toUserList) {
            ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(userID);
            if (uiKitUser != null) {
                zegoUserList.add(new ZegoUser(uiKitUser.userID, uiKitUser.userName));
            }
        }
        ExpressEngineProxy.sendCustomCommand(roomID, command, zegoUserList, errorCode -> {
            if (callback != null) {
                callback.onResult(errorCode);
            }
        });
    }

    public void notifyIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
        inRoomCommandListenerNotifyList.notifyAllListener(zegoInRoomCommandListener -> {
            ZegoUIKitUser uiKitUser = new ZegoUIKitUser(fromUser.userID, fromUser.userName);
            zegoInRoomCommandListener.onInRoomCommandReceived(uiKitUser, command);
        });
    }
}
