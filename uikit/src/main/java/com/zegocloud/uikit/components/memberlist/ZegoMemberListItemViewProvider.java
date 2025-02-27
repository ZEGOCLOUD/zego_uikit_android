package com.zegocloud.uikit.components.memberlist;

import android.view.View;
import android.view.ViewGroup;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

public interface ZegoMemberListItemViewProvider {

    View onCreateView(ViewGroup parent);

    void onBindView(View view, ZegoUIKitUser uiKitUser,int position);
}
