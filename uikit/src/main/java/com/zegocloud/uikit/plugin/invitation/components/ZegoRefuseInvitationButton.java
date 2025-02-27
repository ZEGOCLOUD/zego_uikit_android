package com.zegocloud.uikit.plugin.invitation.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.service.internal.UIKitCore;

public class ZegoRefuseInvitationButton extends androidx.appcompat.widget.AppCompatTextView {

    public ZegoRefuseInvitationButton(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoRefuseInvitationButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected String inviterID;

    protected void initView() {
        setOnClickListener(null);
        setBackgroundResource(R.drawable.zego_uikit_selector_dialog_voice_decline);
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
        UIKitCore.getInstance().getSignalingPlugin().refuseInvitation(inviterID, "", null);
    }
}
