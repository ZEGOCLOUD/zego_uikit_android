package com.zegocloud.uikit.plugin.internal;

import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

public class InvitationUser {

    public ZegoUIKitUser user;
    public InvitationState state;

    public InvitationUser(ZegoUIKitUser user, InvitationState state) {
        this.user = user;
        this.state = state;
    }

    public String getUserID() {
        return user.userID;
    }
}
