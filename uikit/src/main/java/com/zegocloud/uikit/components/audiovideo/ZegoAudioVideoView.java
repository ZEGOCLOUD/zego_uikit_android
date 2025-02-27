package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoViewConfig;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoResourceMode;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoCameraStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserInRoomAttributesInfo;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import com.zegocloud.uikit.utils.Utils;
import im.zego.zegoexpress.constants.ZegoStreamResourceMode;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoPlayerConfig;
import java.util.List;
import java.util.Objects;

public class ZegoAudioVideoView extends FrameLayout {

    private String mUserID;
    private String roomID;
    private int audioViewBackgroundColor;
    private Drawable audioViewBackgroundImage;
    private ZegoCanvas canvas;
    private ZegoForegroundViewProvider zegoForegroundViewProvider;
    private ViewGroup foregroundView = new FrameLayout(getContext());
    private ViewGroup contentView = new FrameLayout(getContext());
    private ViewGroup backgroundView = new FrameLayout(getContext());
    private ZegoAvatarView zegoAvatarView = new ZegoAvatarView(getContext());
    private ZegoAudioVideoViewConfig audioVideoConfig = new ZegoAudioVideoViewConfig();
    private final ZegoCameraStateChangeListener cameraStateListener = this::onCameraStateChange;
    private final ZegoAudioVideoUpdateListener mediaUpdateListener = new ZegoAudioVideoUpdateListener() {
        @Override
        public void onAudioVideoAvailable(List<ZegoUIKitUser> userList) {
            onAudioVideoListUpdate(true, userList);
        }

        @Override
        public void onAudioVideoUnAvailable(List<ZegoUIKitUser> userList) {
            onAudioVideoListUpdate(false, userList);
        }
    };
    private ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener usersInRoomAttributesUpdateListener = new ZegoUIKitSignalingPluginUsersInRoomAttributesUpdateListener() {
        @Override
        public void onUsersInRoomAttributesUpdated(List<String> updateKeys,
            List<ZegoUserInRoomAttributesInfo> oldAttributes, List<ZegoUserInRoomAttributesInfo> attributes,
            ZegoUIKitUser editor) {
            for (ZegoUserInRoomAttributesInfo attribute : attributes) {
                if (Objects.equals(attribute.getUserID(), mUserID)) {
                    zegoAvatarView.updateUser(mUserID);
                }
            }
        }
    };
    ;
    private boolean isPreviewing;
    private boolean autoAvatarSize = true;
    private boolean isPlaying;

