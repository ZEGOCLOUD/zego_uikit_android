package com.zegocloud.uikit.service.express;

import im.zego.zegoexpress.callback.IZegoEventHandler;

public abstract class IExpressEngineEventHandler extends IZegoEventHandler {

    public void onLocalCameraStateUpdate(boolean open) {
    }

    public void onLocalMicrophoneStateUpdate(boolean open) {
    }
}
