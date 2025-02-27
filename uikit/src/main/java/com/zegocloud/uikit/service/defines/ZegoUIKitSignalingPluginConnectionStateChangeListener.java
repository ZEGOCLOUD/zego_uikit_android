package com.zegocloud.uikit.service.defines;

import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginConnectionState;

public interface ZegoUIKitSignalingPluginConnectionStateChangeListener {

    void onConnectionStateChanged(ZegoSignalingPluginConnectionState connectionState);
}
