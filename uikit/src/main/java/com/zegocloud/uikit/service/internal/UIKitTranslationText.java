package com.zegocloud.uikit.service.internal;

import com.zegocloud.uikit.internal.ZegoUIKitLanguage;

public class UIKitTranslationText {

    public String UIKit_InRoomMessage_Host;
    public String UIKit_MessageInput_Hint;
    public String UIKit_TAG_YOU;
    public String UIKit_FIXEDLAYOUT_MORE;
    public String UIKit_USER_JOIN_ROOM;
    public String UIKit_USER_LEFT_ROOM;
    public String UIKit_SHAREING_SCREEN_TIPS;
    public String UIKit_STOP_SHARE;

    public UIKitTranslationText() {
        this(ZegoUIKitLanguage.ENGLISH);
    }

    public UIKitTranslationText(ZegoUIKitLanguage language) {
        if (language == ZegoUIKitLanguage.CHS) {
            UIKit_InRoomMessage_Host = "主持人";
            UIKit_MessageInput_Hint = "说点什么";
            UIKit_TAG_YOU = "(你)";
            UIKit_FIXEDLAYOUT_MORE = "其他 %d 个";
            UIKit_USER_JOIN_ROOM = "%s 加入了会议";
            UIKit_USER_LEFT_ROOM = "%s 离开了会议";
            UIKit_SHAREING_SCREEN_TIPS = "正在进行屏幕共享";
            UIKit_STOP_SHARE = "停止共享";
        } else {
            UIKit_InRoomMessage_Host = "Host";
            UIKit_MessageInput_Hint = "Say Something";
            UIKit_TAG_YOU = "(You)";
            UIKit_FIXEDLAYOUT_MORE = "%d others";
            UIKit_USER_JOIN_ROOM = "%s joins the conference";
            UIKit_USER_LEFT_ROOM = "%s left the conference";
            UIKit_SHAREING_SCREEN_TIPS = "Your are sharing screen";
            UIKit_STOP_SHARE = "Stop Sharing";
        }
    }
}
