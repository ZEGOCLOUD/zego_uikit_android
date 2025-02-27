package com.zegocloud.uikit.plugin;

import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomCommandMessage;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomTextMessage;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginConnectionState;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginConnectionStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginInRoomCommandMessageListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginInRoomTextMessageListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginInvitationListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginRoomPropertyUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserInRoomAttributesInfo;
import com.zegocloud.uikit.service.internal.NotifyList;
import java.util.HashMap;
import java.util.List;

class UIKitSignalingService {

    private NotifyList<ZegoUIKitSignalingPluginInvitationListener> invitationListenerList = new NotifyList<>();
    private NotifyList<ZegoUIKitSignalingPluginRoomPropertyUpdateListener> roomPropertyUpdateListenerList = new NotifyList<>();
    private NotifyList<ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener> usersInRoomAttributesUpdateListenerList = new NotifyList<>();
    private NotifyList<ZegoUIKitSignalingPluginInRoomTextMessageListener> inRoomTextMessageListenerNotifyList = new NotifyList<>();
    private NotifyList<ZegoUIKitSignalingPluginInRoomCommandMessageListener> inRoomCommandMessageListenerNotifyList = new NotifyList<>();
    private NotifyList<ZegoUIKitSignalingPluginConnectionStateChangeListener> connectionStateChangeListenerNotifyList = new NotifyList<>();

    public void notifyCallInvitationReceived(ZegoUIKitUser inviter, int type, String data) {
        invitationListenerList.notifyAllListener(invitationListener -> {
            invitationListener.onInvitationReceived(inviter, type, data);
        });
    }

    public void notifyCallInvitationCancelled(ZegoUIKitUser inviter, String data) {
        invitationListenerList.notifyAllListener(invitationListener -> {
            invitationListener.onInvitationCanceled(inviter, data);
        });
    }

    public void notifyCallInvitationAccepted(ZegoUIKitUser invitee, String data) {
        invitationListenerList.notifyAllListener(invitationListener -> {
            invitationListener.onInvitationAccepted(invitee, data);
        });
    }

    public void notifyCallInvitationRejected(ZegoUIKitUser invitee, String data) {
        invitationListenerList.notifyAllListener(invitationListener -> {
            invitationListener.onInvitationRefused(invitee, data);
        });
    }

    public void notifyCallInvitationTimeout(ZegoUIKitUser inviter, String data) {
        invitationListenerList.notifyAllListener(invitationListener -> {
            invitationListener.onInvitationTimeout(inviter, data);
        });
    }

    public void notifyCallInviteesAnsweredTimeout(List<ZegoUIKitUser> users, String data) {
        invitationListenerList.notifyAllListener(invitationListener -> {
            invitationListener.onInvitationResponseTimeout(users, data);
        });
    }

    public void notifyRoomPropertyUpdated(String key, String oldValue, String newValue) {
        roomPropertyUpdateListenerList.notifyAllListener(listener -> {
            listener.onRoomPropertyUpdated(key, oldValue, newValue);
        });
    }

    public void notifyRoomPropertyFullUpdated(List<String> updateKeys, HashMap<String, String> oldRoomAttributes,
        HashMap<String, String> roomAttributes) {
        roomPropertyUpdateListenerList.notifyAllListener(listener -> {
            listener.onRoomPropertiesFullUpdated(updateKeys, oldRoomAttributes, roomAttributes);
        });
    }

    public void notifyUsersInRoomAttributesUpdated(List<String> updateKeys,
        List<ZegoUserInRoomAttributesInfo> oldAttributes, List<ZegoUserInRoomAttributesInfo> attributes,
        ZegoUIKitUser editor) {
        usersInRoomAttributesUpdateListenerList.notifyAllListener(listener -> {
            listener.onUsersInRoomAttributesUpdated(updateKeys, oldAttributes, attributes, editor);
        });
    }

    public void addRoomPropertyUpdateListener(ZegoUIKitSignalingPluginRoomPropertyUpdateListener listener) {
        roomPropertyUpdateListenerList.addListener(listener);
    }

    public void removeRoomPropertyUpdateListener(ZegoUIKitSignalingPluginRoomPropertyUpdateListener listener) {
        roomPropertyUpdateListenerList.removeListener(listener);
    }

    public void addUsersInRoomAttributesUpdateListener(
        ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener listener) {
        usersInRoomAttributesUpdateListenerList.addListener(listener);
    }

    public void removeUsersInRoomAttributesUpdateListener(
        ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener listener) {
        usersInRoomAttributesUpdateListenerList.removeListener(listener);
    }

    public void addInvitationListener(ZegoUIKitSignalingPluginInvitationListener listener) {
        invitationListenerList.addListener(listener, false);
    }

    public void removeInvitationListener(ZegoUIKitSignalingPluginInvitationListener listener) {
        invitationListenerList.removeListener(listener, false);
    }

    public void clear() {
        invitationListenerList.clear();
        removeRoomListeners();
    }

    public void removeRoomListeners() {
        usersInRoomAttributesUpdateListenerList.clear();
        roomPropertyUpdateListenerList.clear();
        inRoomTextMessageListenerNotifyList.clear();
        connectionStateChangeListenerNotifyList.clear();
        inRoomCommandMessageListenerNotifyList.clear();
    }

    void addInRoomTextMessageListener(ZegoUIKitSignalingPluginInRoomTextMessageListener listener) {
        inRoomTextMessageListenerNotifyList.addListener(listener, false);
    }

    void removeInRoomTextMessageListener(ZegoUIKitSignalingPluginInRoomTextMessageListener listener) {
        inRoomTextMessageListenerNotifyList.removeListener(listener, false);
    }

    void addInRoomCommandMessageListener(ZegoUIKitSignalingPluginInRoomCommandMessageListener listener) {
        inRoomCommandMessageListenerNotifyList.addListener(listener, false);
    }

    void removeInRoomCommandMessageListener(ZegoUIKitSignalingPluginInRoomCommandMessageListener listener) {
        inRoomCommandMessageListenerNotifyList.removeListener(listener, false);
    }

    public void notifyInRoomTextMessageReceived(List<ZegoSignalingInRoomTextMessage> messages, String roomID) {
        inRoomTextMessageListenerNotifyList.notifyAllListener(listener -> {
            listener.onInRoomTextMessageReceived(messages, roomID);
        });
    }

    public void addConnectionStateChangeListener(ZegoUIKitSignalingPluginConnectionStateChangeListener listener) {
        connectionStateChangeListenerNotifyList.addListener(listener, false);
    }

    public void removeConnectionStateChangeListener(ZegoUIKitSignalingPluginConnectionStateChangeListener listener) {
        connectionStateChangeListenerNotifyList.removeListener(listener, false);
    }

    public void notifyConnectionStateChange(ZegoSignalingPluginConnectionState connectionState) {
        connectionStateChangeListenerNotifyList.notifyAllListener(listener -> {
            listener.onConnectionStateChanged(connectionState);
        });
    }

    public void onInRoomCommandMessageReceived(List<ZegoSignalingInRoomCommandMessage> messages, String roomID) {
        inRoomCommandMessageListenerNotifyList.notifyAllListener(listener -> {
            listener.onInRoomCommandMessageReceived(messages, roomID);
        });
    }
}
