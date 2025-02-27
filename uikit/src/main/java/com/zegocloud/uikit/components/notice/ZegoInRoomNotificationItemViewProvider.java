package com.zegocloud.uikit.components.notice;

import android.view.View;
import android.view.ViewGroup;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

public interface ZegoInRoomNotificationItemViewProvider {

    View getJoinView(ViewGroup parent, ZegoUIKitUser uiKitUser);

    View getLeaveView(ViewGroup parent, ZegoUIKitUser uiKitUser);

    View getMessageView(ViewGroup parent, ZegoInRoomMessage inRoomMessage);
}
