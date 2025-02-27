package com.zegocloud.uikit.service.defines;

public interface ZegoOnlySelfInRoomListener {

    /**
     * when the other user in the call leaves room,this callback will be invoked.
     * if you need to finish call,you can call ZegoUIKitPrebuiltCallInvitationService.endCall()
     * in the callback.
     */
    void onOnlySelfInRoom();
}
