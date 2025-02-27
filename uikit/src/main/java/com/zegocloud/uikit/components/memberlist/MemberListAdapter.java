package com.zegocloud.uikit.components.memberlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.components.audiovideo.ZegoCameraStateView;
import com.zegocloud.uikit.components.audiovideo.ZegoMicrophoneStateView;
import com.zegocloud.uikit.components.internal.RippleIconView;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitTranslationText;
import com.zegocloud.uikit.utils.Utils;
import java.util.ArrayList;
import java.util.List;

class MemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ZegoUIKitUser> uiKitUserList = new ArrayList<>();
    private ZegoMemberListItemViewProvider itemProvider;
    private boolean showMicrophoneState;
    private boolean showCameraState;

    public MemberListAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        Context context = parent.getContext();
        if (itemProvider != null) {
            view = itemProvider.onCreateView(parent);
        }
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.zego_uikit_item_inroom_member, parent, false);
            int height = Utils.dp2px(62, context.getResources().getDisplayMetrics());
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
        }
        return new ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZegoUIKitUser zegoUIKitUser = uiKitUserList.get(position);
        if (itemProvider != null) {
            itemProvider.onBindView(holder.itemView, zegoUIKitUser, position);
        } else {
            RippleIconView rippleIconView = holder.itemView.findViewById(R.id.member_icon);
            TextView memberName = holder.itemView.findViewById(R.id.member_name);
            rippleIconView.setText(zegoUIKitUser.userName, false);
            if (position == 0) {
                String tagString = "";
                UIKitTranslationText translationText = UIKitCore.getInstance().getTranslationText();
                if (translationText != null) {
                    tagString = translationText.UIKit_TAG_YOU;
                }
                memberName.setText(zegoUIKitUser.userName + tagString);
            } else {
                memberName.setText(zegoUIKitUser.userName);
            }
            ZegoMicrophoneStateView microphoneStateView = holder.itemView.findViewById(R.id.member_mic_state);
            microphoneStateView.setUserID(zegoUIKitUser.userID);

            microphoneStateView.setImageResource(R.drawable.zego_uikit_icon_member_mic_nor,
                R.drawable.zego_uikit_icon_member_mic_off,
                R.drawable.zego_uikit_icon_member_mic_speaking);
            ZegoCameraStateView cameraStateView = holder.itemView.findViewById(R.id.member_camera_state);
            cameraStateView.setUserID(zegoUIKitUser.userID);
            cameraStateView.setImageResource(R.drawable.zego_uikit_icon_member_camera,
                R.drawable.zego_uikit_icon_member_camera_off);
            if (showCameraState) {
                cameraStateView.setVisibility(View.VISIBLE);
            } else {
                cameraStateView.setVisibility(View.GONE);
            }
            if (showMicrophoneState) {
                microphoneStateView.setVisibility(View.VISIBLE);
            } else {
                microphoneStateView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return uiKitUserList.size();
    }

    public void setUserList(List<ZegoUIKitUser> userInfoList) {
        uiKitUserList.clear();
        uiKitUserList.addAll(userInfoList);

        notifyDataSetChanged();
    }

    public List<ZegoUIKitUser> getUiKitUserList() {
        return uiKitUserList;
    }

    public void setItemViewProvider(ZegoMemberListItemViewProvider provider) {
        itemProvider = provider;
    }

    public void setShowMicrophoneState(boolean showMicrophoneState) {
        this.showMicrophoneState = showMicrophoneState;
    }

    public void setShowCameraState(boolean showCameraState) {
        this.showCameraState = showCameraState;
    }
}
