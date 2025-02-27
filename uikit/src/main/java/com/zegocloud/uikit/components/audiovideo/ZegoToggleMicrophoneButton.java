package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.components.internal.BaseView;
import com.zegocloud.uikit.service.defines.ZegoMicrophoneStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import java.util.Objects;

public class ZegoToggleMicrophoneButton extends BaseView {

    private ZegoMicrophoneStateChangeListener microphoneStateChangeListener = this::onMicrophoneChange;
    private Drawable openDrawable;
    private Drawable closeDrawable;

    public ZegoToggleMicrophoneButton(@NonNull Context context) {
        super(context);
    }

    public ZegoToggleMicrophoneButton(@NonNull Context context, String userID) {
        super(context, userID);
    }

    public ZegoToggleMicrophoneButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView(Context context) {
        super.initView(context);
        setImageResource(R.drawable.zego_uikit_icon_mic_switch, R.drawable.zego_uikit_icon_mic_switch_off);
        setOnClickListener(null);
    }

    @Override
    public void initWidgetListener() {
        UIKitCore.getInstance().addMicrophoneStateListenerInternal(microphoneStateChangeListener);
    }

    @Override
    protected void unInitWidgetListener() {
        UIKitCore.getInstance().removeMicrophoneStateListenerInternal(microphoneStateChangeListener);
    }

    @Override
    public void invokedWhenClick() {
        String userID;
        if (TextUtils.isEmpty(mUserID)) {
            userID = UIKitCore.getInstance().getLocalCoreUser().userID;
        } else {
            userID = mUserID;
        }
        boolean micDeviceOn = UIKitCore.getInstance().isMicrophoneOn(userID);
        UIKitCore.getInstance().turnMicrophoneOn(userID, !micDeviceOn);
    }

    public boolean isOn() {
        String userID;
        if (TextUtils.isEmpty(mUserID)) {
            userID = UIKitCore.getInstance().getLocalCoreUser().userID;
        } else {
            userID = mUserID;
        }
        return UIKitCore.getInstance().isMicrophoneOn(userID);
    }

    public void turnOn(boolean isOn) {
        String userID;
        if (TextUtils.isEmpty(mUserID)) {
            userID = UIKitCore.getInstance().getLocalCoreUser().userID;
        } else {
            userID = mUserID;
        }
        UIKitCore.getInstance().turnMicrophoneOn(userID, isOn);
    }

    @Override
    public void setUserID(String mUserID) {
        super.setUserID(mUserID);
        updateImageIcon();
    }

    private void onMicrophoneChange(ZegoUIKitUser uiKitUser, boolean on) {
        boolean isLocal = TextUtils.isEmpty(mUserID) && UIKitCore.getInstance().isLocalUser(uiKitUser.userID);
        boolean isSelf = Objects.equals(mUserID, uiKitUser.userID);
        if (isLocal || isSelf) {
            updateImageIcon();
        }
    }

    public void setIcon(@DrawableRes int openDrawable,@DrawableRes int closeDrawable) {
        setImageResource(openDrawable, closeDrawable);
    }

    public void setImageDrawable(Drawable openDrawable, Drawable closeDrawable) {
        this.openDrawable = openDrawable;
        this.closeDrawable = closeDrawable;
        updateImageIcon();
    }

    public void setOpenDrawable(Drawable openDrawable) {
        this.openDrawable = openDrawable;
        updateImageIcon();
    }

    public void setCloseDrawable(Drawable closeDrawable) {
        this.closeDrawable = closeDrawable;
        updateImageIcon();
    }

    public void setImageResource(@DrawableRes int openDrawable, @DrawableRes int closeDrawable) {
        if (openDrawable != 0) {
            this.openDrawable = ContextCompat.getDrawable(getContext(), openDrawable);
        } else {
            this.openDrawable = null;
        }
        if (closeDrawable != 0) {
            this.closeDrawable = ContextCompat.getDrawable(getContext(), closeDrawable);
        } else {
            this.closeDrawable = null;
        }
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
                setImageDrawable(this.openDrawable);
            } else {
                setImageDrawable(this.closeDrawable);
            }
        } else {
            setImageDrawable(this.closeDrawable);
        }
    }
}
