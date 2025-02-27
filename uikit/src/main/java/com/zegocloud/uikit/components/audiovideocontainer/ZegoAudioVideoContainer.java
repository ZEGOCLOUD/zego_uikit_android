package com.zegocloud.uikit.components.audiovideocontainer;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.components.audiovideo.ZegoAvatarViewProvider;
import com.zegocloud.uikit.components.audiovideo.ZegoForegroundViewProvider;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoScreenSharingUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserUpdateListener;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.util.List;

public class ZegoAudioVideoContainer extends FrameLayout {

    private LayoutManager layoutManager = new DefaultLayoutManager(this);
    private ZegoAudioVideoViewConfig audioVideoConfig = new ZegoAudioVideoViewConfig();
    private ZegoForegroundViewProvider audioVideoForegroundViewProvider;
    private ZegoForegroundViewProvider screenShareForegroundViewProvider;
    private ZegoAvatarViewProvider zegoAvatarViewProvider;
    ZegoAudioVideoComparator audioVideoComparator;
    private ZegoAudioVideoUpdateListener audioVideoUpdateListener = new ZegoAudioVideoUpdateListener() {
        @Override
        public void onAudioVideoAvailable(List<ZegoUIKitUser> userList) {
            if (layoutManager != null) {
                layoutManager.onAudioVideoAvailable(userList);
            }
        }

        @Override
        public void onAudioVideoUnAvailable(List<ZegoUIKitUser> userList) {
            if (layoutManager != null) {
                layoutManager.onAudioVideoUnAvailable(userList);
            }
        }
    };
    private ZegoUserUpdateListener userUpdateListener = new ZegoUserUpdateListener() {

        @Override
        public void onUserJoined(List<ZegoUIKitUser> userInfoList) {
            if (layoutManager != null) {
                layoutManager.onUserJoined(userInfoList);
            }
        }

        @Override
        public void onUserLeft(List<ZegoUIKitUser> userInfoList) {
            if (layoutManager != null) {
                layoutManager.onUserLeft(userInfoList);
            }
        }
    };

    private ZegoScreenSharingUpdateListener screenSharingUpdateListener = new ZegoScreenSharingUpdateListener() {
        @Override
        public void onScreenSharingAvailable(List<ZegoUIKitUser> userList) {
            if (layoutManager != null) {
                layoutManager.onScreenSharingAvailable(userList);
            }
        }

        @Override
        public void onScreenSharingUnAvailable(List<ZegoUIKitUser> userList) {
            if (layoutManager != null) {
                layoutManager.onScreenSharingUnAvailable(userList);
            }
        }
    };

    public ZegoAudioVideoContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoAudioVideoContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UIKitCore.getInstance().addUserUpdateListenerInternal(userUpdateListener);
        UIKitCore.getInstance().addAudioVideoUpdateListenerInternal(audioVideoUpdateListener);
        UIKitCore.getInstance().addScreenSharingUpdateListenerInternal(screenSharingUpdateListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().removeUserUpdateListenerInternal(userUpdateListener);
        UIKitCore.getInstance().removeAudioVideoUpdateListenerInternal(audioVideoUpdateListener);
        UIKitCore.getInstance().removeScreenSharingUpdateListenerInternal(screenSharingUpdateListener);
    }

    public void setAvatarViewProvider(ZegoAvatarViewProvider zegoAvatarViewProvider) {
        this.zegoAvatarViewProvider = zegoAvatarViewProvider;
    }

    public ZegoAvatarViewProvider getAvatarViewProvider() {
        return zegoAvatarViewProvider;
    }

    public void setAudioVideoForegroundViewProvider(@Nullable ZegoForegroundViewProvider zegoForegroundViewProvider) {
        this.audioVideoForegroundViewProvider = zegoForegroundViewProvider;
    }

    ZegoForegroundViewProvider getAudioVideoForegroundViewProvider() {
        return audioVideoForegroundViewProvider;
    }

    public void setScreenShareForegroundViewProvider(ZegoForegroundViewProvider screenShareForegroundViewProvider) {
        this.screenShareForegroundViewProvider = screenShareForegroundViewProvider;
    }

    ZegoForegroundViewProvider getScreenShareForegroundViewProvider() {
        return screenShareForegroundViewProvider;
    }

