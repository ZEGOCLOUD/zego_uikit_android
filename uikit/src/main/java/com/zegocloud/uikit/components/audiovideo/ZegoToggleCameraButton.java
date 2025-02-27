package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.service.internal.UIKitCore;

public class ZegoToggleCameraButton extends ZegoCameraStateView {

    public ZegoToggleCameraButton(Context context) {
        super(context);
    }

    public ZegoToggleCameraButton(@NonNull Context context, String userID) {
        super(context, userID);
    }

    public ZegoToggleCameraButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(Context context) {
        setImageResource(R.drawable.zego_uikit_icon_camera_switch, R.drawable.zego_uikit_icon_camera_switch_off);
        setOnClickListener(null);
    }

    @Override
    public void invokedWhenClick() {
        String userID;
        if (TextUtils.isEmpty(mUserID)) {
            userID = UIKitCore.getInstance().getLocalCoreUser().userID;
        } else {
            userID = mUserID;
        }
        boolean cameraDeviceOn = UIKitCore.getInstance().isCameraOn(userID);
        UIKitCore.getInstance().turnCameraOn(userID, !cameraDeviceOn);
    }

    public boolean isOn() {
        String userID;
        if (TextUtils.isEmpty(mUserID)) {
            userID = UIKitCore.getInstance().getLocalCoreUser().userID;
        } else {
            userID = mUserID;
        }
        return UIKitCore.getInstance().isCameraOn(userID);
    }

    public void turnOn(boolean isOn) {
        String userID;
        if (TextUtils.isEmpty(mUserID)) {
            userID = UIKitCore.getInstance().getLocalCoreUser().userID;
        } else {
            userID = mUserID;
        }
        UIKitCore.getInstance().turnCameraOn(userID, isOn);
    }
}
