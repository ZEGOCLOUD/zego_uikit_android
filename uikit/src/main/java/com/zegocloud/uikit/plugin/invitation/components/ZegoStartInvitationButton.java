package com.zegocloud.uikit.plugin.invitation.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import java.util.ArrayList;
import java.util.List;

public class ZegoStartInvitationButton extends androidx.appcompat.widget.AppCompatTextView {

    public ZegoStartInvitationButton(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoStartInvitationButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZegoStartInvitationButton);
        type = a.getInt(R.styleable.ZegoStartInvitationButton_zego_uikit_type, 0);
        a.recycle();
        initView();
    }

    protected List<ZegoUIKitUser> invitees = new ArrayList<>();
    protected int type;
    protected String data = "";
    protected int timeout = 60;
    protected String callID = null;

    protected void initView() {
        setOnClickListener(null);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        View.OnClickListener onClickListener = new OnClickListener() {
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

    public void setData(String data) {
        this.data = data;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setType(ZegoInvitationType type) {
        this.type = type.getValue();
        if (type == ZegoInvitationType.VOICE_CALL) {
            setBackgroundResource(R.drawable.zego_uikit_icon_online_voice);
        } else {
            setBackgroundResource(R.drawable.zego_uikit_icon_online_video);
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setInvitees(List<ZegoUIKitUser> invitees) {
        this.invitees = invitees;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    protected void invokedWhenClick() {
        List<String> idList = GenericUtils.map(invitees, zegoUIKitUser -> zegoUIKitUser.userID);
        UIKitCore.getInstance().getSignalingPlugin().sendInvitation(idList, timeout, type, data, null);
    }
}
