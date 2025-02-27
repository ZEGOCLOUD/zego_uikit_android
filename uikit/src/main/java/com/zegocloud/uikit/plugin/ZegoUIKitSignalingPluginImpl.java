package com.zegocloud.uikit.plugin;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.plugin.adapter.ZegoPluginAdapter;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.CancelInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ConnectUserCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.EndRoomBatchOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.PluginZIMUser;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.QueryRoomPropertyCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.QueryUsersInRoomAttributesCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ResponseInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RoomCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RoomPropertyOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.SendRoomMessageCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomCommandMessage;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomTextMessage;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginConnectionState;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginEventHandler;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginNotificationConfig;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginProtocol;
import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.plugin.common.IZegoUIKitSignalingPlugin;
import com.zegocloud.uikit.plugin.common.PluginCallbackListener;
import com.zegocloud.uikit.plugin.internal.InvitationData;
import com.zegocloud.uikit.plugin.internal.InvitationState;
import com.zegocloud.uikit.plugin.internal.InvitationUser;
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
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserInRoomAttributesInfo;
import com.zegocloud.uikit.service.defines.ZegoUsersInRoomAttributesQueriedCallback;
import com.zegocloud.uikit.service.defines.ZegoUsersInRoomAttributesQueryConfig;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

public class ZegoUIKitSignalingPluginImpl implements IZegoUIKitSignalingPlugin, ZegoSignalingPluginEventHandler {

    private UIKitSignalingService signalingService = new UIKitSignalingService();
    private List<ZegoUserInRoomAttributesInfo> usersInRoomAttributes = new ArrayList<>();
    private List<ZegoUserInRoomAttributesInfo> oldUsersInRoomAttributes = new ArrayList<>();
    private HashMap<String, String> roomAttributes = new HashMap<>();
    private String currentRoomID;
    private ZegoSignalingPluginConnectionState signalConnectionState;
    private String token;

    /**
     * key: callID received by zim, there is no invitationID when start invite no return
     */
    private Map<String, InvitationData> invitationMap = new HashMap<>();

    private InvitationUser getInvitee(String invitationID, String userID) {
        InvitationData invitationData = getInvitationByInvitationID(invitationID);
        if (invitationData == null) {
            return null;
        }
        InvitationUser invitationUser = null;
        for (InvitationUser invitee : invitationData.invitees) {
            if (Objects.equals(invitee.getUserID(), userID)) {
                invitationUser = invitee;
                break;
            }
        }
        return invitationUser;
    }

    void addInvitationData(InvitationData invitationData) {
        invitationMap.put(invitationData.id, invitationData);
    }

    InvitationData getInvitationByInvitationID(String invitationID) {
        return invitationMap.get(invitationID);
    }

    InvitationData removeInvitationData(String invitationID) {
        return invitationMap.remove(invitationID);
    }

    void removeIfAllChecked(String invitationID) {
        InvitationData invitationData = getInvitationByInvitationID(invitationID);
        if (invitationData == null) {
            return;
        }
        boolean allChecked = true;
        for (InvitationUser invitee : invitationData.invitees) {
            if (invitee.state == InvitationState.WAITING) {
                allChecked = false;
                break;
            }
        }
        if (allChecked) {
            removeInvitationData(invitationID);
        }
    }

