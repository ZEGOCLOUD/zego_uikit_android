package com.zegocloud.uikit.components.message;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import com.zegocloud.uikit.service.internal.UIKitTranslationText;
import com.zegocloud.uikit.utils.Utils;
import java.util.ArrayList;
import java.util.List;

class ZegoInRoomMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ZegoInRoomMessage> messageList = new ArrayList<>();
    private ZegoInRoomMessageItemViewProvider itemViewProvider;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (itemViewProvider != null) {
            view = itemViewProvider.onCreateView(parent);
        }
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zego_uikit_item_inroom_message, parent, false);
        }
        return new ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZegoInRoomMessage inRoomMessage = messageList.get(position);
        if (itemViewProvider != null) {
            itemViewProvider.onBindView(holder.itemView, inRoomMessage, position);
        } else {
            UIKitCoreUser coreUser = UIKitCore.getInstance().getUserbyUserID(inRoomMessage.user.userID);
            //            boolean isHostMessage = coreUser.isCameraOpen || coreUser.isMicOpen;
            boolean isHostMessage = false;
            String fromUserName = inRoomMessage.user.userName;
            String content = inRoomMessage.message;
            Context context = holder.itemView.getContext();
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

            StringBuilder builder = new StringBuilder();
            UIKitTranslationText translationText = UIKitCore.getInstance().getTranslationText();
            String hostString = "";
            if (translationText != null) {
                hostString = translationText.UIKit_InRoomMessage_Host;
            }
            if (isHostMessage) {
                builder.append(hostString);
                builder.append(" ");
            }
            builder.append(fromUserName);
            builder.append(" ");
            builder.append(content);
            String source = builder.toString();
            SpannableString string = new SpannableString(source);
            RoundBackgroundColorSpan backgroundColorSpan = new RoundBackgroundColorSpan(context,
                ContextCompat.getColor(context, R.color.zego_uikit_purple_dark),
                ContextCompat.getColor(context, android.R.color.white));
            if (isHostMessage) {
                AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(Utils.sp2px(10, displayMetrics));
                string.setSpan(absoluteSizeSpan, 0, hostString.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                string.setSpan(backgroundColorSpan, 0, hostString.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.zego_uikit_teal));
            int indexOfUser = source.indexOf(fromUserName);
            string.setSpan(foregroundColorSpan, indexOfUser, indexOfUser + fromUserName.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(Utils.sp2px(13, displayMetrics));
            string.setSpan(absoluteSizeSpan, indexOfUser, indexOfUser + fromUserName.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            TextView textView = holder.itemView.findViewById(R.id.tv_inroom_message);
            textView.setText(string);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessages(List<ZegoInRoomMessage> list) {
        int size = this.messageList.size();
        this.messageList.addAll(list);
        notifyItemRangeInserted(size, list.size());
    }

    public void addMessage(ZegoInRoomMessage message) {
        int size = this.messageList.size();
        this.messageList.add(message);
        notifyItemInserted(size);
    }

    public void setItemViewProvider(ZegoInRoomMessageItemViewProvider provider) {
        itemViewProvider = provider;
    }
}
