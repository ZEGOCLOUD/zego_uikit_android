package com.zegocloud.uikit.components.common;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.service.defines.ZegoScreenSharingUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler;
import im.zego.zegoexpress.constants.ZegoScreenCaptureExceptionType;
import java.util.List;

public class ZegoScreenSharingToggleButton extends ZEGOImageButton {

    private ZegoPresetResolution presetResolution = ZegoPresetResolution.PRESET_540P;
    private boolean bottomBarStyle = true;

    public ZegoScreenSharingToggleButton(@NonNull Context context) {
        super(context);
    }

    public ZegoScreenSharingToggleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZegoScreenSharingToggleButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        setImageResource(R.drawable.zego_uikit_icon_start_share_light, R.drawable.zego_uikit_icon_start_share_light);
        setScaleType(ScaleType.CENTER);

        ZegoUIKit.addScreenSharingUpdateListener(new ZegoScreenSharingUpdateListener() {
            @Override
            public void onScreenSharingAvailable(List<ZegoUIKitUser> userList) {
                if (userList.contains(ZegoUIKit.getLocalUser())) {
                    updateState(true);
                }
            }

            @Override
            public void onScreenSharingUnAvailable(List<ZegoUIKitUser> userList) {
                if (userList.contains(ZegoUIKit.getLocalUser())) {
                    updateState(false);
                }
            }
        });
        setRoundPercent(1f);

        ZegoUIKit.addEventHandler(new IExpressEngineEventHandler() {
            @Override
            public void onScreenCaptureExceptionOccurred(ZegoScreenCaptureExceptionType exceptionType) {
                super.onScreenCaptureExceptionOccurred(exceptionType);
                close();
            }
        });
    }

    @Override
    public void open() {
        super.open();
        ZegoUIKit.startSharingScreen(presetResolution);

    }

    public void setPresetResolution(ZegoPresetResolution presetResolution) {
        this.presetResolution = presetResolution;
    }

    public void bottomBarStyle() {
        bottomBarStyle = true;
        updateState(isOpen());
    }

    public void topBarStyle() {
        bottomBarStyle = false;
        updateState(isOpen());
    }

    @Override
    public void updateState(boolean state) {
        if (bottomBarStyle) {
            if (state) {
                setImageResource(R.drawable.zego_uikit_icon_stop_share_dark);
            } else {
                setImageResource(R.drawable.zego_uikit_icon_start_share_light);
            }
        } else {
            setBackgroundDrawable(null);
            if (state) {
                setImageResource(R.drawable.zego_uikit_icon_stop_share);
            } else {
                setImageResource(R.drawable.zego_uikit_icon_start_share);
            }
        }
    }

    @Override
    public void close() {
        super.close();
        ZegoUIKit.stopSharingScreen();
    }
}
