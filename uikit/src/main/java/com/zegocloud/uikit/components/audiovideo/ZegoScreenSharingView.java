package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.service.defines.ZegoScreenSharingUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import com.zegocloud.uikit.service.internal.UIKitTranslationText;
import com.zegocloud.uikit.utils.Utils;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCanvas;
import java.util.List;
import java.util.Objects;

public class ZegoScreenSharingView extends FrameLayout {

    private ZegoForegroundViewProvider zegoForegroundViewProvider;
    private ViewGroup foregroundView = new FrameLayout(getContext());
    private ViewGroup contentView = new FrameLayout(getContext());
    private ViewGroup backgroundView = new FrameLayout(getContext());
    private boolean useVideoViewAspectFill = false;
    private String userID;
    private TextureView textureView;
    private LinearLayout stopShareView;
    private boolean isFullScreen;

    protected GestureDetectorCompat gestureDetectorCompat;
    private long lastClickTime = 0;
    private static final int CLICK_INTERVAL = 200;

    private ZegoScreenSharingUpdateListener screenSharingUpdateListener = new ZegoScreenSharingUpdateListener() {
        @Override
        public void onScreenSharingAvailable(List<ZegoUIKitUser> userList) {
            for (ZegoUIKitUser uiKitUser : userList) {
                if (Objects.equals(uiKitUser.userID, userID)) {
                    UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(userID);
                    ZegoCanvas canvas = new ZegoCanvas(textureView);
                    canvas.viewMode = getVideoViewAspectFill(useVideoViewAspectFill);
                    ZegoUIKit.startPlayingStream(coreUser.shareStreamID, canvas);
                }
            }
        }

        @Override
        public void onScreenSharingUnAvailable(List<ZegoUIKitUser> userList) {

        }
    };
    private int screenSharingViewBackgroundColor;


    public ZegoScreenSharingView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoScreenSharingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZegoScreenSharingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        addView(backgroundView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(contentView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(foregroundView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        textureView = new TextureView(getContext());
        textureView.setLayoutParams(
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        contentView.addView(textureView);

        stopShareView = new LinearLayout(getContext());
        stopShareView.setOrientation(LinearLayout.VERTICAL);
        stopShareView.setGravity(Gravity.CENTER);
        TextView stopTips = new TextView(getContext());
        UIKitTranslationText translationText = UIKitCore.getInstance().getTranslationText();
        if (translationText != null) {
            stopTips.setText(translationText.UIKit_SHAREING_SCREEN_TIPS);
        }
        stopTips.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        stopTips.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams tipsParam = new LinearLayout.LayoutParams(-2, -2);
        stopShareView.addView(stopTips, tipsParam);

        TextView stopButton = new TextView(getContext());
        if (translationText != null) {
            stopButton.setText(translationText.UIKit_STOP_SHARE);
        }
        stopButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        stopButton.setTextColor(Color.parseColor("#ff4a50"));
        stopButton.setBackgroundResource(R.drawable.zego_uikit_bg_stop_share_button);
        int top = Utils.dp2px(8, getContext().getResources().getDisplayMetrics());
        int left = Utils.dp2px(12, getContext().getResources().getDisplayMetrics());
        stopButton.setPadding(left, top, left, top);
        LinearLayout.LayoutParams stopButtonParam = new LinearLayout.LayoutParams(-2, -2);
        stopButtonParam.topMargin = Utils.dp2px(20, getContext().getResources().getDisplayMetrics());
        stopShareView.addView(stopButton, stopButtonParam);
        stopButton.setOnClickListener(v -> {
            ZegoUIKit.stopSharingScreen();
        });
        LayoutParams stopShareViewParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        stopShareViewParam.gravity = Gravity.CENTER;
        stopShareView.setLayoutParams(stopShareViewParam);

        gestureDetectorCompat = new GestureDetectorCompat(getContext(), new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (System.currentTimeMillis() - lastClickTime < CLICK_INTERVAL) {
                    return true;
                }
                performClick();
                lastClickTime = System.currentTimeMillis();
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UIKitCore.getInstance().addScreenSharingUpdateListenerInternal(screenSharingUpdateListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().removeScreenSharingUpdateListenerInternal(screenSharingUpdateListener);
    }

    public void setUserID(String userID) {
        this.userID = userID;
        UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(userID);

        if (coreUser != null) {
            if (coreUser.userID.equals(ZegoUIKit.getLocalUser().userID)) {
                showStopShareView();
            } else {
                showShareStreamView();
                String streamID = UIKitCore.generateScreenShareStreamID(UIKitCore.getInstance().getRoom().roomID,
                    coreUser.userID);
                ZegoCanvas canvas = new ZegoCanvas(textureView);
                canvas.viewMode = getVideoViewAspectFill(useVideoViewAspectFill);
                ZegoUIKit.startPlayingStream(streamID, canvas);
            }
            if (zegoForegroundViewProvider != null) {
                resetForeground();
                ZegoUIKitUser userInfo = coreUser.getUIKitUser();
                View subView = zegoForegroundViewProvider.getForegroundView(this, userInfo);
                if (subView != null) {
                    subView.setTag(coreUser.userID);
                    this.foregroundView.addView(subView);
                }
            }
        } else {
            contentView.removeAllViews();
            resetForeground();
        }
    }

    public String getUserID() {
        return userID;
    }

    private void resetForeground() {
        foregroundView.removeAllViews();
    }

    private void showShareStreamView() {
        contentView.removeAllViews();
        contentView.addView(textureView);
    }

    private void showStopShareView() {
        contentView.removeAllViews();
        contentView.addView(stopShareView);
    }

    private View getForegroundSubView() {
        return foregroundView.getChildAt(0);
    }

    public void setForegroundViewProvider(ZegoForegroundViewProvider zegoForegroundViewProvider) {
        this.zegoForegroundViewProvider = zegoForegroundViewProvider;
    }

    public ZegoViewMode getVideoViewAspectFill(boolean use) {
        VideoFillMode mode;
        if (use) {
            mode = VideoFillMode.ASPECT_FILL;
        } else {
            mode = VideoFillMode.ASPECT_FIT;
        }
        return ZegoViewMode.getZegoViewMode(mode.value());
    }

    public void setScreenSharingViewBackgroundColor(@ColorInt int color) {
        this.screenSharingViewBackgroundColor = color;
        backgroundView.setBackgroundColor(color);
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }
}
