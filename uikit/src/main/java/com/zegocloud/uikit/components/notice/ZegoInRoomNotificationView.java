package com.zegocloud.uikit.components.notice;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserUpdateListener;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitTranslationText;
import com.zegocloud.uikit.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class ZegoInRoomNotificationView extends LinearLayout {

    int maxCount = 3;
    int itemMaxLine = 3;
    int itemDisappearTime = 3000;
    private List<InRoomNotification> inRoomNotifications = new ArrayList<>(maxCount);
    private ZegoUserUpdateListener userUpdateListener;
    private ZegoInRoomMessageListener inRoomMessageListener;
    private ZegoInRoomNotificationItemViewProvider inRoomNotificationItemViewProvider;
    private Runnable runnable;
    private ZegoInRoomNotificationViewConfig notificationViewConfig;

    public ZegoInRoomNotificationView(Context context) {
        super(context);
        initView();
    }

    public ZegoInRoomNotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZegoInRoomNotificationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(300);
        setLayoutTransition(layoutTransition);

        userUpdateListener = new ZegoUserUpdateListener() {
            @Override
            public void onUserJoined(List<ZegoUIKitUser> userInfoList) {
                for (ZegoUIKitUser zegoUIKitUser : userInfoList) {
                    InRoomNotification notification = new InRoomNotification(InRoomNotification.type_JOIN);
                    notification.uiKitUser = zegoUIKitUser;
                    addNotification(notification);
                }
            }

            @Override
            public void onUserLeft(List<ZegoUIKitUser> userInfoList) {
                for (ZegoUIKitUser zegoUIKitUser : userInfoList) {
                    InRoomNotification notification = new InRoomNotification(InRoomNotification.type_LEAVE);
                    notification.uiKitUser = zegoUIKitUser;
                    addNotification(notification);
                }
            }
        };

        inRoomMessageListener = new ZegoInRoomMessageListener() {
            @Override
            public void onInRoomMessageReceived(List<ZegoInRoomMessage> messageList) {
                for (ZegoInRoomMessage zegoInRoomMessage : messageList) {
                    InRoomNotification notification = new InRoomNotification(InRoomNotification.type_MESSAGE);
                    notification.inRoomMessage = zegoInRoomMessage;
                    addNotification(notification);
                }
            }

            @Override
            public void onInRoomMessageSendingStateChanged(ZegoInRoomMessage inRoomMessage) {
            }
        };
        runnable = () -> {
            setVisibility(View.GONE);
            removeAllViews();
        };

        notificationViewConfig = new ZegoInRoomNotificationViewConfig();
    }

    private void addNotification(InRoomNotification inRoomNotification) {
        if (inRoomNotifications.size() >= 3) {
            inRoomNotifications.remove(0);
        }
        inRoomNotifications.add(inRoomNotification);
        addNotificationView(inRoomNotification);

        if (getHandler() != null) {
            setVisibility(View.VISIBLE);
            getHandler().removeCallbacks(runnable);
            getHandler().postDelayed(runnable, itemDisappearTime);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UIKitCore.getInstance().addUserUpdateListenerInternal(userUpdateListener);
        UIKitCore.getInstance().addInRoomMessageReceivedListenerInternal(inRoomMessageListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().removeUserUpdateListenerInternal(userUpdateListener);
        UIKitCore.getInstance().removeInRoomMessageReceivedListenerInternal(inRoomMessageListener);
    }

    private void addNotificationView(InRoomNotification inRoomNotification) {
        if (getChildCount() >= maxCount) {
            removeViewAt(0);
        }
        View view = null;
        if (inRoomNotification.type == InRoomNotification.type_JOIN) {
            if (inRoomNotificationItemViewProvider == null
                || inRoomNotificationItemViewProvider.getJoinView(this, inRoomNotification.uiKitUser) == null) {
                TextView textView = new TextView(getContext());
                textView.setTextSize(14);
                int paddingStart = Utils.dp2px(12, getContext().getResources().getDisplayMetrics());
                int paddingTop = Utils.dp2px(8, getContext().getResources().getDisplayMetrics());
                textView.setPadding(paddingStart, paddingTop, paddingStart, paddingTop);
                UIKitTranslationText translationText = UIKitCore.getInstance().getTranslationText();
                if (translationText != null) {
                    textView.setText(
                        String.format(translationText.UIKit_USER_JOIN_ROOM, inRoomNotification.uiKitUser.userName));
                }
                textView.setTextColor(Color.WHITE);
                view = textView;
            } else {
                view = inRoomNotificationItemViewProvider.getJoinView(this, inRoomNotification.uiKitUser);
            }
        } else if (inRoomNotification.type == InRoomNotification.type_MESSAGE) {
            if (inRoomNotificationItemViewProvider == null
                || inRoomNotificationItemViewProvider.getMessageView(this, inRoomNotification.inRoomMessage) == null) {
                TextView textView = new TextView(getContext());
                textView.setTextSize(14);
                textView.setTextColor(Color.WHITE);
                int paddingStart = Utils.dp2px(12, getContext().getResources().getDisplayMetrics());
                int paddingTop = Utils.dp2px(8, getContext().getResources().getDisplayMetrics());
                textView.setPadding(paddingStart, paddingTop, paddingStart, paddingTop);
                ZegoUIKitUser uiKitUser = inRoomNotification.inRoomMessage.user;
                SpannableStringBuilder spannableString = new SpannableStringBuilder(
                    uiKitUser.userName + "\n" + inRoomNotification.inRoomMessage.message);
                int color = Color.parseColor("#FFB763");
                spannableString.setSpan(new ForegroundColorSpan(color), 0, uiKitUser.userName.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
                view = textView;
            } else {
                view = inRoomNotificationItemViewProvider.getMessageView(this, inRoomNotification.inRoomMessage);
            }
        } else {
            if (!notificationViewConfig.notifyUserLeave) {
                return;
            }
            if (inRoomNotificationItemViewProvider == null
                || inRoomNotificationItemViewProvider.getLeaveView(this, inRoomNotification.uiKitUser) == null) {
                TextView textView = new TextView(getContext());
                textView.setTextSize(14);
                int paddingStart = Utils.dp2px(12, getContext().getResources().getDisplayMetrics());
                int paddingTop = Utils.dp2px(8, getContext().getResources().getDisplayMetrics());
                textView.setPadding(paddingStart, paddingTop, paddingStart, paddingTop);
                UIKitTranslationText translationText = UIKitCore.getInstance().getTranslationText();
                if (translationText != null) {
                    textView.setText(
                        String.format(translationText.UIKit_USER_LEFT_ROOM, inRoomNotification.uiKitUser.userName));
                }
                textView.setTextColor(Color.WHITE);
                view = textView;
            } else {
                view = inRoomNotificationItemViewProvider.getLeaveView(this, inRoomNotification.uiKitUser);
            }
        }
        if (view != null) {
            LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
            int margin = Utils.dp2px(12, getContext().getResources().getDisplayMetrics());
            layoutParams.bottomMargin = margin;
            layoutParams.leftMargin = margin;
            view.setBackgroundResource(R.drawable.zego_uikit_inroom_notification_background);
            addView(view, layoutParams);
        }
    }

    public void setInRoomNotificationItemViewProvider(ZegoInRoomNotificationItemViewProvider provider) {
        inRoomNotificationItemViewProvider = provider;
    }

    public void setInRoomNotificationViewConfig(ZegoInRoomNotificationViewConfig config) {
        this.notificationViewConfig = config;
    }

}
