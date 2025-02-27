package com.zegocloud.uikit.components.audiovideocontainer;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.components.audiovideo.ZegoAudioVideoView;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoContainer.LayoutManager;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class PipLayoutManager extends LayoutManager {

    private ZegoLayoutPictureInPictureConfig mConfig = new ZegoLayoutPictureInPictureConfig();
    private ZegoAudioVideoViewConfig mAudioVideoConfig = new ZegoAudioVideoViewConfig();
    private ViewGroup smallViewParent;
    private ViewGroup fullViewParent;
    private int smallViewMaxCount = 3;
    private List<ZegoUIKitUser> audioVideoUserList = new ArrayList<>();

    public PipLayoutManager(ZegoAudioVideoContainer container) {
        super(container);
        container.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.zego_uikit_layout_pip, container, true);

        fullViewParent = view.findViewById(R.id.full_view_parent);
        smallViewParent = view.findViewById(R.id.small_view_parent);
    }

    private void switchChildView(ZegoAudioVideoView clickView) {
        MaterialCardView clickViewParent = (MaterialCardView) clickView.getParent();
        clickView.setOnClickListener(null);
        clickView.setClickable(false);
        int indexOfClickView = audioVideoUserList.indexOf(new ZegoUIKitUser(clickView.getUserID()));
        clickViewParent.removeView(clickView);
        ZegoAudioVideoView fullView = (ZegoAudioVideoView) fullViewParent.getChildAt(0);
        fullViewParent.removeView(fullView);

        clickViewParent.addView(fullView);
        fullView.setAudioViewBackgroundColor(mConfig.smallViewBackgroundColor);
        fullView.setAudioViewBackground(mConfig.smallViewBackgroundImage);

        fullView.setOnClickListener(v -> {
            if (mConfig.switchLargeOrSmallViewByClick) {
                switchChildView((ZegoAudioVideoView) v);
            }
        });
        fullViewParent.addView(clickView);
        clickView.setAudioViewBackgroundColor(mConfig.largeViewBackgroundColor);
        clickView.setAudioViewBackground(mConfig.largeViewBackgroundImage);

        audioVideoUserList.set(indexOfClickView, UIKitCore.getInstance().getUser(fullView.getUserID()));
        audioVideoUserList.set(0, UIKitCore.getInstance().getUser(clickView.getUserID()));
    }

    @Override
    public void setConfig(ZegoLayoutConfig config) {
        if (config == null) {
            this.mConfig = new ZegoLayoutPictureInPictureConfig();
        } else {
            this.mConfig = (ZegoLayoutPictureInPictureConfig) config;
        }
        updateGravity();

        for (int i = 0; i < fullViewParent.getChildCount(); i++) {
            ZegoAudioVideoView child = (ZegoAudioVideoView) fullViewParent.getChildAt(i);
            child.setAudioViewBackgroundColor(mConfig.largeViewBackgroundColor);
            child.setAudioViewBackground(mConfig.largeViewBackgroundImage);
        }

        for (int i = 0; i < smallViewParent.getChildCount(); i++) {
            MaterialCardView child = (MaterialCardView) smallViewParent.getChildAt(i);
            ZegoAudioVideoView audioVideoView = (ZegoAudioVideoView) child.getChildAt(0);
            audioVideoView.setAudioViewBackgroundColor(mConfig.smallViewBackgroundColor);
            audioVideoView.setAudioViewBackground(mConfig.smallViewBackgroundImage);
            child.setCardBackgroundColor(mConfig.smallViewBackgroundColor);

            DisplayMetrics displayMetrics = container.getContext().getResources().getDisplayMetrics();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) child.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(-2, -2);
            }

            int containerWidth = container.getWidth();
            int containerHeight = container.getHeight();
            if (containerWidth < containerHeight) {
                layoutParams.width = Utils.dp2px(mConfig.smallViewSize.getWidth(), displayMetrics);
                layoutParams.height = Utils.dp2px(mConfig.smallViewSize.getHeight(), displayMetrics);
            } else {
                layoutParams.width = Utils.dp2px(mConfig.smallViewSize.getHeight(), displayMetrics);
                layoutParams.height = Utils.dp2px(mConfig.smallViewSize.getWidth(), displayMetrics);
            }

            layoutParams.bottomMargin = Utils.dp2px(mConfig.spacingBetweenSmallViews, displayMetrics);
            child.setLayoutParams(layoutParams);
        }
    }

    private void notifyDataAdded(List<ZegoUIKitUser> userList) {
        List<ZegoUIKitUser> oldList = subListToMaxCount(audioVideoUserList);

        for (ZegoUIKitUser uiKitUser : userList) {
            if (!audioVideoUserList.contains(uiKitUser)) {
                audioVideoUserList.add(uiKitUser);
            }
        }
        List<ZegoUIKitUser> sortUsers = new ArrayList<>(audioVideoUserList);
        if (container.audioVideoComparator != null) {
            sortUsers = container.audioVideoComparator.sortAudioVideo(sortUsers);
        }
        audioVideoUserList.clear();
        audioVideoUserList.addAll(sortUsers);

        List<ZegoUIKitUser> newList = subListToMaxCount(sortUsers);

        boolean addOnTail = true;
        for (int i = 0; i < oldList.size(); i++) {
            if (oldList.get(i) != newList.get(i)) {
                addOnTail = false;
                break;
            }
        }
        if (addOnTail) {
            List<ZegoUIKitUser> addedList = newList.subList(oldList.size(), newList.size());
            for (ZegoUIKitUser uiKitUser : addedList) {
                if (fullViewParent.getChildCount() == 0) {
                    ZegoAudioVideoView audioVideoView = new ZegoAudioVideoView(fullViewParent.getContext());
                    audioVideoView.setForegroundViewProvider(container.getAudioVideoForegroundViewProvider());
                    audioVideoView.setAvatarViewProvider(container.getAvatarViewProvider());
                    audioVideoView.setAudioVideoConfig(mAudioVideoConfig);
                    audioVideoView.setUserID(uiKitUser.userID);
                    fullViewParent.addView(audioVideoView);
                } else {
                    ZegoAudioVideoView audioVideoView = new ZegoAudioVideoView(smallViewParent.getContext());
                    audioVideoView.setForegroundViewProvider(container.getAudioVideoForegroundViewProvider());
                    audioVideoView.setAvatarViewProvider(container.getAvatarViewProvider());
                    audioVideoView.setAudioVideoConfig(mAudioVideoConfig);
                    audioVideoView.setUserID(uiKitUser.userID);
                    audioVideoView.setOnClickListener(v -> {
                        if (mConfig.switchLargeOrSmallViewByClick) {
                            switchChildView((ZegoAudioVideoView) v);
                        }
                    });
                    smallViewParent.addView(wrapWithMaterialView(audioVideoView));
                }
            }
        } else {
            updatePipViewsFromUserList(newList, true);
        }
        setConfig(mConfig);

        setAudioVideoConfig(mAudioVideoConfig);
    }

    private void notifyDataRemoved(List<ZegoUIKitUser> userList) {
        List<ZegoUIKitUser> oldList = subListToMaxCount(audioVideoUserList);

        boolean needUpdateVideoList = false;
        for (ZegoUIKitUser uiKitUser : userList) {
            if (oldList.contains(uiKitUser)) {
                needUpdateVideoList = true;
                break;
            }
        }
        if (!needUpdateVideoList) {
            return;
        }

        audioVideoUserList.removeAll(userList);
        List<ZegoUIKitUser> sortUsers = new ArrayList<>(audioVideoUserList);
        if (container.audioVideoComparator != null) {
            sortUsers = container.audioVideoComparator.sortAudioVideo(sortUsers);
        }
        audioVideoUserList.clear();
        audioVideoUserList.addAll(sortUsers);

        List<ZegoUIKitUser> newList = subListToMaxCount(sortUsers);

        //only remove and no need to update all videos
        boolean removeOnly = true;

        Iterator<ZegoUIKitUser> iterator = oldList.iterator();
        while (iterator.hasNext()) {
            ZegoUIKitUser next = iterator.next();
            if (newList.contains(next)) {
                iterator.remove();
            }
        }
        if (oldList.size() == newList.size()) {
            for (int i = 0; i < newList.size(); i++) {
                if (oldList.get(i) != newList.get(i)) {
                    removeOnly = false;
                    break;
                }
            }
        } else {
            removeOnly = false;
        }

        if (removeOnly) {
            for (int i = 0; i < smallViewParent.getChildCount(); i++) {
                MaterialCardView child = (MaterialCardView) smallViewParent.getChildAt(i);
                ZegoAudioVideoView audioVideoView = (ZegoAudioVideoView) child.getChildAt(0);
                if (!newList.contains(new ZegoUIKitUser(audioVideoView.getUserID()))) {
                    audioVideoView.setUserID("");
                    child.removeView(audioVideoView);
                    smallViewParent.removeView(child);
                }
            }
            for (int i = 0; i < fullViewParent.getChildCount(); i++) {
                ZegoAudioVideoView audioVideoView = (ZegoAudioVideoView) fullViewParent.getChildAt(i);
                if (!newList.contains(new ZegoUIKitUser(audioVideoView.getUserID()))) {
                    fullViewParent.removeView(audioVideoView);
                }
            }
        } else {
            updatePipViewsFromUserList(newList, true);
        }

        setConfig(mConfig);

        setAudioVideoConfig(mAudioVideoConfig);
    }

    private List<ZegoUIKitUser> subListToMaxCount(List<ZegoUIKitUser> sortUsers) {
        List<ZegoUIKitUser> filteredUsers;
        if (sortUsers.size() > smallViewMaxCount + 1) {
            filteredUsers = new ArrayList<>(sortUsers.subList(0, smallViewMaxCount + 1));
        } else {
            filteredUsers = new ArrayList<>(sortUsers);
        }
        return filteredUsers;
    }

    @Override
    public void notifyDataSetChanged() {
        List<ZegoUIKitUser> sortUsers = new ArrayList<>(audioVideoUserList);
        if (container.audioVideoComparator != null) {
            sortUsers = container.audioVideoComparator.sortAudioVideo(sortUsers);
        }
        audioVideoUserList.clear();
        audioVideoUserList.addAll(sortUsers);

        List<ZegoUIKitUser> filteredUsers = subListToMaxCount(sortUsers);

        updatePipViewsFromUserList(filteredUsers, false);

        setConfig(mConfig);

        setAudioVideoConfig(mAudioVideoConfig);
    }

    private void updatePipViewsFromUserList(List<ZegoUIKitUser> uiKitUserList, boolean useCache) {
        List<ZegoAudioVideoView> resultViews = new ArrayList<>();
        Map<String, ZegoAudioVideoView> existedViewMap = new HashMap<>();
        if (useCache) {
            for (int i = 0; i < fullViewParent.getChildCount(); i++) {
                ZegoAudioVideoView audioVideoView = (ZegoAudioVideoView) fullViewParent.getChildAt(i);
                existedViewMap.put(audioVideoView.getUserID(), audioVideoView);
            }
        }
        fullViewParent.removeAllViews();
        if (useCache) {
            for (int i = 0; i < smallViewParent.getChildCount(); i++) {
                MaterialCardView child = (MaterialCardView) smallViewParent.getChildAt(i);
                ZegoAudioVideoView audioVideoView = (ZegoAudioVideoView) child.getChildAt(0);
                audioVideoView.setOnClickListener(null);
                audioVideoView.setClickable(false);
                child.removeView(audioVideoView);
                existedViewMap.put(audioVideoView.getUserID(), audioVideoView);
            }
        }
        smallViewParent.removeAllViews();

        for (ZegoUIKitUser filteredUser : uiKitUserList) {
            ZegoAudioVideoView audioVideoView = existedViewMap.remove(filteredUser.userID);
            if (audioVideoView != null) {
                resultViews.add(audioVideoView);
            } else {
                resultViews.add(new ZegoAudioVideoView(smallViewParent.getContext()));
            }
        }
        for (ZegoAudioVideoView value : existedViewMap.values()) {
            value.setUserID("");
        }

        for (int i = 0; i < resultViews.size(); i++) {
            ZegoAudioVideoView audioVideoView = resultViews.get(i);
            audioVideoView.setForegroundViewProvider(container.getAudioVideoForegroundViewProvider());
            audioVideoView.setAvatarViewProvider(container.getAvatarViewProvider());
            if (TextUtils.isEmpty(audioVideoView.getUserID())) {
                audioVideoView.setAudioVideoConfig(mAudioVideoConfig);
                audioVideoView.setUserID(uiKitUserList.get(i).userID);
            }
            if (i == 0) {
                fullViewParent.addView(audioVideoView);
            } else {
                audioVideoView.setOnClickListener(v -> {
                    if (mConfig.switchLargeOrSmallViewByClick) {
                        switchChildView((ZegoAudioVideoView) v);
                    }
                });
                smallViewParent.addView(wrapWithMaterialView(audioVideoView));
            }
        }
    }

    private MaterialCardView wrapWithMaterialView(View view) {
        MaterialCardView cardView = new MaterialCardView(container.getContext());
        DisplayMetrics displayMetrics = container.getContext().getResources().getDisplayMetrics();
        cardView.setRadius(Utils.dp2px(8f, displayMetrics));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
        params.gravity = Gravity.CENTER;
        cardView.addView(view, params);
        return cardView;
    }

    @Override
    public void setAudioVideoConfig(@NonNull ZegoAudioVideoViewConfig audioVideoConfig) {
        this.mAudioVideoConfig = audioVideoConfig;
        for (int i = 0; i < fullViewParent.getChildCount(); i++) {
            ZegoAudioVideoView child = (ZegoAudioVideoView) fullViewParent.getChildAt(i);
            child.setAudioVideoConfig(mAudioVideoConfig);
        }
        for (int i = 0; i < smallViewParent.getChildCount(); i++) {
            MaterialCardView child = (MaterialCardView) smallViewParent.getChildAt(i);
            ZegoAudioVideoView audioVideoView = (ZegoAudioVideoView) child.getChildAt(0);
            audioVideoView.setAudioVideoConfig(mAudioVideoConfig);
        }
    }

    @Override
    public void onAudioVideoAvailable(List<ZegoUIKitUser> userList) {
        notifyDataAdded(userList);
    }

    @Override
    public void onAudioVideoUnAvailable(List<ZegoUIKitUser> userList) {
        if (!mConfig.removeViewWhenAudioVideoUnavailable) {
            return;
        }
        notifyDataRemoved(userList);
    }

    @Override
    public void onUserLeft(List<ZegoUIKitUser> userInfoList) {
        notifyDataRemoved(userInfoList);
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
        notifyDataSetChanged();
    }

    private void updateGravity() {
        LayoutParams layoutParams = (LayoutParams) smallViewParent.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.gravity = getGravity(mConfig.smallViewDefaultPosition);
            smallViewParent.setLayoutParams(layoutParams);
        }
    }

    private int getGravity(ZegoViewPosition position) {
        int gravity = 0;
        if (position == ZegoViewPosition.TOP_RIGHT) {
            gravity = Gravity.TOP | Gravity.END;
        } else if (position == ZegoViewPosition.TOP_LEFT) {
            gravity = Gravity.TOP | Gravity.START;
        } else if (position == ZegoViewPosition.BOTTOM_LEFT) {
            gravity = Gravity.BOTTOM | Gravity.START;
        } else if (position == ZegoViewPosition.BOTTOM_RIGHT) {
            gravity = Gravity.BOTTOM | Gravity.END;
        }
        return gravity;
    }
}
