package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener {

    void onUsersInRoomAttributesUpdated(List<String> updateKeys, List<ZegoUserInRoomAttributesInfo> oldAttributes, List<ZegoUserInRoomAttributesInfo> attributes, ZegoUIKitUser editor);

}