    public void login(String userID, String userName, ZegoUIKitPluginCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            PluginZIMUser currentUser = signalingPlugin.getCurrentUser();
            if (currentUser != null && Objects.equals(currentUser.userID, userID)) {
                callback.onResult(0, "this userID already login,success return");
                return;
            }
            if (currentUser != null && !Objects.equals(currentUser.userID, userID)) {
                signalingPlugin.disconnectUser();
            }

            signalingPlugin.connectUser(userID, userName, token, new ConnectUserCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage) {
                    if (callback != null) {
                        callback.onResult(errorCode, errorMessage);
                    }
                }
            });
        }
    }

    /**
     * Enter the room
     *
     * @param roomID
     * @param callback
     */
    @Override
    public void joinRoom(String roomID, ZegoUIKitPluginCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.joinRoom(roomID, new RoomCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage) {
                    if (errorCode == 0) {
                        currentRoomID = roomID;
                    }
                    if (callback != null) {
                        callback.onResult(errorCode, errorMessage);
                    }
                }
            });
        }
    }

    @Override
    public void leaveRoom(ZegoUIKitPluginCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            if (!TextUtils.isEmpty(currentRoomID)) {
                signalingPlugin.leaveRoom(currentRoomID, new RoomCallback() {
                    @Override
                    public void onResult(int errorCode, String errorMessage) {
                        removeRoomListenersAndData();
                        if (callback != null) {
                            callback.onResult(errorCode, errorMessage);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void destroy() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.destroy();
        }
    }

    @Override
    public void renewToken(String token) {
        if (!Objects.equals(token, this.token)) {
            ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
            if (signalingPlugin != null) {
                signalingPlugin.renewToken(token);
            }
        }
        this.token = token;
    }

    private void removeRoomListenersAndData() {
        signalingService.removeRoomListeners();
        roomAttributes.clear();
        usersInRoomAttributes.clear();
    }

    /**
     * Set the user's room properties
     *
     * @param key
     * @param value
     * @param userIDs
     * @param callback
     */
    @Override
    public void setUsersInRoomAttributes(String key, String value, List<String> userIDs,
        ZegoSetUsersInRoomAttributesCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            HashMap<String, String> attributes = new HashMap<>();
            attributes.put(key, value);
            signalingPlugin.setUsersInRoomAttributes(attributes, userIDs, currentRoomID,
                (errorCode, errorMessage, errorUserList, attributesMap, errorKeysMap) -> {
                    if (errorCode == 0) {
                        oldUsersInRoomAttributes.clear();
                        oldUsersInRoomAttributes.addAll(usersInRoomAttributes);

                        List<ZegoUserInRoomAttributesInfo> infos = new ArrayList<>();
                        for (Entry<String, HashMap<String, String>> entry : attributesMap.entrySet()) {
                            infos.add(new ZegoUserInRoomAttributesInfo(entry.getKey(), entry.getValue()));
                        }
                        if (value.isEmpty()) {
                            for (ZegoUserInRoomAttributesInfo info : infos) {
                                removeUsersInRoomAttributesForKey(info, key);
                            }
                        } else {
                            for (ZegoUserInRoomAttributesInfo info : infos) {
                                updateUsersInRoomAttributesForKey(info, key, value);
                            }
                        }
                        UIKitCore.getInstance()
                            .dispatchRoomUserCountOrPropertyChanged(UIKitCore.getInstance().getAllUsers());
                    }
                    if (callback != null) {
                        callback.onSetUsersInRoomAttributes(errorCode, errorMessage);
                    }
                });
        }
    }

    /**
     * Delete User Properties
     *
     * @param info
     */
    private void removeUsersInRoomAttributesForKey(ZegoUserInRoomAttributesInfo info, String key) {
        for (int i = 0; i < usersInRoomAttributes.size(); i++) {
            ZegoUserInRoomAttributesInfo userInRoomAttributesInfo = usersInRoomAttributes.get(i);
            if (info.getUserID().equals(userInRoomAttributesInfo.getUserID())) {
                userInRoomAttributesInfo.getAttributes().remove(key);
                break;
            }
        }
    }

    private void updateUsersInRoomAttributesForKey(ZegoUserInRoomAttributesInfo info, String key, String value) {
        boolean needAddData = true;
        for (int i = 0; i < usersInRoomAttributes.size(); i++) {
            ZegoUserInRoomAttributesInfo userInRoomAttributesInfo = usersInRoomAttributes.get(i);
            if (info.getUserID().equals(userInRoomAttributesInfo.getUserID())) {
                needAddData = false;
                userInRoomAttributesInfo.getAttributes().put(key, value);
            }
        }

        if (needAddData) {
            usersInRoomAttributes.add(info);
        }
    }

    /**
     * Changing user attributes
     *
     * @param infos
     */
    private void insertOrUpdateUsersInRoomAttributes(List<ZegoUserInRoomAttributesInfo> infos) {
        if (infos == null || infos.size() == 0) {
            return;
        }

        if (usersInRoomAttributes == null || usersInRoomAttributes.size() == 0) {
            usersInRoomAttributes.addAll(infos);
            return;
        }

        for (int j = 0; j < infos.size(); j++) {
            ZegoUserInRoomAttributesInfo newUserInRoomAttributesInfo = infos.get(j);
            boolean needAddData = true;
            for (int i = 0; i < usersInRoomAttributes.size(); i++) {
                ZegoUserInRoomAttributesInfo oldUserInRoomAttributesInfo = usersInRoomAttributes.get(i);
                if (newUserInRoomAttributesInfo.getUserID().equals(oldUserInRoomAttributesInfo.getUserID())) {
                    usersInRoomAttributes.set(i, newUserInRoomAttributesInfo);
                    needAddData = false;
                }
            }

            if (needAddData) {
                usersInRoomAttributes.add(newUserInRoomAttributesInfo);
            }
        }
    }

    @Override
    public void queryUsersInRoomAttributes(ZegoUsersInRoomAttributesQueryConfig config,
        ZegoUsersInRoomAttributesQueriedCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.queryUsersInRoomAttributes(currentRoomID, config.getCount(), config.getNextFlag(),
                new QueryUsersInRoomAttributesCallback() {
                    @Override
                    public void onResult(int errorCode, String errorMessage, String nextFlag,
                        Map<String, HashMap<String, String>> attributesMap) {
                        List<ZegoUserInRoomAttributesInfo> infos = new ArrayList<>();
                        for (Entry<String, HashMap<String, String>> entry : attributesMap.entrySet()) {
                            infos.add(new ZegoUserInRoomAttributesInfo(entry.getKey(), entry.getValue()));
                        }
                        if (errorCode == 0) {
                            //                            insertOrUpdateUsersInRoomAttributes(infos);
                            updateCoreUserAndNotifyChanges(infos);
                            onUsersInRoomAttributesUpdated(attributesMap, "", currentRoomID);
                        }
                        if (callback != null) {
                            callback.onUsersInRoomAttributesQueried(infos, nextFlag, errorCode, errorMessage);

                        }
                    }
                });
        }
    }

    @Override
    public void updateRoomProperty(String key, String value, boolean isDeleteAfterOwnerLeft, boolean isForce,
        boolean isUpdateOwner, ZegoUIKitSignalingPluginRoomAttributesOperatedCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            HashMap<String, String> attributes = new HashMap<>();
            attributes.put(key, value);
            signalingPlugin.updateRoomProperty(attributes, currentRoomID, isForce, isDeleteAfterOwnerLeft,
                isUpdateOwner, new RoomPropertyOperationCallback() {
                    @Override
                    public void onResult(int errorCode, String errorMessage, List<String> errorKeys) {
                        if (callback != null) {
                            callback.onSignalingPluginRoomAttributesOperated(errorCode, errorMessage, errorKeys);
                        }
                    }
                });
        }
    }

    @Override
    public void updateRoomProperty(Map<String, String> map, boolean isDeleteAfterOwnerLeft, boolean isForce,
        boolean isUpdateOwner, ZegoUIKitSignalingPluginRoomAttributesOperatedCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            HashMap<String, String> attributes = new HashMap<>(map);
            signalingPlugin.updateRoomProperty(attributes, currentRoomID, isForce, isDeleteAfterOwnerLeft,
                isUpdateOwner, new RoomPropertyOperationCallback() {
                    @Override
                    public void onResult(int errorCode, String errorMessage, List<String> errorKeys) {
                        if (callback != null) {
                            callback.onSignalingPluginRoomAttributesOperated(errorCode, errorMessage, errorKeys);
                        }
                    }
                });
        }
    }

    @Override
    public void deleteRoomProperties(List<String> keys, boolean isForce,
        ZegoUIKitSignalingPluginRoomAttributesOperatedCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.deleteRoomProperties(keys, currentRoomID, isForce, new RoomPropertyOperationCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage, List<String> errorKeys) {
                    if (callback != null) {
                        callback.onSignalingPluginRoomAttributesOperated(errorCode, errorMessage, errorKeys);
                    }
                }
            });
        }
    }

    @Override
    public void beginRoomPropertiesBatchOperation(boolean isDeleteAfterOwnerLeft, boolean isForce,
        boolean isUpdateOwner) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.beginRoomPropertiesBatchOperation(currentRoomID, isDeleteAfterOwnerLeft, isForce,
                isUpdateOwner);
        }
    }

    @Override
    public void endRoomPropertiesBatchOperation(ZegoUIKitSignalingPluginRoomPropertyOperatedCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.endRoomPropertiesBatchOperation(currentRoomID, new EndRoomBatchOperationCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage) {
                    if (callback != null) {
                        callback.onSignalingPluginRoomPropertyOperated(errorCode, errorMessage, null);
                    }
                }
            });
        }
    }

    @Override
    public void queryRoomProperties(ZegoUIKitSignalingPluginRoomPropertyQueriedCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.queryRoomProperties(currentRoomID, new QueryRoomPropertyCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage, HashMap<String, String> properties) {
                    if (errorCode == 0) {
                        roomAttributes.clear();
                        roomAttributes.putAll(properties);
                    }
                    callback.onSignalingPluginRoomPropertyQueried(properties, errorCode, errorMessage);
                }
            });
        }
    }

    @Override
    public HashMap<String, String> getRoomProperties() {
        return roomAttributes;
    }

    public void updateCoreUserAndNotifyChanges(List<ZegoUserInRoomAttributesInfo> infos) {
        if (infos == null || infos.size() == 0) {
            return;
        }
        boolean shouldNotifyChange = false;
        for (int i = 0; i < infos.size(); i++) {
            ZegoUserInRoomAttributesInfo info = infos.get(i);
            UIKitCoreUser uiKitUser = UIKitCore.getInstance().getUserbyUserID(info.getUserID());
            if (uiKitUser != null) {
                HashMap<String, String> attributes = info.getAttributes();
                HashMap<String, String> userAttributes = uiKitUser.attributes;
                if (userAttributes == null) {
                    shouldNotifyChange = true;
                    uiKitUser.setAttributes(attributes);
                } else {
                    for (Map.Entry<String, String> infoEntry : attributes.entrySet()) {
                        boolean isUpdate = false;
                        String newKey = infoEntry.getKey();
                        String newValue = infoEntry.getValue();
                        for (Map.Entry<String, String> entry : uiKitUser.attributes.entrySet()) {
                            String oldKey = entry.getKey();
                            String oldValue = entry.getValue();
                            if (oldKey.equals(newKey)) {
                                isUpdate = true;
                                shouldNotifyChange = true;
                                uiKitUser.attributes.put(oldKey, newValue);
                            }
                        }
                        if (!isUpdate) {
                            shouldNotifyChange = true;
                            uiKitUser.attributes.put(newKey, newValue);
                        }
                    }
                }
            }
        }
        if (shouldNotifyChange) {
            UIKitCore.getInstance().dispatchRoomUserCountOrPropertyChanged(UIKitCore.getInstance().getAllUsers());
        }
    }

    private int getIndexFromUsersInRoomAttributes(String userID) {
        int index = -1;
        for (int i = 0; i < usersInRoomAttributes.size(); i++) {
            ZegoUserInRoomAttributesInfo info = usersInRoomAttributes.get(i);
            if (userID.equals(info.getUserID())) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public void addRoomPropertyUpdateListener(ZegoUIKitSignalingPluginRoomPropertyUpdateListener listener) {
        signalingService.addRoomPropertyUpdateListener(listener);
    }

    @Override
    public void removeRoomPropertyUpdateListener(ZegoUIKitSignalingPluginRoomPropertyUpdateListener listener) {
        signalingService.removeRoomPropertyUpdateListener(listener);
    }

    @Override
    public void addUsersInRoomAttributesUpdateListener(
        ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener listener) {
        signalingService.addUsersInRoomAttributesUpdateListener(listener);
    }

    @Override
    public void removeUsersInRoomAttributesUpdateListener(
        ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener listener) {
        signalingService.removeUsersInRoomAttributesUpdateListener(listener);
    }

    @Override
    public void addInRoomTextMessageListener(ZegoUIKitSignalingPluginInRoomTextMessageListener listener) {
        signalingService.addInRoomTextMessageListener(listener);
    }

    @Override
    public void removeInRoomTextMessageListener(ZegoUIKitSignalingPluginInRoomTextMessageListener listener) {
        signalingService.removeInRoomTextMessageListener(listener);
    }

    @Override
    public void addInRoomCommandMessageListener(ZegoUIKitSignalingPluginInRoomCommandMessageListener listener) {
        signalingService.addInRoomCommandMessageListener(listener);
    }

    @Override
    public void removeInRoomCommandMessageListener(ZegoUIKitSignalingPluginInRoomCommandMessageListener listener) {
        signalingService.removeInRoomCommandMessageListener(listener);
    }

    @Override
    public void addInvitationListener(ZegoUIKitSignalingPluginInvitationListener listener) {
        signalingService.addInvitationListener(listener);
    }

    @Override
    public void removeInvitationListener(ZegoUIKitSignalingPluginInvitationListener listener) {
        signalingService.removeInvitationListener(listener);
    }

    @Override
    public void init(Application application, Long appID, String appSign) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.init(application, appID, appSign);
            signalingPlugin.registerPluginEventHandler(this);
        }
    }

    @Override
    public boolean isPluginInitSucceed() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            return signalingPlugin.isZIMInitSuccess();
        } else {
            return false;
        }
    }

    @Override
    public void sendInvitation(List<String> invitees, int timeout, int type, String data,
        PluginCallbackListener callbackListener) {
        sendInvitation(invitees, timeout, type, data, null, callbackListener);
    }

    public void sendInRoomCommandMessage(String command, String roomID, SendRoomMessageCallback callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.sendRoomCommandMessage(command, roomID, new SendRoomMessageCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage) {
                    if (callback != null) {
                        callback.onResult(errorCode, errorMessage);
                    }
                }
            });
        }
    }

    @Override
    public void sendInvitation(List<String> invitees, int timeout, int type, String data,
        ZegoSignalingPluginNotificationConfig notificationConfig, PluginCallbackListener callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            PluginZIMUser currentUser = signalingPlugin.getCurrentUser();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", type);
                if (currentUser != null) {
                    jsonObject.put("inviter_id", currentUser.userID);
                    jsonObject.put("inviter_name", currentUser.userName);
                }
                jsonObject.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            signalingPlugin.sendInvitation(invitees, timeout, jsonObject.toString(), notificationConfig,
                (errorCode, errorMessage, invitationID, errorInvitees) -> {
                    if (errorCode == 0) {
                        ZegoUIKitUser inviter = new ZegoUIKitUser(currentUser.userID, currentUser.userName);
                        List<InvitationUser> invitationUsers = GenericUtils.map(invitees,
                            userID -> new InvitationUser(new ZegoUIKitUser(userID), InvitationState.WAITING));
                        InvitationData invitationData = new InvitationData(invitationID, inviter, invitationUsers,
                            type);
                        addInvitationData(invitationData);

                        List<ZegoUIKitUser> errorUsers = new ArrayList<>();
                        for (InvitationUser invitee : invitationData.invitees) {
                            if (errorInvitees.contains(invitee.getUserID())) {
                                invitee.state = InvitationState.ERROR;
                                errorUsers.add(invitee.user);
                            }
                        }
                        removeIfAllChecked(invitationID);

                        if (callback != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("code", errorCode);
                            map.put("message", errorMessage);
                            map.put("invitationID", invitationID);
                            map.put("errorInvitees", errorUsers);
                            callback.callback(map);
                        }
                    } else {
                        if (callback != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("code", errorCode);
                            map.put("message", errorMessage);
                            map.put("invitationID", invitationID);
                            callback.callback(map);
                        }
                    }
                });
        } else {
            if (callback != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", -2);
                map.put("message", "No SIGNALING plugin was added");
                callback.callback(map);
            }
        }
    }

    @Override
    public void cancelInvitation(List<String> invitees, String data, PluginCallbackListener callbackListener) {
        cancelInvitation(invitees, data, null, callbackListener);
    }

    @Override
    public void cancelInvitation(List<String> invitees, String data, ZegoSignalingPluginNotificationConfig pushConfig,
        PluginCallbackListener callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            String invitationID = null;
            JSONObject jsonObject = getJsonObjectFromString(data);
            if (jsonObject != null) {
                invitationID = getStringFromJson(jsonObject, "invitationID");
            }
            if (invitationID == null) {
                for (InvitationData invitationData : invitationMap.values()) {
                    if (invitees.isEmpty()) {
                        break;
                    }
                    List<String> inviteUserIDs = GenericUtils.map(invitationData.invitees,
                        invitationUser -> invitationUser.getUserID());
                    for (String invitee : invitees) {
                        if (inviteUserIDs.contains(invitee)) {
                            invitationID = invitationData.id;
                            break;
                        }
                    }
                    if (invitationID != null) {
                        break;
                    }
                }
            }
            if (invitationID == null) {
                if (callback != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", 0);
                    callback.callback(map);
                }
                return;
            }
            String finalInvitationID = invitationID;
            callCancel(invitees, finalInvitationID, data, pushConfig, callback);
        }
    }

    @Override
    public void callCancel(List<String> invitees, String invitationID, String extendedData,
        ZegoSignalingPluginNotificationConfig pushConfig, PluginCallbackListener callbackListener) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.cancelInvitation(invitees, invitationID, extendedData, pushConfig,
                new CancelInvitationCallback() {
                    @Override
                    public void onResult(int errorCode, String errorMessage, List<String> errorInvitees) {
                        InvitationData invitationData = getInvitationByInvitationID(invitationID);
                        List<ZegoUIKitUser> errorCancelUsers = new ArrayList<>();
                        if (invitationData != null) {
                            for (InvitationUser invitationUser : invitationData.invitees) {
                                boolean cancelUser = invitees.contains(invitationUser.getUserID());
                                boolean cancelError = errorInvitees.contains(invitationUser.getUserID());
                                if (cancelUser && !cancelError) {
                                    invitationUser.state = InvitationState.CANCEL;
                                } else {
                                    invitationUser.state = InvitationState.ERROR;
                                }
                                if (cancelError) {
                                    errorCancelUsers.add(invitationUser.user);
                                }
                            }
                        }
                        removeIfAllChecked(invitationID);
                        if (callbackListener != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("code", errorCode);
                            map.put("message", errorMessage);
                            map.put("errorInvitees", errorCancelUsers);
                            map.put("invitationID", invitationID);
                            callbackListener.callback(map);
                        }
                    }
                });
        }
    }

    @Override
    public void refuseInvitation(String inviterID, String data, PluginCallbackListener callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            String invitationID = null;
            JSONObject jsonObject = getJsonObjectFromString(data);
            if (jsonObject != null) {
                invitationID = getStringFromJson(jsonObject, "invitationID");
            }

            if (invitationID == null) {
                for (InvitationData value : invitationMap.values()) {
                    if (Objects.equals(value.inviter.userID, inviterID)) {
                        invitationID = value.id;
                        break;
                    }
                }
            }
            if (invitationID == null) {
                if (callback != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", 0);
                    callback.callback(map);
                }
                return;
            }
            String finalInvitationID = invitationID;
            callReject(finalInvitationID, data, callback);
        }
    }

    @Override
    public void callReject(String invitationID, String extendedData, PluginCallbackListener callbackListener) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.refuseInvitation(invitationID, extendedData, new ResponseInvitationCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage) {
                    if (errorCode == 0) {
                        removeInvitationData(invitationID);
                    }
                    if (callbackListener != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("code", errorCode);
                        map.put("message", errorMessage);
                        callbackListener.callback(map);
                    }
                }
            });
        }
    }

    @Override
    public void acceptInvitation(String inviterID, String data, PluginCallbackListener callback) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            String invitationID = null;
            JSONObject jsonObject = getJsonObjectFromString(data);
            if (jsonObject != null) {
                invitationID = getStringFromJson(jsonObject, "invitationID");
            }
            if (invitationID == null) {
                for (InvitationData value : invitationMap.values()) {
                    if (Objects.equals(value.inviter.userID, inviterID)) {
                        invitationID = value.id;
                        break;
                    }
                }
            }
            if (invitationID == null) {
                if (callback != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", 0);
                    callback.callback(map);
                }
                return;
            }
            String finalInvitationID = invitationID;
            callAccept(finalInvitationID, data, callback);
        }
    }

    @Override
    public void callAccept(String invitationID, String extendedData, PluginCallbackListener callbackListener) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.acceptInvitation(invitationID, extendedData, new ResponseInvitationCallback() {
                @Override
                public void onResult(int errorCode, String errorMessage) {
                    if (errorCode == 0) {
                        removeInvitationData(invitationID);
                    }
                    if (callbackListener != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("code", errorCode);
                        map.put("message", errorMessage);
                        callbackListener.callback(map);
                    }
                }
            });
        }
    }

    public void logout() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.disconnectUser();
        }

        removeRoomListenersAndData();
        signalingService.clear();
        invitationMap.clear();
    }

    @Override
    public void onActivityStarted() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            boolean isDisconnected = signalConnectionState == ZegoSignalingPluginConnectionState.DISCONNECTED;
            PluginZIMUser currentUser = signalingPlugin.getCurrentUser();
            if (currentUser != null && isDisconnected) {
                signalingPlugin.connectUser(currentUser.userID, currentUser.userName, null);
            }
        }
    }

    @Override
    public boolean isPluginExited() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        return signalingPlugin != null;
    }

    static JSONObject getJsonObjectFromString(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            Log.w("ZEGO Signal", "data is empty");
        }
        return null;
    }

    static String getStringFromJson(JSONObject jsonObject, String key) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.getString(key);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void putStringToJson(String key, String value, JSONObject jsonObject) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnectionStateChanged(ZegoSignalingPluginConnectionState state) {
        signalConnectionState = state;
        signalingService.notifyConnectionStateChange(state);
    }

    @Override
    public void onTokenWillExpire(int second) {
        UIKitCore.getInstance().notifyTokenWillExpire(second);
    }

    @Override
    public void onCallInvitationReceived(String invitationID, String inviterID, String data) {
        try {
            JSONObject jsonObject = getJsonObjectFromString(data);
            if (jsonObject != null) {
                int type = jsonObject.getInt("type");
                String inviterName = getStringFromJson(jsonObject, "inviter_name");
                String originDataString = getStringFromJson(jsonObject, "data");
                JSONObject originDataJson = getJsonObjectFromString(originDataString);
                ZegoUIKitUser inviter = new ZegoUIKitUser(inviterID, inviterName);
                PluginZIMUser currentUser = ZegoPluginAdapter.signalingPlugin().getCurrentUser();
                InvitationUser invitee = new InvitationUser(new ZegoUIKitUser(currentUser.userID, currentUser.userName),
                    InvitationState.WAITING);
                InvitationData invitationData = new InvitationData(invitationID, inviter,
                    Collections.singletonList(invitee), type);
                addInvitationData(invitationData);

                if (originDataJson == null) {
                    originDataJson = new JSONObject();
                }
                ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(inviterID);
                if (uiKitUser != null) {
                    inviter = uiKitUser;
                }
                originDataJson.put("invitationID", invitationID);
                signalingService.notifyCallInvitationReceived(inviter, type, originDataJson.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCallInvitationCancelled(String invitationID, String inviterID, String data) {
        InvitationData invitationData = removeInvitationData(invitationID);
        if (invitationData != null) {
            ZegoUIKitUser inviter = invitationData.inviter;
            ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(inviterID);
            if (uiKitUser != null) {
                inviter = uiKitUser;
            }
            JSONObject jsonObject = getJsonObjectFromString(data);
            if (jsonObject == null) {
                jsonObject = new JSONObject();
            }
            putStringToJson("invitationID", invitationID, jsonObject);
            signalingService.notifyCallInvitationCancelled(inviter, jsonObject.toString());
        }
    }

    @Override
    public void onCallInvitationAccepted(String invitationID, String invitee, String data) {
        InvitationUser invitationUser = getInvitee(invitationID, invitee);
        if (invitationUser != null) {
            invitationUser.state = InvitationState.ACCEPT;
            removeInvitationData(invitationID);
            ZegoUIKitUser inviteeUser = invitationUser.user;
            ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(invitee);
            if (uiKitUser != null) {
                inviteeUser = uiKitUser;
            }

            JSONObject jsonObject = getJsonObjectFromString(data);
            if (jsonObject == null) {
                jsonObject = new JSONObject();
            }
            putStringToJson("invitationID", invitationID, jsonObject);
            signalingService.notifyCallInvitationAccepted(inviteeUser, jsonObject.toString());
        }
    }

    @Override
    public void onCallInvitationRejected(String invitationID, String invitee, String data) {
        InvitationUser invitationUser = getInvitee(invitationID, invitee);
        if (invitationUser != null) {
            invitationUser.state = InvitationState.REFUSE;
            removeIfAllChecked(invitationID);
            ZegoUIKitUser inviteeUser = invitationUser.user;
            ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(invitee);
            if (uiKitUser != null) {
                inviteeUser = uiKitUser;
            }

            JSONObject jsonObject = getJsonObjectFromString(data);
            if (jsonObject == null) {
                jsonObject = new JSONObject();
            }
            putStringToJson("invitationID", invitationID, jsonObject);
            signalingService.notifyCallInvitationRejected(inviteeUser, jsonObject.toString());
        }
    }

    @Override
    public void onCallInvitationTimeout(String invitationID) {
        InvitationData invitationData = removeInvitationData(invitationID);
        if (invitationData != null) {
            ZegoUIKitUser inviter = invitationData.inviter;
            ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(inviter.userID);
            if (uiKitUser != null) {
                inviter = uiKitUser;
            }

            JSONObject jsonObject = new JSONObject();
            putStringToJson("invitationID", invitationID, jsonObject);
            signalingService.notifyCallInvitationTimeout(inviter, jsonObject.toString());
        }
    }

    @Override
    public void onCallInviteesAnsweredTimeout(String invitationID, List<String> invitees) {
        InvitationData invitationData = getInvitationByInvitationID(invitationID);
        if (invitationData == null) {
            return;
        }
        List<InvitationUser> timeoutUsers = GenericUtils.filter(invitationData.invitees,
            uiKitUser -> invitees.contains(uiKitUser.getUserID()));
        for (InvitationUser timeoutUser : timeoutUsers) {
            timeoutUser.state = InvitationState.TIMEOUT;
        }
        removeIfAllChecked(invitationID);
        List<ZegoUIKitUser> timeoutInvitees = GenericUtils.map(timeoutUsers, invitationUser -> invitationUser.user);

        for (ZegoUIKitUser user : timeoutInvitees) {
            ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(user.userID);
            if (uiKitUser != null) {
                user.userName = uiKitUser.userName;
            }
        }

        JSONObject jsonObject = new JSONObject();
        putStringToJson("invitationID", invitationID, jsonObject);
        signalingService.notifyCallInviteesAnsweredTimeout(timeoutInvitees, jsonObject.toString());
    }

    @Override
    public void onUsersInRoomAttributesUpdated(Map<String, HashMap<String, String>> attributesMap, String editor,
        String roomID) {
        List<ZegoUserInRoomAttributesInfo> oldAttributes = new ArrayList<>();
        String userLocalID = ZegoUIKit.getLocalUser().userID;
        if (userLocalID.equals(editor)) {
            oldAttributes.addAll(oldUsersInRoomAttributes);
        } else {
            oldAttributes.addAll(usersInRoomAttributes);
        }

        List<String> updateKeys = new ArrayList<>();
        List<ZegoUserInRoomAttributesInfo> infos = new ArrayList<>();
        for (Entry<String, HashMap<String, String>> entry : attributesMap.entrySet()) {
            infos.add(new ZegoUserInRoomAttributesInfo(entry.getKey(), entry.getValue()));
        }
        for (ZegoUserInRoomAttributesInfo info : infos) {
            int index = getIndexFromUsersInRoomAttributes(info.getUserID());
            for (Map.Entry<String, String> entry : info.getAttributes().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!updateKeys.contains(key)) {
                    updateKeys.add(key);
                }
                if (index != -1) {
                    usersInRoomAttributes.get(index).getAttributes().put(key, value);
                }
            }
            if (index == -1) {
                usersInRoomAttributes.add(info);
            }
        }

        updateCoreUserAndNotifyChanges(infos);

        ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(editor);
        signalingService.notifyUsersInRoomAttributesUpdated(updateKeys, oldAttributes, usersInRoomAttributes,
            uiKitUser);

    }

    @Override
    public void onRoomPropertiesUpdated(List<Map<String, String>> setProperties,
        List<Map<String, String>> deleteProperties, String roomID) {
        List<String> updateKeys = new ArrayList<>();
        HashMap<String, String> oldRoomAttributes = new HashMap<>(roomAttributes);

        for (Map<String, String> setProperty : setProperties) {
            for (Map.Entry<String, String> entry : setProperty.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!updateKeys.contains(key)) {
                    updateKeys.add(key);
                }
                String oldValue = roomAttributes.get(key);
                roomAttributes.put(key, value);
                signalingService.notifyRoomPropertyUpdated(key, oldValue, value);
            }
        }
        for (Map<String, String> deleteProperty : deleteProperties) {
            for (Map.Entry<String, String> entry : deleteProperty.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!updateKeys.contains(key)) {
                    updateKeys.add(key);
                }
                String oldValue = roomAttributes.get(key);
                roomAttributes.put(key, "");
                signalingService.notifyRoomPropertyUpdated(key, oldValue, "");
            }
        }
        signalingService.notifyRoomPropertyFullUpdated(updateKeys, oldRoomAttributes, roomAttributes);
    }

    @Override
    public void onRoomMemberLeft(List<String> userIDList, String roomID) {
        List<ZegoUserInRoomAttributesInfo> oldAttributes = new ArrayList<>(usersInRoomAttributes);
        for (String userID : userIDList) {
            int index = getIndexFromUsersInRoomAttributes(userID);
            if (index != -1) {
                usersInRoomAttributes.remove(index);
            }
        }
        signalingService.notifyUsersInRoomAttributesUpdated(null, oldAttributes, usersInRoomAttributes, null);
    }

    @Override
    public void onRoomMemberJoined(List<String> userIDList, String roomID) {

    }

    @Override
    public void onInRoomTextMessageReceived(List<ZegoSignalingInRoomTextMessage> messages, String roomID) {
        signalingService.notifyInRoomTextMessageReceived(messages, roomID);
    }

    @Override
    public void onInRoomCommandMessageReceived(List<ZegoSignalingInRoomCommandMessage> messages, String roomID) {
        signalingService.onInRoomCommandMessageReceived(messages, roomID);
    }

    @Override
    public void enableNotifyWhenAppRunningInBackgroundOrQuit(boolean enable) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.enableNotifyWhenAppRunningInBackgroundOrQuit(enable);
        }
    }

    @Override
    public void addConnectionStateChangeListener(ZegoUIKitSignalingPluginConnectionStateChangeListener listener) {
        signalingService.addConnectionStateChangeListener(listener);
    }

    @Override
    public void removeConnectionStateChangeListener(ZegoUIKitSignalingPluginConnectionStateChangeListener listener) {
        signalingService.removeConnectionStateChangeListener(listener);
    }

    @Override
    public void enableFCMPush() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.enableFCMPush();
        }
    }

    @Override
    public boolean isFCMPushEnable() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            return signalingPlugin.isFCMPushEnabled();
        }
        return false;
    }

    @Override
    public void disableFCMPush() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.disableFCMPush();
        }
    }

    @Override
    public void enableHWPush(String hwAppID) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.enableHWPush(hwAppID);
        }
    }

    @Override
    public void enableMiPush(String miAppID, String miAppKey) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.enableMiPush(miAppID, miAppKey);
        }
    }

    @Override
    public void enableVivoPush(String vivoAppID, String vivoAppKey) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.enableVivoPush(vivoAppID, vivoAppKey);
        }
    }

    @Override
    public void enableOppoPush(String oppoAppID, String oppoAppKey, String oppoAppSecret) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.enableOppoPush(oppoAppID, oppoAppKey, oppoAppSecret);
        }
    }

    @Override
    public boolean isOtherPushEnable() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            return signalingPlugin.isOtherPushEnabled();
        }
        return false;
    }

    @Override
    public void registerPush() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.registerPush();
        }
    }

    @Override
    public void setAppType(int appType) {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.setAppType(appType);
        }
    }

    @Override
    public String getVersion() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            return signalingPlugin.getVersion();
        } else {
            return "";
        }
    }

    @Override
    public void unregisterPush() {
        ZegoSignalingPluginProtocol signalingPlugin = ZegoPluginAdapter.signalingPlugin();
        if (signalingPlugin != null) {
            signalingPlugin.unregisterPush();
        }
    }
}
