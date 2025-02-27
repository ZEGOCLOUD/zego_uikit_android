package com.zegocloud.uikit.components.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

public class ZegoClickViewGroup extends FrameLayout {

    public ZegoClickViewGroup(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoClickViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZegoClickViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ZegoClickViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    protected GestureDetectorCompat gestureDetectorCompat;
    private long lastClickTime = 0;
    private static final int CLICK_INTERVAL = 200;

    protected void initView() {
        gestureDetectorCompat = new GestureDetectorCompat(getContext(), new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (System.currentTimeMillis() - lastClickTime < CLICK_INTERVAL) {
                    return true;
                }
                beforeClick();
                performClick();
                afterClick();
                lastClickTime = System.currentTimeMillis();
                return true;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }


    protected void afterClick() {

    }

    protected boolean beforeClick() {
        return true;
    }
}
