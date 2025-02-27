package com.zegocloud.uikit.components.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.zegocloud.uikit.components.message.ZegoInRoomMessageState;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageListener;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.util.List;

public class ZegoInRoomChatView extends FrameLayout {

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private InRoomChatAdapter inRoomChatAdapter;
    private ZegoInRoomMessageListener inRoomMessageListener;
    private ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider;

    public ZegoInRoomChatView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoInRoomChatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZegoInRoomChatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        recyclerView = new RecyclerView(getContext());
        layoutManager = new LinearLayoutManager(getContext());
        inRoomChatAdapter = new InRoomChatAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(inRoomChatAdapter);
        addView(recyclerView);

        inRoomMessageListener = new ZegoInRoomMessageListener() {
            @Override
            public void onInRoomMessageReceived(List<ZegoInRoomMessage> messageList) {
                boolean reachBottom = !recyclerView.canScrollVertically(1);
                inRoomChatAdapter.onInRoomMessageReceived(messageList);
                if (reachBottom) {
                    layoutManager.scrollToPosition(inRoomChatAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onInRoomMessageSendingStateChanged(ZegoInRoomMessage inRoomMessage) {
                inRoomChatAdapter.onInRoomMessageSendingStateChanged(inRoomMessage);
                if (inRoomMessage.state == ZegoInRoomMessageState.IDLE) {
                    layoutManager.scrollToPosition(inRoomChatAdapter.getItemCount() - 1);
                }
            }
        };
        inRoomChatAdapter.setInRoomChatItemViewProvider(inRoomChatItemViewProvider);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public void setInRoomChatItemViewProvider(ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider) {
        this.inRoomChatItemViewProvider = inRoomChatItemViewProvider;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inRoomChatAdapter.onInRoomMessageReceived(UIKitCore.getInstance().getInRoomMessages());
        UIKitCore.getInstance().addInRoomMessageReceivedListenerInternal(inRoomMessageListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().removeInRoomMessageReceivedListenerInternal(inRoomMessageListener);
    }
}
