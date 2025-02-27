package com.zegocloud.uikit.service.internal;

import com.zegocloud.uikit.components.message.ZegoInRoomMessageState;
import com.zegocloud.uikit.service.defines.ZegoBarrageMessageListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageSendStateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitRoom;
import com.zegocloud.uikit.service.express.ExpressEngineProxy;
import im.zego.zegoexpress.callback.IZegoIMSendBroadcastMessageCallback;
import im.zego.zegoexpress.entity.ZegoBarrageMessageInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class MessageService {

    private NotifyList<ZegoInRoomMessageListener> messageListeners = new NotifyList<>();
    private NotifyList<ZegoBarrageMessageListener> barrageListeners = new NotifyList<>();
    public static int messageID = 0;


    public void sendInRoomMessage(String message, ZegoInRoomMessageSendStateListener listener) {
        ZegoUIKitRoom room = UIKitCore.getInstance().getRoom();
        ZegoInRoomMessage inRoomMessage = new ZegoInRoomMessage();
        inRoomMessage.user = UIKitCore.getInstance().getLocalCoreUser().getUIKitUser();
        inRoomMessage.timestamp = System.currentTimeMillis();
        inRoomMessage.message = message;
        inRoomMessage.state = ZegoInRoomMessageState.IDLE;
        messageID = messageID - 1;
        inRoomMessage.messageID = messageID;

        List<ZegoInRoomMessage> inRoomMessages = UIKitCore.getInstance().getInRoomMessages();
        inRoomMessages.add(inRoomMessage);

        if (listener != null) {
            listener.onInRoomMessageSendingStateChanged(inRoomMessage);
        }
        messageListeners.notifyAllListener(inRoomMessageListener -> {
            inRoomMessageListener.onInRoomMessageSendingStateChanged(inRoomMessage);
        });
        // if success within 300 ms,then show success directly,
        // else show loading
        Runnable runnable = () -> {
            if (inRoomMessage.state == ZegoInRoomMessageState.IDLE) {
                inRoomMessage.state = ZegoInRoomMessageState.SENDING;
                if (listener != null) {
                    listener.onInRoomMessageSendingStateChanged(inRoomMessage);
                }
                messageListeners.notifyAllListener(inRoomMessageListener -> {
                    inRoomMessageListener.onInRoomMessageSendingStateChanged(inRoomMessage);
                });
            }
        };
        UIKitCore.getInstance().handler.postDelayed(runnable, 300);

        ExpressEngineProxy.sendBroadcastMessage(room.roomID, message, new IZegoIMSendBroadcastMessageCallback() {
            @Override
            public void onIMSendBroadcastMessageResult(int errorCode, long messageID) {
                UIKitCore.getInstance().handler.removeCallbacks(runnable);
                inRoomMessage.state = (errorCode == 0) ? ZegoInRoomMessageState.SUCCESS : ZegoInRoomMessageState.FAILED;
                if (listener != null) {
                    listener.onInRoomMessageSendingStateChanged(inRoomMessage);
                }
                messageListeners.notifyAllListener(inRoomMessageListener -> {
                    inRoomMessageListener.onInRoomMessageSendingStateChanged(inRoomMessage);
                });
            }
        });
    }

    public void resendInRoomMessage(ZegoInRoomMessage message, ZegoInRoomMessageSendStateListener listener) {
        List<ZegoInRoomMessage> inRoomMessages = UIKitCore.getInstance().getInRoomMessages();
        for (ZegoInRoomMessage inRoomMessage : inRoomMessages) {
            if (Objects.equals(inRoomMessage.messageID, message.messageID)) {
                inRoomMessages.remove(inRoomMessage);
                break;
            }
        }
        sendInRoomMessage(message.message, listener);
    }

    public void notifyInRoomMessageReceived(String roomID, List<ZegoInRoomMessage> messageList) {
        messageListeners.notifyAllListener(inRoomMessageReceivedListener -> {
            inRoomMessageReceivedListener.onInRoomMessageReceived(messageList);
        });
    }

    public void addInRoomMessageReceivedListener(ZegoInRoomMessageListener listener, boolean weakRef) {
        messageListeners.addListener(listener, weakRef);
    }

    public void removeInRoomMessageReceivedListener(ZegoInRoomMessageListener listener, boolean weakRef) {
        messageListeners.removeListener(listener, weakRef);
    }

    public void clear() {
        messageListeners.clear();
        barrageListeners.clear();
    }

    public void addBarrageMessageListener(ZegoBarrageMessageListener listener, boolean weakRef) {
        barrageListeners.addListener(listener, weakRef);
    }

    public void removeBarrageMessageListener(ZegoBarrageMessageListener listener, boolean weakRef) {
        barrageListeners.removeListener(listener, weakRef);
    }


    public void notifyIMRecvBarrageMessage(String roomID, ArrayList<ZegoBarrageMessageInfo> messageList) {
        barrageListeners.notifyAllListener(zegoBarrageMessageListener -> {
            zegoBarrageMessageListener.onIMRecvBarrageMessage(roomID, messageList);
        });
    }
}
