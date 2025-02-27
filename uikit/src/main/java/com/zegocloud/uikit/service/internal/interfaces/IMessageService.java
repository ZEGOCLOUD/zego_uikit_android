package com.zegocloud.uikit.service.internal.interfaces;

import com.zegocloud.uikit.service.defines.ZegoBarrageMessageListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageSendStateListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import im.zego.zegoexpress.callback.IZegoIMSendBarrageMessageCallback;
import java.util.List;

public interface IMessageService {

    List<ZegoInRoomMessage> getInRoomMessages();

    void sendInRoomMessage(String message);

    void sendInRoomMessage(String message, ZegoInRoomMessageSendStateListener listener);

    void addInRoomMessageReceivedListener(ZegoInRoomMessageListener listener);

    void removeInRoomMessageReceivedListener(ZegoInRoomMessageListener listener);

    void sendBarrageMessage(String roomID, String message, IZegoIMSendBarrageMessageCallback callback);

    void addBarrageMessageListener(ZegoBarrageMessageListener listener);

    void removeBarrageMessageListener(ZegoBarrageMessageListener listener);

}
