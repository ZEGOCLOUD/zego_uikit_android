package com.zegocloud.uikit.service.defines;

import im.zego.zegoexpress.entity.ZegoBarrageMessageInfo;
import java.util.ArrayList;

public interface ZegoBarrageMessageListener {
   void onIMRecvBarrageMessage(String roomID, ArrayList<ZegoBarrageMessageInfo> messageList);
}
