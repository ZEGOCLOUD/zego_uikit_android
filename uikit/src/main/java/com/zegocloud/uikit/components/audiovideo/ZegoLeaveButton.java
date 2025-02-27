package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.components.internal.BaseView;

public class ZegoLeaveButton extends BaseView {


    public ZegoLeaveButton(@NonNull Context context) {
        super(context);
    }

    public ZegoLeaveButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView(Context context) {
        super.initView(context);
        setIcon(R.drawable.zego_uikit_icon_hangup);
        setOnClickListener(null);
    }

    @Override
    public void initWidgetListener() {
    }

    @Override
    protected void unInitWidgetListener() {

    }

    @Override
    public void invokedWhenClick() {
        leaveRoom();
    }

    private void leaveRoom() {
        UIKitCore.getInstance().leaveRoom();
    }


    public void setIconLeave(@DrawableRes int iconLeave) {
        setIcon(iconLeave);
    }

    public void setIcon(@DrawableRes int iconLeave) {
        setImageResource(iconLeave);
    }
}
