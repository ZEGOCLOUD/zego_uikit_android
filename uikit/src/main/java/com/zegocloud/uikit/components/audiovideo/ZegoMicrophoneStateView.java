package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.components.internal.BaseView;
import com.zegocloud.uikit.service.defines.ZegoMicrophoneStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoSoundLevelUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import java.util.Objects;

public class ZegoMicrophoneStateView extends BaseView {

    private ZegoMicrophoneStateChangeListener stateListener = this::onMicrophoneChange;
    private ZegoSoundLevelUpdateListener soundListener = this::onSoundUpdateListener;

    private static final int MIN_SOUND = 5;
    private int iconMicrophoneOn;
    private int iconMicrophoneOff;
    private int iconMicrophoneInputting;
    private int state = 0;

    public ZegoMicrophoneStateView(@NonNull Context context) {
        super(context);
    }

    public ZegoMicrophoneStateView(@NonNull Context context, String userID) {
        super(context, userID);
    }

    public ZegoMicrophoneStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(Context context) {
        setImageResource(R.drawable.zego_uikit_icon_mic_state, R.drawable.zego_uikit_icon_mic_state_off, R.drawable.zego_uikit_icon_mic_state_wave);
    }

    @Override
    public void initWidgetListener() {
        UIKitCore.getInstance().addMicrophoneStateListenerInternal(stateListener);
        UIKitCore.getInstance().addSoundLevelUpdatedListenerInternal(soundListener);
    }

    @Override
    protected void unInitWidgetListener() {
        UIKitCore.getInstance().removeMicrophoneStateListenerInternal(stateListener);
        UIKitCore.getInstance().removeSoundLevelUpdatedListenerInternal(soundListener);
    }

    @Override
    public void invokedWhenClick() {

    }

    private void onMicrophoneChange(ZegoUIKitUser uiKitUser, boolean on) {
        boolean isLocal = TextUtils.isEmpty(mUserID) && UIKitCore.getInstance().isLocalUser(uiKitUser.userID);
        boolean isSelf = Objects.equals(mUserID, uiKitUser.userID);
        if (isLocal || isSelf) {
            updateImageIcon();
        }
    }

    private void onSoundUpdateListener(ZegoUIKitUser uiKitUser, float soundLevel) {
        boolean isLocal = TextUtils.isEmpty(mUserID) && UIKitCore.getInstance().isLocalUser(uiKitUser.userID);
        boolean isSelf = Objects.equals(mUserID, uiKitUser.userID);
        if (isLocal || isSelf) {
            updateImageIcon();
        }
    }

    @Override
    public void setUserID(String userID) {
        super.setUserID(userID);
        updateImageIcon();
    }

    public void setImageResource(@DrawableRes int iconMicrophoneOn, @DrawableRes int iconMicrophoneOff,
        @DrawableRes int iconMicrophoneInputting) {

        this.iconMicrophoneOn = iconMicrophoneOn;
        this.iconMicrophoneOff = iconMicrophoneOff;
        this.iconMicrophoneInputting = iconMicrophoneInputting;

        updateImageIcon();
    }

    public void setIcon(@DrawableRes int iconMicrophoneOn, @DrawableRes int iconMicrophoneOff,
        @DrawableRes int iconMicrophoneInputting) {

        this.iconMicrophoneOn = iconMicrophoneOn;
        this.iconMicrophoneOff = iconMicrophoneOff;
        this.iconMicrophoneInputting = iconMicrophoneInputting;

        updateImageIcon();
    }

    private void updateImageIcon() {
        UIKitCoreUser coreUser;
        if (TextUtils.isEmpty(mUserID)) {
            coreUser = UIKitCore.getInstance().getLocalCoreUser();
        } else {
            coreUser = UIKitCore.getInstance().getUserbyUserID(mUserID);
        }
        if (coreUser != null) {
            if (coreUser.isMicOpen) {
                if (coreUser.soundLevel > MIN_SOUND) {
                    setImageResource(iconMicrophoneInputting);
                } else {
                    setImageResource(iconMicrophoneOn);
                }
            } else {
                setImageResource(iconMicrophoneOff);
            }
        } else {
            setImageResource(iconMicrophoneOff);
        }
    }
}
