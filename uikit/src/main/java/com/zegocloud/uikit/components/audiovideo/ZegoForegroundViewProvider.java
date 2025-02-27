package com.zegocloud.uikit.components.audiovideo;

import android.view.ViewGroup;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

public interface ZegoForegroundViewProvider {

    ZegoBaseAudioVideoForegroundView getForegroundView(ViewGroup parent, ZegoUIKitUser uiKitUser);
}
