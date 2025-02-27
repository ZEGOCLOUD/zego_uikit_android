package com.zegocloud.uikit.plugin.common;

import android.app.Application;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.SendRoomMessageCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginNotificationConfig;
import com.zegocloud.uikit.service.defines.ZegoSetUsersInRoomAttributesCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitPluginCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginConnectionStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginInRoomCommandMessageListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginInRoomTextMessageListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginInvitationListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginRoomAttributesOperatedCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginRoomPropertyOperatedCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginRoomPropertyQueriedCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginRoomPropertyUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUsersInRoomAttributesQueriedCallback;
import com.zegocloud.uikit.service.defines.ZegoUsersInRoomAttributesQueryConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * interfaces for prebuilt to invoke
 */
public interface IZegoUIKitSignalingPlugin {

    void init(Application application, Long appID, String appSign);

    boolean isPluginInitSucceed();

    boolean isPluginExited();

    void onActivityStarted();

    void login(String userID, String userName, ZegoUIKitPluginCallback callback);

    void logout();

    void joinRoom(String roomID, ZegoUIKitPluginCallback callback);

    void leaveRoom(ZegoUIKitPluginCallback callback);

    void destroy();

    void renewToken(String token);

    void sendInvitation(List<String> invitees, int timeout, int type, String data,
        PluginCallbackListener callbackListener);

    void sendInvitation(List<String> invitees, int timeout, int type, String data,
        ZegoSignalingPluginNotificationConfig notificationConfig, PluginCallbackListener callbackListener);

    void cancelInvitation(List<String> invitees, String data, PluginCallbackListener callbackListener);

    void cancelInvitation(List<String> invitees, String data, ZegoSignalingPluginNotificationConfig pushConfig,
        PluginCallbackListener callbackListener);

    void callCancel(List<String> invitees, String invitationID, String extendedData,
        ZegoSignalingPluginNotificationConfig pushConfig, PluginCallbackListener callbackListener);

    void refuseInvitation(String inviterID, String data, PluginCallbackListener callbackListener);

    void callReject(String invitationID, String extendedData, PluginCallbackListener callbackListener);

    void acceptInvitation(String inviterID, String data, PluginCallbackListener callbackListener);

    void callAccept(String invitationID, String extendedData, PluginCallbackListener callbackListener);

    void addInvitationListener(ZegoUIKitSignalingPluginInvitationListener listener);

    void removeInvitationListener(ZegoUIKitSignalingPluginInvitationListener listener);

    HashMap<String, String> getRoomProperties();

    void setUsersInRoomAttributes(String key, String value, List<String> userIDs,
        ZegoSetUsersInRoomAttributesCallback callback);

    void queryUsersInRoomAttributes(ZegoUsersInRoomAttributesQueryConfig config,
        ZegoUsersInRoomAttributesQueriedCallback callback);

    void updateRoomProperty(String key, String value, boolean isDeleteAfterOwnerLeft, boolean isForce,
        boolean isUpdateOwner, ZegoUIKitSignalingPluginRoomAttributesOperatedCallback callback);

    void updateRoomProperty(Map<String, String> map, boolean isDeleteAfterOwnerLeft, boolean isForce,
        boolean isUpdateOwner, ZegoUIKitSignalingPluginRoomAttributesOperatedCallback callback);

    void deleteRoomProperties(List<String> keys, boolean isForce,
        ZegoUIKitSignalingPluginRoomAttributesOperatedCallback callback);

    void beginRoomPropertiesBatchOperation(boolean isDeleteAfterOwnerLeft, boolean isForce, boolean isUpdateOwner);

    void endRoomPropertiesBatchOperation(ZegoUIKitSignalingPluginRoomPropertyOperatedCallback callback);

    void queryRoomProperties(ZegoUIKitSignalingPluginRoomPropertyQueriedCallback callback);

    void addRoomPropertyUpdateListener(ZegoUIKitSignalingPluginRoomPropertyUpdateListener roomPropertyUpdateListener);

    void removeRoomPropertyUpdateListener(
        ZegoUIKitSignalingPluginRoomPropertyUpdateListener roomPropertyUpdateListener);

    void addUsersInRoomAttributesUpdateListener(
        ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener usersInRoomAttributesUpdateListener);

    void removeUsersInRoomAttributesUpdateListener(
        ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener usersInRoomAttributesUpdateListener);


    void addInRoomTextMessageListener(ZegoUIKitSignalingPluginInRoomTextMessageListener listener);

    void removeInRoomTextMessageListener(ZegoUIKitSignalingPluginInRoomTextMessageListener listener);

    public void sendInRoomCommandMessage(String command, String roomID, SendRoomMessageCallback callback);

    void addInRoomCommandMessageListener(ZegoUIKitSignalingPluginInRoomCommandMessageListener listener);

    void removeInRoomCommandMessageListener(ZegoUIKitSignalingPluginInRoomCommandMessageListener listener);

    void enableNotifyWhenAppRunningInBackgroundOrQuit(boolean enable);

    void addConnectionStateChangeListener(ZegoUIKitSignalingPluginConnectionStateChangeListener listener);

    void removeConnectionStateChangeListener(ZegoUIKitSignalingPluginConnectionStateChangeListener listener);

    void enableFCMPush();

    boolean isFCMPushEnable();

    void disableFCMPush();

    void enableHWPush(String hwAppID);

    void enableMiPush(String miAppID, String miAppKey);

    void enableVivoPush(String vivoAppID, String vivoAppKey);

    void enableOppoPush(String oppoAppID, String oppoAppKey, String oppoAppSecret);

    boolean isOtherPushEnable();

    void registerPush();

    void unregisterPush();

    void setAppType(int appType);

    String getVersion();
}
