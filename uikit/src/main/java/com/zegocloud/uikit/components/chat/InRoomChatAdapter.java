package com.zegocloud.uikit.components.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.components.internal.RippleIconView;
import com.zegocloud.uikit.components.message.ZegoInRoomMessageState;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class InRoomChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ZegoInRoomMessage> messageList = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.ENGLISH);
    private ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (inRoomChatItemViewProvider != null) {
            view = inRoomChatItemViewProvider.onCreateView(parent);
        }
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zego_uikit_item_inroom_chat, parent, false);
        }
        return new ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZegoInRoomMessage message = messageList.get(position);
        if (inRoomChatItemViewProvider != null) {
            inRoomChatItemViewProvider.onBindView(holder.itemView, message);
        } else {
            RippleIconView rippleIconView;
            TextView chatName;
            TextView chatTime;
            TextView chatMessage;
            ImageView status;
            Group sendLayout = holder.itemView.findViewById(R.id.chat_send_widget);
            Group recvLayout = holder.itemView.findViewById(R.id.chat_recv_widget);
            if (UIKitCore.getInstance().isLocalUser(message.user.userID)) {
                sendLayout.setVisibility(View.VISIBLE);
                recvLayout.setVisibility(View.GONE);
                rippleIconView = holder.itemView.findViewById(R.id.chat_send_icon);
                chatName = holder.itemView.findViewById(R.id.chat_send_name);
                chatTime = holder.itemView.findViewById(R.id.chat_send_time);
                chatMessage = holder.itemView.findViewById(R.id.chat_send_message);
                status = holder.itemView.findViewById(R.id.chat_send_status);
            } else {
                sendLayout.setVisibility(View.GONE);
                recvLayout.setVisibility(View.VISIBLE);

                rippleIconView = holder.itemView.findViewById(R.id.chat_recv_icon);
                chatName = holder.itemView.findViewById(R.id.chat_recv_name);
                chatTime = holder.itemView.findViewById(R.id.chat_recv_time);
                chatMessage = holder.itemView.findViewById(R.id.chat_recv_message);
                status = holder.itemView.findViewById(R.id.chat_recv_status);
            }
            rippleIconView.setText(message.user.userName, false);
            chatName.setText(message.user.userName);
            chatTime.setText(sdf.format(new Date(message.timestamp)));
            chatMessage.setText(message.message);
            if (message.state == ZegoInRoomMessageState.SENDING) {
                status.setImageResource(R.drawable.zego_uikit_rotate_drawable);
                status.setVisibility(View.VISIBLE);
            } else if (message.state == ZegoInRoomMessageState.FAILED) {
                status.setImageResource(R.drawable.zego_uikit_chat_send_fail);
                status.setVisibility(View.VISIBLE);
            } else {
                status.setVisibility(View.GONE);
            }
            status.setOnClickListener(v -> {
                if (message.state == ZegoInRoomMessageState.FAILED) {
                    messageList.remove(message);
                    UIKitCore.getInstance().resendInRoomMessage(message, null);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void onInRoomMessageReceived(List<ZegoInRoomMessage> messageList) {
        int size = this.messageList.size();
        this.messageList.addAll(messageList);
        notifyItemRangeInserted(size, messageList.size());
    }

    public void onInRoomMessageSendingStateChanged(ZegoInRoomMessage inRoomMessage) {
        if (inRoomMessage.state == ZegoInRoomMessageState.IDLE) {
            int size = this.messageList.size();
            this.messageList.add(inRoomMessage);
            notifyItemInserted(size);
        } else {
            int index = this.messageList.indexOf(inRoomMessage);
            notifyItemChanged(index);
        }
    }

    public void setInRoomChatItemViewProvider(ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider) {
        this.inRoomChatItemViewProvider = inRoomChatItemViewProvider;
    }
}
