package com.zegocloud.uikit.service.internal.interfaces;

import com.zegocloud.uikit.service.defines.ZegoMeRemovedFromRoomListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitCallback;
import com.zegocloud.uikit.service.defines.ZegoUserCountOrPropertyChangedListener;
import com.zegocloud.uikit.service.defines.ZegoOnlySelfInRoomListener;
import com.zegocloud.uikit.service.defines.ZegoUserUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.List;

public interface IUserService {

    void login(String userID, String userName, ZegoUIKitCallback callback);

    void logout();

    ZegoUIKitUser getUser(String userID);

    ZegoUIKitUser getLocalUser();

    List<ZegoUIKitUser> getAllUsers();

    void addOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener);

    void removeOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener);

    void addUserUpdateListener(ZegoUserUpdateListener listener);

    void removeUserUpdateListener(ZegoUserUpdateListener listener);

    void addUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener);

    void removeUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener);

    void removeUserFromRoom(List<String> userIDs);

    void addOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener);

    void removeOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener);
}