    public ZegoAudioVideoView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoAudioVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        addView(backgroundView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(contentView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(foregroundView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        zegoAvatarView = new ZegoAvatarView(getContext());
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = Gravity.CENTER;
        zegoAvatarView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UIKitCore.getInstance().addAudioVideoUpdateListenerInternal(mediaUpdateListener);
        UIKitCore.getInstance().addCameraStateListenerInternal(cameraStateListener);
        ZegoUIKit.getSignalingPlugin().addUsersInRoomAttributesUpdateListener(usersInRoomAttributesUpdateListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().removeAudioVideoUpdateListenerInternal(mediaUpdateListener);
        UIKitCore.getInstance().removeCameraStateListenerInternal(cameraStateListener);
        ZegoUIKit.getSignalingPlugin().removeUsersInRoomAttributesUpdateListener(usersInRoomAttributesUpdateListener);
    }

    private void onCameraStateChange(ZegoUIKitUser uiKitUser, boolean on) {
        if (Objects.equals(mUserID, uiKitUser.userID)) {
            if (UIKitCore.getInstance().isLocalUser(uiKitUser.userID)) {
                // is self device
                if (isShown()) {
                    if (on) {
                        startPreviewCamera();
                    } else {
                        stopPreviewCamera();
                    }
                }
            } else {
                // is remote user
                if (on) {
                    UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(uiKitUser.userID);
                    startPlayingRemoteView(coreUser.getMainStreamID());
                }
            }
            showTextureView(on);
        }
    }

    /**
     * @param userList
     */
    private void onAudioVideoListUpdate(boolean added, List<ZegoUIKitUser> userList) {
        // steam come/go  will accompany with Camera/Mic notify, showTextureView in them;
        for (ZegoUIKitUser uiKitUser : userList) {
            UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(uiKitUser.userID);
            if (Objects.equals(uiKitUser.userID, mUserID)) {
                String selfUserID = UIKitCore.getInstance().getLocalCoreUser().userID;
                if (Objects.equals(selfUserID, uiKitUser.userID)) {
                    // if self,don't play stream
                } else {
                    if (added) {
                        startPlayingRemoteView(coreUser.getMainStreamID());
                    }
                }
            }
        }
    }

    private void showTextureView(boolean show) {
        contentView.setVisibility(show ? VISIBLE : GONE);
    }

    public void setForegroundViewProvider(ZegoForegroundViewProvider zegoForegroundViewProvider) {
        this.zegoForegroundViewProvider = zegoForegroundViewProvider;
    }

    public void setAvatarViewProvider(ZegoAvatarViewProvider zegoAvatarViewProvider) {
        zegoAvatarView.setAvatarViewProvider(zegoAvatarViewProvider);
    }

    private void resetForeground() {
        foregroundView.removeAllViews();
    }

    private void resetBackground() {
        backgroundView.removeAllViews();
    }

    private void resetContent() {
        stopPreviewCamera();
        contentView.removeAllViews();
    }

    private void setForegroundSubView(UIKitCoreUser coreUser) {
        if (zegoForegroundViewProvider != null) {
            View child = getForegroundSubView();
            if (child != null) {
                if (Objects.equals(child.getTag(), coreUser.userID)) {
                    return;
                }
            }
            ZegoUIKitUser userInfo = coreUser.getUIKitUser();
            View subView = zegoForegroundViewProvider.getForegroundView(this, userInfo);
            if (subView != null) {
                subView.setTag(coreUser.userID);
                this.foregroundView.addView(subView);
            }
        }
    }

    private View getForegroundSubView() {
        return foregroundView.getChildAt(0);
    }

    private void setBackgroundSubView(UIKitCoreUser coreUser) {
        ZegoAvatarView avatarView = getBackgroundSubView();
        if (avatarView == null) {
            backgroundView.addView(zegoAvatarView);
        }
        zegoAvatarView.updateUser(coreUser.userID);
    }

    private ZegoAvatarView getBackgroundSubView() {
        View child = backgroundView.getChildAt(0);
        if (child != null) {
            return (ZegoAvatarView) child;
        } else {
            return null;
        }
    }

    private void setContentViewSubView(UIKitCoreUser coreUser) {
        View child = getContentViewSubView();
        if (child == null) {
            TextureView textureView = new TextureView(getContext());
            canvas = new ZegoCanvas(textureView);
            setVideoFillMode(audioVideoConfig);
            contentView.addView(textureView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (UIKitCore.getInstance().isLocalUser(coreUser.userID)) {
            if (coreUser.isCameraOpen) {
                startPreviewCamera();
            }
        } else {
            String streamID = UIKitCore.generateCameraStreamID(UIKitCore.getInstance().getRoom().roomID,
                coreUser.userID);
            if (coreUser.isCameraOpen || coreUser.isMicOpen) {
                startPlayingRemoteView(streamID);
            }
        }
        showTextureView(coreUser.isCameraOpen);
    }

    private void startPlayingRemoteView(String streamID) {
        isPlaying = true;
        ZegoAudioVideoResourceMode resourceMode = UIKitCore.getInstance().getAudioVideoResourceMode();
        if (resourceMode == null) {
            ZegoUIKit.startPlayingStream(streamID, canvas);
        } else {
            ZegoPlayerConfig config = new ZegoPlayerConfig();
            config.resourceMode = ZegoStreamResourceMode.getZegoStreamResourceMode(resourceMode.value());
            ZegoUIKit.startPlayingStream(streamID, canvas, config);
        }
    }

    private void stopPlayingRemoteView(String streamID) {
        isPlaying = false;
        ZegoUIKit.stopPlayingStream(streamID);
    }

    private TextureView getContentViewSubView() {
        View child = contentView.getChildAt(0);
        if (child != null) {
            return (TextureView) child;
        } else {
            return null;
        }
    }

    public void setUserID(String userID) {
        if (!Objects.equals(this.mUserID, userID)) {
            // first clear current state
            resetForeground();
            resetContent();
            resetBackground();
        }
        UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(userID);
        if (coreUser != null) {
            setBackgroundSubView(coreUser);
            setContentViewSubView(coreUser);
            setForegroundSubView(coreUser);
        }

        this.mUserID = userID;
    }

    private void startPreviewCamera() {
        ZegoUIKit.startPreview(canvas);
        isPreviewing = true;
    }

    private void stopPreviewCamera() {
        if (isPreviewing) {
            ZegoUIKit.stopPreview();
        }
        isPreviewing = false;
    }

    public String getUserID() {
        return mUserID;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (autoAvatarSize) {
            autoAvatarSize(Math.min(w, h) / 5);
        }
    }

    private void autoAvatarSize(int radius) {
        int minTextSize = Utils.sp2px(20, getResources().getDisplayMetrics());
        int maxTextSize = Utils.sp2px(32, getResources().getDisplayMetrics());
        int textSize = Math.min(Math.max(radius, minTextSize), maxTextSize);
        int rippleWidth = Math.max((int) Math.floor(radius / 10f), 3);
        ZegoAvatarView avatarView = getBackgroundSubView();
        if (avatarView != null) {
            avatarView.setRadius(radius);
            avatarView.setTextSize(textSize);
            avatarView.setRippleWidth(rippleWidth);
            avatarView.onSizeChanged(radius);
        }
    }

    /**
     * @param avatarSize  is for user avatar with ripple size
     * @param contentSize is for user avatar icon
     */
    public void setAvatarSize(int avatarSize, int contentSize) {
        autoAvatarSize = false;
        FrameLayout.LayoutParams layoutParams = (LayoutParams) zegoAvatarView.getLayoutParams();
        layoutParams.width = avatarSize;
        layoutParams.height = avatarSize;
        zegoAvatarView.setLayoutParams(layoutParams);
        zegoAvatarView.setRadius(contentSize / 2);
        int rippleWidth = (avatarSize - contentSize) / 6;
        zegoAvatarView.setRippleWidth(rippleWidth);
    }

    public void setAvatarAlignment(ZegoAvatarAlignment avatarAlignment) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) zegoAvatarView.getLayoutParams();
        if (avatarAlignment != null) {
            if (avatarAlignment == ZegoAvatarAlignment.CENTER) {
                layoutParams.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
            } else if (avatarAlignment == ZegoAvatarAlignment.START) {
                layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            } else {
                layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            }
        } else {
            layoutParams.gravity = Gravity.CENTER;
        }
        zegoAvatarView.setLayoutParams(layoutParams);
    }

    public void setSoundWaveColor(@ColorInt int color) {
        zegoAvatarView.setRippleColor(color);
    }

    public ZegoViewMode getVideoViewFillMode(boolean useVideoViewAspectFill) {
        VideoFillMode videoFillMode;
        if (useVideoViewAspectFill) {
            videoFillMode = VideoFillMode.ASPECT_FILL;
        } else {
            videoFillMode = VideoFillMode.ASPECT_FIT;
        }
        return ZegoViewMode.getZegoViewMode(videoFillMode.value());
    }

    public void setAudioViewBackgroundColor(@ColorInt int color) {
        this.audioViewBackgroundColor = color;
        backgroundView.setBackgroundColor(color);
    }

    public void setAudioViewBackground(Drawable drawable) {
        this.audioViewBackgroundImage = drawable;
        if (drawable == null) {
            setAudioViewBackgroundColor(audioViewBackgroundColor);
        } else {
            backgroundView.setBackground(drawable);
        }
    }

    public void setAudioVideoConfig(ZegoAudioVideoViewConfig audioVideoConfig) {
        boolean viewModeChanged =
            this.audioVideoConfig.useVideoViewAspectFill != audioVideoConfig.useVideoViewAspectFill;
        this.audioVideoConfig = audioVideoConfig;
        setVideoFillMode(audioVideoConfig);
        // if viewModeChanged, should restart preview or play to take effect
        if (viewModeChanged) {
            if (isPreviewing) {
                stopPreviewCamera();
                startPreviewCamera();
            } else if (isPlaying) {
                UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(mUserID);
                if (coreUser != null) {
                    String streamID = UIKitCore.generateCameraStreamID(UIKitCore.getInstance().getRoom().roomID,
                        coreUser.userID);
                    stopPlayingRemoteView(streamID);
                    if (coreUser.isCameraOpen || coreUser.isMicOpen) {
                        startPlayingRemoteView(streamID);
                    }
                }
            }
        }
        zegoAvatarView.setShowSoundWave(audioVideoConfig.showSoundWavesInAudioMode);
    }

    private void setVideoFillMode(ZegoAudioVideoViewConfig audioVideoConfig) {
        if (canvas != null) {
            ZegoViewMode videoViewFillMode = getVideoViewFillMode(audioVideoConfig.useVideoViewAspectFill);
            canvas.viewMode = videoViewFillMode;
        }
    }
}
