package com.zegocloud.uikit.components.message;

import android.view.View;
import android.view.ViewGroup;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;

public interface ZegoInRoomMessageItemViewProvider {

    View onCreateView(ViewGroup parent);

    void onBindView(View view, ZegoInRoomMessage inRoomMessage,int position);
}
