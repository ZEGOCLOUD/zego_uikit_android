package com.zegocloud.uikit.plugin.internal;

import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.List;

public class InvitationData {

    public String id; // invitation ID
    public ZegoUIKitUser inviter;
    public List<InvitationUser> invitees;
    public int type;

    public InvitationData(String id, ZegoUIKitUser inviter, List<InvitationUser> invitees, int type) {
        this.id = id;
        this.inviter = inviter;
        this.invitees = invitees;
        this.type = type;
    }
}
