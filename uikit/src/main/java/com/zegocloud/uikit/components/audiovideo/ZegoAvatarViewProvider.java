package com.zegocloud.uikit.components.audiovideo;

import android.view.View;
import android.view.ViewGroup;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

public interface ZegoAvatarViewProvider {

    View onUserIDUpdated(ViewGroup parent, ZegoUIKitUser uiKitUser);
}
