package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import com.zegocloud.uikit.components.internal.RippleIconView;
import com.zegocloud.uikit.service.defines.ZegoSoundLevelUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import java.util.Objects;

public class ZegoAvatarView extends FrameLayout {


    private ZegoSoundLevelUpdateListener soundListener = this::onSoundUpdateListener;
    private static final int MIN_SOUND = 5;
    protected String mUserID;
    private boolean showSoundWave;
    private RippleIconView rippleIconView;
    private FrameLayout customView;
    private ZegoAvatarViewProvider avatarViewProvider;

    public ZegoAvatarView(Context context) {
        super(context);
        initView();
    }

    public ZegoAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        rippleIconView = new RippleIconView(getContext());
        addView(rippleIconView);
        rippleIconView.setText("", false);
        customView = new FrameLayout(getContext());
        addView(customView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initWidgetListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unInitWidgetListener();
    }

    public void initWidgetListener() {
        UIKitCore.getInstance().addSoundLevelUpdatedListenerInternal(soundListener);
    }

    protected void unInitWidgetListener() {
        UIKitCore.getInstance().removeSoundLevelUpdatedListenerInternal(soundListener);
    }

    public void setShowSoundWave(boolean showSoundWave) {
        this.showSoundWave = showSoundWave;
    }

    private void onSoundUpdateListener(ZegoUIKitUser user, float soundLevel) {
        boolean isSelf = Objects.equals(mUserID, user.userID);
        if (isSelf) {
            if (soundLevel > MIN_SOUND) {
                if (showSoundWave) {
                    rippleIconView.startAnimation();
                }
            } else {
                rippleIconView.stopAnimation();
            }
        }
    }

    public void setRadius(int radius) {
        rippleIconView.setRadius(radius);
    }

    public void setTextSize(int textSize) {
        rippleIconView.setTextSize(textSize);
    }

    public void setRippleWidth(int rippleWidth) {
        rippleIconView.setRippleWidth(rippleWidth);
    }

    public void setRippleColor(int color) {
        rippleIconView.setRippleColor(color);
    }

    public void updateUser(String userID) {
        boolean userIDChanged = !Objects.equals(mUserID, userID);
        this.mUserID = userID;
        UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(mUserID);
        if (coreUser != null) {
            rippleIconView.setText(coreUser.userName, userIDChanged);
        } else {
            rippleIconView.setText("", userIDChanged);
        }
        int size = rippleIconView.getRadius() * 2;
        LayoutParams params = new LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        customView.setLayoutParams(params);

        if (avatarViewProvider != null) {
            if (coreUser != null) {
                View providerView = avatarViewProvider.onUserIDUpdated(this, coreUser.getUIKitUser());
                if (providerView != null) {
                    customView.removeAllViews();
                    customView.addView(providerView);
                }
            }
        }
    }

    public void setAvatarViewProvider(ZegoAvatarViewProvider avatarViewProvider) {
        this.avatarViewProvider = avatarViewProvider;
    }

    public void onSizeChanged(int radius) {
        LayoutParams params = new LayoutParams(radius * 2, radius * 2);
        params.gravity = Gravity.CENTER;
        customView.setLayoutParams(params);
    }
}
