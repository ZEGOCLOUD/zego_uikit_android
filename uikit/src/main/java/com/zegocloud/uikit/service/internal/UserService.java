package com.zegocloud.uikit.service.internal;

import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.service.defines.ZegoMeRemovedFromRoomListener;
import com.zegocloud.uikit.service.defines.ZegoOnlySelfInRoomListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserCountOrPropertyChangedListener;
import com.zegocloud.uikit.service.defines.ZegoUserUpdateListener;
import java.util.List;

class UserService {

    private NotifyList<ZegoUserUpdateListener> userUpdateListeners = new NotifyList<>();
    private NotifyList<ZegoOnlySelfInRoomListener> onlySelfListeners = new NotifyList<>();
    private NotifyList<ZegoUserCountOrPropertyChangedListener> roomUserCountOrPropertyChangedListeners = new NotifyList<>();
    private NotifyList<ZegoMeRemovedFromRoomListener> kickOutListenerNotifyList = new NotifyList<>();

    public void addOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener, boolean weakRef) {
        onlySelfListeners.addListener(listener, weakRef);
    }

    public void removeOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener, boolean weakRef) {
        onlySelfListeners.removeListener(listener, weakRef);
    }

    public void clear() {
        userUpdateListeners.clear();
        onlySelfListeners.clear();
        roomUserCountOrPropertyChangedListeners.clear();
        kickOutListenerNotifyList.clear();
    }

    public void notifyUserJoin(List<UIKitCoreUser> userInfoList) {
        List<ZegoUIKitUser> collect = GenericUtils.map(userInfoList, UIKitCoreUser::getUIKitUser);
        userUpdateListeners.notifyAllListener(roomUserUpdateListener -> {
            roomUserUpdateListener.onUserJoined(collect);
        });
    }

    public void notifyUserLeave(List<UIKitCoreUser> userInfoList) {
        List<ZegoUIKitUser> collect = GenericUtils.map(userInfoList, UIKitCoreUser::getUIKitUser);
        userUpdateListeners.notifyAllListener(roomUserUpdateListener -> {
            roomUserUpdateListener.onUserLeft(collect);
        });
    }

    public void notifyOnlySelfInRoom() {
        onlySelfListeners.notifyAllListener(ZegoOnlySelfInRoomListener::onOnlySelfInRoom);
    }

    public void addUserUpdateListener(ZegoUserUpdateListener listener, boolean weakRef) {
        userUpdateListeners.addListener(listener, weakRef);
    }

    public void removeUserUpdateListener(ZegoUserUpdateListener listener, boolean weakRef) {
        userUpdateListeners.removeListener(listener, weakRef);
    }

    public void addUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener,
        boolean weakRef) {
        roomUserCountOrPropertyChangedListeners.addListener(listener, weakRef);
    }

    public void removeUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener,
        boolean weakRef) {
        roomUserCountOrPropertyChangedListeners.removeListener(listener, weakRef);
    }

    public void notifyRoomUserCountOrPropertyChanged(List<ZegoUIKitUser> userList) {
        roomUserCountOrPropertyChangedListeners.notifyAllListener(userCountOrPropertyChangedListener -> {
            userCountOrPropertyChangedListener.onUserCountOrPropertyChanged(userList);
        });
    }

    public void addOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener, boolean weakRef) {
        kickOutListenerNotifyList.addListener(listener, weakRef);
    }

    public void removeOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener, boolean weakRef) {
        kickOutListenerNotifyList.removeListener(listener, weakRef);
    }

    public void notifyRemovedFromRoomCommand() {
        kickOutListenerNotifyList.notifyAllListener(ZegoMeRemovedFromRoomListener::onMeRemovedFromRoom);
    }
}
