package com.zegocloud.uikit.components.memberlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserCountOrPropertyChangedListener;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.util.List;

public class ZegoMemberList extends FrameLayout {

    private MemberListAdapter listAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ZegoUserCountOrPropertyChangedListener userCountOrPropertyChangedListener;
    private ZegoMemberListComparator memberListComparator;

    private boolean showMicrophoneState;
    private boolean showCameraState;


    public ZegoMemberList(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoMemberList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZegoMemberList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        recyclerView = new RecyclerView(getContext());
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new MemberListAdapter();

        updateList(UIKitCore.getInstance().getAllUsers());

        recyclerView.setAdapter(listAdapter);
        addView(recyclerView);

        userCountOrPropertyChangedListener = new ZegoUserCountOrPropertyChangedListener() {
            @Override
            public void onUserCountOrPropertyChanged(List<ZegoUIKitUser> userList) {
                updateList(userList);
            }
        };
    }

    public void setItemViewProvider(@Nullable ZegoMemberListItemViewProvider provider) {
        listAdapter.setItemViewProvider(provider);
    }

    public void setShowMicrophoneState(boolean showMicrophoneState) {
        this.showMicrophoneState = showMicrophoneState;
        if (listAdapter != null) {
            listAdapter.setShowMicrophoneState(showMicrophoneState);
        }
    }

    private void updateList(List<ZegoUIKitUser> uiKitUserList) {
        if (memberListComparator != null) {
            List<ZegoUIKitUser> list = memberListComparator.sortUserList(uiKitUserList);
            listAdapter.setUserList(list);
        } else {
            listAdapter.setUserList(uiKitUserList);
        }
    }

    public void notifyDataSetChanged() {
        if (listAdapter != null) {
            if (memberListComparator != null) {
                List<ZegoUIKitUser> uiKitUserList = listAdapter.getUiKitUserList();
                List<ZegoUIKitUser> list = memberListComparator.sortUserList(uiKitUserList);
                listAdapter.setUserList(list);
            } else {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setMemberListComparator(ZegoMemberListComparator memberListComparator) {
        this.memberListComparator = memberListComparator;
        updateList(UIKitCore.getInstance().getAllUsers());
    }

    public void setShowCameraState(boolean showCameraState) {
        this.showCameraState = showCameraState;
        if (listAdapter != null) {
            listAdapter.setShowCameraState(showCameraState);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UIKitCore.getInstance().addUserCountOrPropertyChangedListenerInternal(userCountOrPropertyChangedListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UIKitCore.getInstance().addUserCountOrPropertyChangedListenerInternal(userCountOrPropertyChangedListener);
    }
}
