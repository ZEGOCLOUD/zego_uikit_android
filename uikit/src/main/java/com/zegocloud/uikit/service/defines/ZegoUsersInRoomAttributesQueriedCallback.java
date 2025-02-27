package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoUsersInRoomAttributesQueriedCallback {

    void onUsersInRoomAttributesQueried(List<ZegoUserInRoomAttributesInfo> attributes, String nextFlag, int errorCode, String errorMessage);

}
