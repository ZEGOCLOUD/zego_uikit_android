package com.zegocloud.uikit.plugin.invitation.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.service.internal.UIKitCore;

public class ZegoAcceptInvitationButton extends androidx.appcompat.widget.AppCompatTextView {

    public ZegoAcceptInvitationButton(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoAcceptInvitationButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected String inviterID;

    protected void initView() {
        setOnClickListener(null);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l != null) {
                    l.onClick(v);
                }
                invokedWhenClick();
            }
        };
        super.setOnClickListener(onClickListener);
    }

    public void setInviterID(String inviterID) {
        this.inviterID = inviterID;
    }

    protected void invokedWhenClick() {
        UIKitCore.getInstance().getSignalingPlugin().acceptInvitation(inviterID, "", null);
    }
}
