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
import com.zegocloud.uikit.service.defines.ZegoCameraStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import java.util.Objects;

public class ZegoCameraStateView extends BaseView {


    private ZegoCameraStateChangeListener stateListener = this::onCameraStateChange;
    private Drawable openDrawable;
    private Drawable closeDrawable;

    public ZegoCameraStateView(@NonNull Context context) {
        super(context);
    }

    public ZegoCameraStateView(@NonNull Context context, String userID) {
        super(context, userID);
    }

    public ZegoCameraStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(Context context) {
        setImageResource(R.drawable.zego_uikit_icon_camera_state, R.drawable.zego_uikit_icon_camera_state_off);
    }

    @Override
    public void initWidgetListener() {
        UIKitCore.getInstance().addCameraStateListenerInternal(stateListener);
    }

    @Override
    protected void unInitWidgetListener() {
        UIKitCore.getInstance().removeCameraStateListenerInternal(stateListener);
    }

    @Override
    public void invokedWhenClick() {

    }

    private void onCameraStateChange(ZegoUIKitUser uiKitUser, boolean on) {
        boolean isLocal = TextUtils.isEmpty(mUserID) && UIKitCore.getInstance().isLocalUser(uiKitUser.userID);
        boolean isSelf = Objects.equals(mUserID, uiKitUser.userID);
        if (isLocal || isSelf) {
            updateImageIcon();
        }
    }

    @Override
    public void setUserID(String mUserID) {
        super.setUserID(mUserID);
        updateImageIcon();
    }


    public void setIcon(int openDrawable, int closeDrawable) {
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
            if (coreUser.isCameraOpen) {
                setImageDrawable(this.openDrawable);
            } else {
                setImageDrawable(this.closeDrawable);
            }
        } else {
            setImageDrawable(this.closeDrawable);
        }
    }
}
