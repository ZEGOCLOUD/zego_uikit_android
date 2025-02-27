package com.zegocloud.uikit.components.memberlist;

import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.List;

public interface ZegoMemberListComparator {

    List<ZegoUIKitUser> sortUserList(List<ZegoUIKitUser> userList);
}
