package com.zegocloud.uikit.components.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageListener;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.util.List;

public class ZegoInRoomMessageView extends FrameLayout {

    private ZegoInRoomMessageAdapter messageAdapter;
    private ZegoInRoomMessageListener inRoomMessageListener;
    private LinearLayoutManager layoutManager;

    public ZegoInRoomMessageView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoInRoomMessageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZegoInRoomMessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private RecyclerView recyclerView;

    public void initView() {
        recyclerView = new RecyclerView(getContext());
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new ZegoInRoomMessageAdapter();
        recyclerView.setAdapter(messageAdapter);
        layoutManager.scrollToPosition(messageAdapter.getItemCount());
        LayoutParams params = new LayoutParams(-1, -2);
        params.gravity = Gravity.BOTTOM;
        addView(recyclerView, params);
        inRoomMessageListener = new ZegoInRoomMessageListener() {
            @Override
            public void onInRoomMessageReceived(List<ZegoInRoomMessage> messageList) {
                if (messageAdapter != null) {
                    boolean reachBottom = !recyclerView.canScrollVertically(1);
                    messageAdapter.addMessages(messageList);
                    //                    if (reachBottom) {
                    layoutManager.scrollToPosition(messageAdapter.getItemCount() - 1);
                    //                    }
                }
            }

            @Override
            public void onInRoomMessageSendingStateChanged(ZegoInRoomMessage inRoomMessage) {
                if (inRoomMessage.state == ZegoInRoomMessageState.SUCCESS) {
                    if (messageAdapter != null) {
                        messageAdapter.addMessage(inRoomMessage);
                        layoutManager.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                }
            }
        };
    }

    public void setItemViewProvider(@Nullable ZegoInRoomMessageItemViewProvider provider) {
        messageAdapter.setItemViewProvider(provider);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        messageAdapter.addMessages(UIKitCore.getInstance().getInRoomMessages());
        UIKitCore.getInstance().addInRoomMessageReceivedListenerInternal(inRoomMessageListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().removeInRoomMessageReceivedListenerInternal(inRoomMessageListener);
    }

}
