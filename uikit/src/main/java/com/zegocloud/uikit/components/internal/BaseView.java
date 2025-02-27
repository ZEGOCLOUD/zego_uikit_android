package com.zegocloud.uikit.components.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public abstract class BaseView extends AppCompatImageView {

    protected String mUserID;

    public BaseView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public BaseView(@NonNull Context context, String userID) {
        super(context);
        this.mUserID = userID;
        initView(context);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    protected void initView(Context context) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initWidgetListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unInitWidgetListener();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        View.OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                invokedWhenClick();
                if (l != null) {
                    l.onClick(v);
                }
            }
        };
        super.setOnClickListener(onClickListener);
    }

    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public abstract void initWidgetListener();

    protected abstract void unInitWidgetListener();

    public abstract void invokedWhenClick();
}
