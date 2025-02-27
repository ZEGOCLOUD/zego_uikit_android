package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.service.defines.ZegoCameraStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoMicrophoneStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserInRoomAttributesInfo;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class ZegoBaseAudioVideoForegroundView extends FrameLayout {

    private ZegoCameraStateChangeListener cameraStateListener;
    private ZegoMicrophoneStateChangeListener microphoneStateListener;
    private ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener usersInRoomAttributesUpdateListener;
    protected String userID;

    public ZegoBaseAudioVideoForegroundView(@NonNull Context context, String userID) {
        super(context);
        this.userID = userID;
        initView();
    }

    public ZegoBaseAudioVideoForegroundView(@NonNull Context context, @Nullable AttributeSet attrs, String userID) {
        super(context, attrs);
        this.userID = userID;
        initView();
    }


    private void initView() {
        onForegroundViewCreated(ZegoUIKit.getUser(userID));
        cameraStateListener = new ZegoCameraStateChangeListener() {
            @Override
            public void onCameraOn(ZegoUIKitUser uiKitUser, boolean isOn) {
                if (Objects.equals(userID, uiKitUser.userID)) {
                    onCameraStateChanged(uiKitUser.isCameraOn);
                }
            }
        };
        microphoneStateListener = new ZegoMicrophoneStateChangeListener() {
            @Override
            public void onMicrophoneOn(ZegoUIKitUser uiKitUser, boolean isOn) {
                if (Objects.equals(userID, uiKitUser.userID)) {
                    onMicrophoneStateChanged(uiKitUser.isMicrophoneOn);
                }
            }
        };
        usersInRoomAttributesUpdateListener = new ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener() {
            @Override
            public void onUsersInRoomAttributesUpdated(List<String> updateKeys,
                List<ZegoUserInRoomAttributesInfo> oldAttributes, List<ZegoUserInRoomAttributesInfo> attributes,
                ZegoUIKitUser editor) {
                for (ZegoUserInRoomAttributesInfo attribute : attributes) {
                    if (Objects.equals(attribute.getUserID(), userID)) {
                        ZegoUIKitUser user = ZegoUIKit.getUser(userID);
                        if (user != null) {
                            onInRoomAttributesUpdated(user.inRoomAttributes);
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UIKitCore.getInstance().addCameraStateListenerInternal(cameraStateListener);
        UIKitCore.getInstance().addMicrophoneStateListenerInternal(microphoneStateListener);
        if (ZegoUIKit.getSignalingPlugin() != null) {
            ZegoUIKit.getSignalingPlugin().addUsersInRoomAttributesUpdateListener(usersInRoomAttributesUpdateListener);
        }
        ZegoUIKitUser uiKitUser = ZegoUIKit.getUser(userID);
        if (uiKitUser != null) {
            onCameraStateChanged(uiKitUser.isCameraOn);
            onMicrophoneStateChanged(uiKitUser.isMicrophoneOn);
            onInRoomAttributesUpdated(uiKitUser.inRoomAttributes);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().removeCameraStateListenerInternal(cameraStateListener);
        UIKitCore.getInstance().removeMicrophoneStateListenerInternal(microphoneStateListener);
        ZegoUIKit.getSignalingPlugin()
            .removeUsersInRoomAttributesUpdateListener(usersInRoomAttributesUpdateListener);
    }

    protected void onForegroundViewCreated(ZegoUIKitUser uiKitUser) {

    }

    protected void onCameraStateChanged(boolean isCameraOn) {

    }

    protected void onMicrophoneStateChanged(boolean isMicrophoneOn) {

    }

    protected void onInRoomAttributesUpdated(HashMap<String, String> inRoomAttributes) {

    }
}