    public void showScreenSharingViewInFullscreenMode(String userID, boolean fullscreen) {
        if (layoutManager instanceof FixedLayoutManager) {
            ((FixedLayoutManager) layoutManager).showScreenSharingViewInFullscreenMode(userID, fullscreen);
        }
    }


    public boolean isScreenSharingViewInFullscreenMode(String userID) {
        if (layoutManager instanceof FixedLayoutManager) {
            return ((FixedLayoutManager) layoutManager).isScreenSharingViewInFullscreenMode(userID);
        }
        return false;
    }

    public void setAudioVideoComparator(ZegoAudioVideoComparator comparator) {
        this.audioVideoComparator = comparator;
    }

    public void setLayout(ZegoLayout zegoLayout) {
        if (zegoLayout != null) {
            if (zegoLayout.mode == ZegoLayoutMode.PICTURE_IN_PICTURE && !(layoutManager instanceof PipLayoutManager)) {
                layoutManager = new PipLayoutManager(this);
            }
            if (zegoLayout.mode == ZegoLayoutMode.GALLERY && !(layoutManager instanceof FixedLayoutManager)) {
                layoutManager = new FixedLayoutManager(this);
            }
            layoutManager.setConfig(zegoLayout.config);
        }
        layoutManager.setAudioVideoConfig(audioVideoConfig);
        layoutManager.notifyDataSetChanged();
    }

    public void updateLayout() {
        layoutManager.notifyDataSetChanged();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // no delay not work
        getHandler().postDelayed(() -> {
            if (layoutManager != null) {
                layoutManager.onConfigurationChanged();
            }
        }, 100);
    }

    public void setAudioVideoConfig(@Nullable ZegoAudioVideoViewConfig audioVideoConfig) {
        if (audioVideoConfig == null) {
            audioVideoConfig = new ZegoAudioVideoViewConfig();
        }
        this.audioVideoConfig = audioVideoConfig;
        if (layoutManager != null) {
            layoutManager.setAudioVideoConfig(audioVideoConfig);
        }
    }

    abstract static class LayoutManager {

        protected ZegoAudioVideoContainer container;

        public LayoutManager(ZegoAudioVideoContainer container) {
            this.container = container;
        }

        public abstract void notifyDataSetChanged();

        public abstract void setConfig(ZegoLayoutConfig config);

        public abstract void setAudioVideoConfig(ZegoAudioVideoViewConfig audioVideoConfig);

        public abstract void onAudioVideoAvailable(List<ZegoUIKitUser> userList);

        public abstract void onAudioVideoUnAvailable(List<ZegoUIKitUser> userList);

        public abstract void onUserLeft(List<ZegoUIKitUser> userInfoList);

        public abstract void onUserJoined(List<ZegoUIKitUser> userInfoList);

        public abstract void onScreenSharingAvailable(List<ZegoUIKitUser> userInfoList);

        public abstract void onScreenSharingUnAvailable(List<ZegoUIKitUser> userInfoList);

        public abstract void onConfigurationChanged();
    }

    static class DefaultLayoutManager extends LayoutManager {

        public DefaultLayoutManager(ZegoAudioVideoContainer container) {
            super(container);
        }

        @Override
        public void setConfig(ZegoLayoutConfig config) {

        }

        @Override
        public void notifyDataSetChanged() {

        }

        @Override
        public void setAudioVideoConfig(@NonNull ZegoAudioVideoViewConfig audioVideoConfig) {

        }

        @Override
        public void onAudioVideoUnAvailable(List<ZegoUIKitUser> userList) {

        }

        @Override
        public void onAudioVideoAvailable(List<ZegoUIKitUser> userList) {

        }

        @Override
        public void onUserLeft(List<ZegoUIKitUser> userInfoList) {

        }

        @Override
        public void onUserJoined(List<ZegoUIKitUser> userInfoList) {

        }

        @Override
        public void onScreenSharingAvailable(List<ZegoUIKitUser> userInfoList) {

        }

        @Override
        public void onScreenSharingUnAvailable(List<ZegoUIKitUser> userInfoList) {

        }

        @Override
        public void onConfigurationChanged() {

        }
    }

}
