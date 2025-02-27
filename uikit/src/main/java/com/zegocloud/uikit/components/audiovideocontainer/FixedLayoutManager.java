package com.zegocloud.uikit.components.audiovideocontainer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import com.google.android.material.card.MaterialCardView;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.components.audiovideo.ZegoAudioVideoView;
import com.zegocloud.uikit.components.audiovideo.ZegoScreenSharingView;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoContainer.LayoutManager;
import com.zegocloud.uikit.components.internal.RippleIconView;
import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import com.zegocloud.uikit.service.internal.UIKitTranslationText;
import com.zegocloud.uikit.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FixedLayoutManager extends LayoutManager {

    private final RippleIconView moreDown;
    private final RippleIconView moreUp;
    private final TextView moreText;
    private View moreView;
    private ConstraintLayout constraintLayout;
    private Flow flow;
    private ZegoLayoutGalleryConfig mConfig;
    private ZegoAudioVideoViewConfig mAudioVideoConfig;
    private final int maxCount = 8;
    private ZegoAudioVideoView[] audioVideoViews = new ZegoAudioVideoView[maxCount];
    private ZegoScreenSharingView[] screenSharingViews = new ZegoScreenSharingView[maxCount];
    private final int[] NO_IDS = new int[0];

    private String fullScreenShareUserID;
    private boolean fullMode;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int gap = Utils.dp2px(5f, container.getContext().getResources().getDisplayMetrics());

    public FixedLayoutManager(ZegoAudioVideoContainer container) {
        super(container);
        container.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.zego_uikit_layout_fixed, container, true);
        constraintLayout = view.findViewById(R.id.constraint_layout);
        constraintLayout.setBackgroundColor(Color.parseColor("#171821"));

        flow = constraintLayout.findViewById(R.id.flow);
        flow.setWrapMode(Flow.WRAP_ALIGNED);

        for (int i = 0; i < maxCount; i++) {
            audioVideoViews[i] = new ZegoAudioVideoView(container.getContext());
            screenSharingViews[i] = new ZegoScreenSharingView(container.getContext());
        }

        moreView = inflater.inflate(R.layout.zego_uikit_item_fixed_more, null, false);
        moreDown = moreView.findViewById(R.id.fixed_more_down);
        moreUp = moreView.findViewById(R.id.fixed_more_up);
        moreText = moreView.findViewById(R.id.fixed_more_text);
    }

    @Override
    public void setConfig(ZegoLayoutConfig config) {
        if (config == null) {
            mConfig = new ZegoLayoutGalleryConfig();
        } else {
            mConfig = (ZegoLayoutGalleryConfig) config;
        }
    }

    @Override
    public synchronized void notifyDataSetChanged() {
        // first remove all views
        int childCount = constraintLayout.getChildCount();
        for (int index = childCount - 1; index > 0; index--) {
            View child = constraintLayout.getChildAt(index);
            if (child instanceof MaterialCardView) {
                ((ViewGroup) child).removeAllViews();
            }
            constraintLayout.removeView(child);
        }
        flow.setReferencedIds(NO_IDS);
        flow.requestLayout();

        if (fullMode) {
            enterFullMode();
        } else {
            enterGalleryMode();
        }
    }

    private void enterGalleryMode() {
        List<ZegoUIKitUser> allUsers = UIKitCore.getInstance().getAllUsers();

        List<ZegoUIKitUser> sortedUsers = allUsers;
        // if removeViewWhenAudioVideoUnavailable ,only show  user has stream
        if (mConfig.removeViewWhenAudioVideoUnavailable) {
            sortedUsers = GenericUtils.filter(allUsers, uiKitUser -> {
                UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(uiKitUser.userID);
                return coreUser.hasStream();
            });
        }
        // sort
        if (container.audioVideoComparator != null) {
            sortedUsers = container.audioVideoComparator.sortAudioVideo(sortedUsers);
        }

        // calc max count and views
        List<ZegoUIKitUser> mainUsers = new ArrayList<>();
        List<ZegoUIKitUser> shareUsers = new ArrayList<>();
        for (int i = 0; i < sortedUsers.size(); i++) {
            ZegoUIKitUser uiKitUser = sortedUsers.get(i);
            UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(uiKitUser.userID);
            if (!TextUtils.isEmpty(coreUser.shareStreamID)) {
                shareUsers.add(uiKitUser);
            }
            if (mConfig.removeViewWhenAudioVideoUnavailable) {
                if (!TextUtils.isEmpty(coreUser.mainStreamID)) {
                    mainUsers.add(uiKitUser);
                }
            } else {
                mainUsers.add(uiKitUser);
            }
        }
        // if videoCount <= maxCount,show videoCount videos
        // if videoCount > maxCount,show maxCount-1 videos + moreView
        boolean showMoreView = shareUsers.size() + mainUsers.size() > maxCount;
        if (showMoreView) {
            mainUsers = mainUsers.subList(0, (maxCount - shareUsers.size() - 1));
        }
        Collections.reverse(shareUsers);

        for (int i = 0; i < shareUsers.size(); i++) {
            ZegoUIKitUser uiKitUser = shareUsers.get(i);
            ZegoScreenSharingView screenSharingView = screenSharingViews[i];
            screenSharingView.setFullScreen(false);
            screenSharingView.setForegroundViewProvider(container.getScreenShareForegroundViewProvider());
            screenSharingView.setUserID(uiKitUser.userID);

            ViewGroup cellView;
            if (mConfig.addBorderRadiusAndSpacingBetweenView && (shareUsers.size() + mainUsers.size()) > 1) {
                cellView = wrapWithMaterialView(screenSharingView);
            } else {
                screenSharingView.setScreenSharingViewBackgroundColor(Color.parseColor("#4A4B4D"));
                cellView = screenSharingView;
            }
            cellView.setId(View.generateViewId());
            constraintLayout.addView(cellView);
        }

        for (int i = 0; i < mainUsers.size(); i++) {
            ZegoUIKitUser uiKitUser = mainUsers.get(i);
            ZegoAudioVideoView audioVideoView = audioVideoViews[i];
            audioVideoView.setAudioVideoConfig(mAudioVideoConfig);
            audioVideoView.setForegroundViewProvider(container.getAudioVideoForegroundViewProvider());
            audioVideoView.setAvatarViewProvider(container.getAvatarViewProvider());
            audioVideoView.setUserID(uiKitUser.userID);

            ViewGroup cellView;
            if (mConfig.addBorderRadiusAndSpacingBetweenView && (shareUsers.size() + mainUsers.size()) > 1) {
                cellView = wrapWithMaterialView(audioVideoView);
            } else {
                cellView = audioVideoView;
                audioVideoView.setAudioViewBackgroundColor(Color.parseColor("#4A4B4D"));
            }
            cellView.setId(View.generateViewId());
            constraintLayout.addView(cellView);
        }
        // for more view:
        if (showMoreView) {
            moreDown.setText(sortedUsers.get(maxCount - 1).userName, false);
            moreUp.setText(sortedUsers.get(maxCount).userName, false);
            UIKitTranslationText translationText = UIKitCore.getInstance().getTranslationText();
            if (translationText != null) {
                int count = sortedUsers.size() - maxCount + 1;
                moreText.setText(String.format(translationText.UIKit_FIXEDLAYOUT_MORE, count));
            }
            if (mConfig.addBorderRadiusAndSpacingBetweenView) {
                constraintLayout.addView(wrapWithMaterialView(moreView));
            } else {
                moreView.setBackgroundColor(Color.parseColor("#4A4B4D"));
                constraintLayout.addView(moreView);
            }
        }

        int[] idList = new int[constraintLayout.getChildCount() - 1];
        for (int i = 0; i < constraintLayout.getChildCount(); i++) {
            View cellView = constraintLayout.getChildAt(i);
            if (cellView instanceof Flow) {
                continue;
            }
            idList[i - 1] = cellView.getId();
        }
        flow.setReferencedIds(idList);

        updateGallerySize();
    }

    private void updateGallerySize() {
        for (int i = 0; i < constraintLayout.getChildCount(); i++) {
            View cellView = constraintLayout.getChildAt(i);
            if (cellView instanceof Flow) {
                continue;
            }
            LayoutParams layoutParams = getCellLayoutParam(constraintLayout.getChildCount() - 1,
                mConfig.addBorderRadiusAndSpacingBetweenView, gap);
            cellView.setLayoutParams(layoutParams);
        }

        if (mConfig.addBorderRadiusAndSpacingBetweenView) {
            flow.setHorizontalGap(gap);
            flow.setVerticalGap(gap);
        } else {
            flow.setHorizontalGap(0);
            flow.setVerticalGap(0);
        }
        flow.requestLayout();
    }

    // full mode will display one screenShare stream,only show first one screenSharingView
    private void enterFullMode() {
        ZegoScreenSharingView screenSharingView = screenSharingViews[0];
        screenSharingView.setFullScreen(true);
        screenSharingView.setForegroundViewProvider(container.getScreenShareForegroundViewProvider());
        screenSharingView.setUserID(fullScreenShareUserID);
        ViewGroup cellView = wrapWithMaterialView(screenSharingView);
        cellView.setId(View.generateViewId());
        constraintLayout.addView(cellView);

        LayoutParams layoutParams = new LayoutParams(-1, -1);
        cellView.setLayoutParams(layoutParams);
    }

    @NonNull
    private MaterialCardView wrapWithMaterialView(View view) {
        MaterialCardView cardView = new MaterialCardView(container.getContext());
        cardView.setRadius(Utils.dp2px(6f, container.getContext().getResources().getDisplayMetrics()));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
        params.gravity = Gravity.CENTER;
        cardView.addView(view, params);
        cardView.setCardBackgroundColor(Color.parseColor("#4A4B4D"));
        return cardView;
    }

    private void applyFlowParams(int userCount, boolean showGap, int gap) {
        if (isCurrentLandscape()) {
            flow.setMaxElementsWrap(2);
        } else {
            if (userCount <= 2) {
                flow.setMaxElementsWrap(1);
            } else {
                flow.setMaxElementsWrap(2);
            }
        }

        if (showGap) {
            flow.setHorizontalGap(gap);
            flow.setVerticalGap(gap);
        } else {
            flow.setHorizontalGap(0);
            flow.setVerticalGap(0);
        }
    }

    private boolean isCurrentLandscape() {
        boolean isLandscape = false;
        if (container.getContext() instanceof Activity) {
            int rotation = ((Activity) container.getContext()).getWindowManager().getDefaultDisplay().getRotation();
            if (Surface.ROTATION_0 == rotation || Surface.ROTATION_180 == rotation) {
                isLandscape = false;
            } else {
                isLandscape = true;
            }
        }
        return isLandscape;
    }

    private LayoutParams getCellLayoutParam(int userCount) {
        LayoutParams layoutParams;
        if (userCount == 1) {
            layoutParams = new LayoutParams(-1, -1);
        } else {
            layoutParams = new LayoutParams(-1, -1);
            if (userCount == 2) {
                layoutParams.width = container.getWidth();
                layoutParams.height = (int) (container.getHeight() * 0.5f);
            } else if (userCount <= 4) {
                layoutParams.width = (int) (container.getWidth() * 0.5f);
                layoutParams.height = (int) (container.getHeight() * 0.5f);
            } else if (userCount <= 6) {
                layoutParams.width = (int) (container.getWidth() * 0.5f);
                layoutParams.height = (int) (container.getHeight() * 0.33f);
            } else {
                layoutParams.width = (int) (container.getWidth() * 0.5f);
                layoutParams.height = (int) (container.getHeight() * 0.25f);
            }
        }
        return layoutParams;
    }

    private LayoutParams getCellLayoutParam(int userCount, int gap) {
        LayoutParams layoutParams;
        if (userCount <= 1) {
            layoutParams = new LayoutParams(-1, -1);
        } else {
            int cellWidth;
            int cellHeight;
            int columns;
            int rows;
            if (isCurrentLandscape()) {
                if (userCount <= 2) {
                    // 1 row
                    rows = 1;
                } else {
                    // 2 rows
                    rows = 2;
                }
                cellHeight = (container.getHeight() - gap * (rows + 1)) / rows;

                if (userCount > 6) {
                    // 4 columns
                    columns = 4;
                } else if (userCount > 4) {
                    // 3 columns
                    columns = 3;
                } else {
                    // 2 columns
                    columns = 2;
                }
                cellWidth = (container.getWidth() - gap * (columns + 1)) / columns;

            } else {
                if (userCount <= 2) {
                    // 1 columns
                    columns = 1;
                } else {
                    // 2 columns
                    columns = 2;
                }
                cellWidth = (container.getWidth() - gap * (columns + 1)) / columns;

                if (userCount > 6) {
                    // 4 rows
                    rows = 4;
                } else if (userCount > 4) {
                    // 3 rows
                    rows = 3;
                } else {
                    // 2 rows
                    rows = 2;
                }
                cellHeight = (container.getHeight() - gap * (rows + 1)) / rows;
            }
            layoutParams = new LayoutParams(cellWidth, cellHeight);
        }
        return layoutParams;
    }

    private LayoutParams getCellLayoutParam(int userCount, boolean showGap, int gap) {
        if (showGap) {
            return getCellLayoutParam(userCount, gap);
        } else {
            return getCellLayoutParam(userCount);
        }
    }

    @Override
    public void setAudioVideoConfig(@NonNull ZegoAudioVideoViewConfig audioVideoConfig) {
        this.mAudioVideoConfig = audioVideoConfig;
        for (int referencedId : flow.getReferencedIds()) {
            View view = constraintLayout.findViewById(referencedId);
            if (view instanceof MaterialCardView) {
                View child = ((MaterialCardView) view).getChildAt(0);
                if (child instanceof ZegoAudioVideoView) {
                    ((ZegoAudioVideoView) child).setAudioVideoConfig(audioVideoConfig);
                }
            } else if (view instanceof ZegoAudioVideoView) {
                ((ZegoAudioVideoView) view).setAudioVideoConfig(audioVideoConfig);
            }
        }
    }

    @Override
    public void onAudioVideoUnAvailable(List<ZegoUIKitUser> userList) {
        if (mConfig.removeViewWhenAudioVideoUnavailable) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void onAudioVideoAvailable(List<ZegoUIKitUser> userList) {
        notifyDataSetChanged();
    }

    @Override
    public void onUserLeft(List<ZegoUIKitUser> userInfoList) {
        notifyDataSetChanged();
    }

    @Override
    public void onUserJoined(List<ZegoUIKitUser> userInfoList) {
        if (!mConfig.removeViewWhenAudioVideoUnavailable) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void onScreenSharingAvailable(List<ZegoUIKitUser> userInfoList) {
        String shareUserID = userInfoList.get(0).userID;
        if (Objects.equals(ZegoUIKit.getLocalUser().userID, shareUserID)) {
            fullScreenShareUserID = shareUserID;
            fullMode = true;
            notifyDataSetChanged();
        } else {
            if (mConfig.showNewScreenSharingViewInFullscreenMode) {
                if (!fullMode) {
                    fullScreenShareUserID = shareUserID;
                    fullMode = true;
                    notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onScreenSharingUnAvailable(List<ZegoUIKitUser> userInfoList) {
        for (ZegoUIKitUser uiKitUser : userInfoList) {
            if (Objects.equals(uiKitUser.userID, fullScreenShareUserID)) {
                fullScreenShareUserID = null;
                fullMode = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onConfigurationChanged() {
        updateGallerySize();
    }

    public void showScreenSharingViewInFullscreenMode(String userID, boolean fullscreen) {
        fullScreenShareUserID = userID;
        fullMode = fullscreen;
        notifyDataSetChanged();
    }

    public boolean isScreenSharingViewInFullscreenMode(String userID) {
        return fullMode && Objects.equals(fullScreenShareUserID, userID);
    }
}
