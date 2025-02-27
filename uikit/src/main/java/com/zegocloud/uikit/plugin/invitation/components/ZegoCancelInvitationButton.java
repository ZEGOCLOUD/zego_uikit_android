package com.zegocloud.uikit.plugin.invitation.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.util.ArrayList;
import java.util.List;

public class ZegoCancelInvitationButton extends androidx.appcompat.widget.AppCompatTextView {

    public ZegoCancelInvitationButton(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoCancelInvitationButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected List<String> invitees = new ArrayList<>();

    protected void initView() {
        setOnClickListener(null);
        setBackgroundResource(R.drawable.zego_uikit_icon_hangup);
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

    public void setInvitees(List<String> invitees) {
        this.invitees = invitees;
    }

    protected void invokedWhenClick() {
        UIKitCore.getInstance().getSignalingPlugin().cancelInvitation(invitees, "",null,null);
    }
}
