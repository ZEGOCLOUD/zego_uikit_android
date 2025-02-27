package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.components.internal.BaseView;
import com.zegocloud.uikit.service.internal.UIKitCore;

public class ZegoSwitchCameraButton extends BaseView {

    private Drawable frontDrawable;
    private Drawable backDrawable;

    public ZegoSwitchCameraButton(@NonNull Context context) {
        super(context);
    }

    public ZegoSwitchCameraButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(Context context) {
        super.initView(context);
        setImageResource(R.drawable.zego_uikit_icon_camera_flip, R.drawable.zego_uikit_icon_camera_flip);
        setOnClickListener(null);
        update();
    }

    @Override
    public void initWidgetListener() {
    }

    @Override
    protected void unInitWidgetListener() {

    }

    @Override
    public void invokedWhenClick() {
        boolean useFrontCamera = UIKitCore.getInstance().isUseFrontCamera();
        UIKitCore.getInstance().useFrontFacingCamera(!useFrontCamera);

        update();
    }

    private void update() {
        boolean useFrontCamera = UIKitCore.getInstance().isUseFrontCamera();
        if (useFrontCamera) {
            setImageDrawable(frontDrawable);
        } else {
            setImageDrawable(backDrawable);
        }
    }

    public void setImageResource(@DrawableRes int iconFrontFacingCamera, @DrawableRes int iconBackFacingCamera) {
        setIcon(iconFrontFacingCamera, iconBackFacingCamera);
    }

    public void setIcon(@DrawableRes int iconCamera) {
        setIcon(iconCamera, iconCamera);
    }

    public void setIcon(@DrawableRes int iconFrontFacingCamera, @DrawableRes int iconBackFacingCamera) {
        this.frontDrawable = ContextCompat.getDrawable(getContext(), iconFrontFacingCamera);
        this.backDrawable = ContextCompat.getDrawable(getContext(), iconBackFacingCamera);
    }

    public void useFrontFacingCamera(boolean front) {
        UIKitCore.getInstance().useFrontFacingCamera(front);
    }

    public void setOpenDrawable(Drawable switchCameraFrontImage) {
        this.frontDrawable = switchCameraFrontImage;
        update();
    }

    public void setCloseDrawable(Drawable switchCameraBackImage) {
        this.backDrawable = switchCameraBackImage;
        update();
    }
}
