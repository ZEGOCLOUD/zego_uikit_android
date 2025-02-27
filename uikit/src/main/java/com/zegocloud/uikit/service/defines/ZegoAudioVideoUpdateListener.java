package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoAudioVideoUpdateListener {

    void onAudioVideoAvailable(List<ZegoUIKitUser> userList);

    void onAudioVideoUnAvailable(List<ZegoUIKitUser> userList);
}
