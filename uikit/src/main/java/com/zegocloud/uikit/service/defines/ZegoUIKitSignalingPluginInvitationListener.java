package com.zegocloud.uikit.service.defines;

import java.util.List;

public interface ZegoUIKitSignalingPluginInvitationListener {

    void onInvitationReceived(ZegoUIKitUser inviter, int type, String data);

    void onInvitationTimeout(ZegoUIKitUser inviter, String data);

    void onInvitationResponseTimeout(List<ZegoUIKitUser> invitees, String data);

    void onInvitationAccepted(ZegoUIKitUser invitee, String data);

    void onInvitationRefused(ZegoUIKitUser invitee, String data);

    void onInvitationCanceled(ZegoUIKitUser inviter, String data);
}

