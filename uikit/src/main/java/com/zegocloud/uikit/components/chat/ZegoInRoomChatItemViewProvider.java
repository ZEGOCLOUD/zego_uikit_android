package com.zegocloud.uikit.components.chat;

import android.view.View;
import android.view.ViewGroup;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;

public interface ZegoInRoomChatItemViewProvider {

    View onCreateView(ViewGroup parent);

    void onBindView(View view, ZegoInRoomMessage message);
}
