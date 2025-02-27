package com.zegocloud.uikit.components.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

/**
 * used to imply a imageButton with two status.Click the button will cause change from one to another.
 */
public class ZEGOImageButton extends ImageFilterView {

    protected GestureDetectorCompat gestureDetectorCompat;
    private long lastClickTime = 0;
    private static final int CLICK_INTERVAL = 200;
    private boolean isOpen = false;
    private Drawable openDrawable;
    private Drawable closeDrawable;

    public ZEGOImageButton(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZEGOImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZEGOImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

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
                boolean result = beforeClick();
                performClick();
                if (result) {
                    afterClick();
                }
                lastClickTime = System.currentTimeMillis();
                return true;
            }
        });
    }

    protected void afterClick() {
        toggle();
    }

    protected boolean beforeClick() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    public void setImageDrawable(Drawable openDrawable, Drawable closeDrawable) {
        this.openDrawable = openDrawable;
        this.closeDrawable = closeDrawable;
        updateState(isOpen);
    }

    public void setImageResource(@DrawableRes int openDrawable, @DrawableRes int closeDrawable) {
        if (openDrawable != 0) {
            this.openDrawable = ContextCompat.getDrawable(getContext(), openDrawable);
        } else {
            this.openDrawable = null;
        }
        if (closeDrawable != 0) {
            this.closeDrawable = ContextCompat.getDrawable(getContext(), closeDrawable);
        } else {
            this.closeDrawable = null;
        }
        updateState(isOpen);
    }

    public void setOpenImageResource(@DrawableRes int openDrawable) {
        if (openDrawable != 0) {
            this.openDrawable = ContextCompat.getDrawable(getContext(), openDrawable);
        } else {
            this.openDrawable = null;
        }
        updateState(isOpen);
    }

    public void setOpenDrawable(Drawable openDrawable) {
        this.openDrawable = openDrawable;
        updateState(isOpen);
    }

    public void setCloseImageResource(@DrawableRes int closeImageResource) {
        if (closeImageResource != 0) {
            this.closeDrawable = ContextCompat.getDrawable(getContext(), closeImageResource);
        } else {
            this.closeDrawable = null;
        }
        updateState(isOpen);
    }


    public void setCloseDrawable(Drawable closeDrawable) {
        this.closeDrawable = closeDrawable;
        updateState(isOpen);
    }

    /**
     * should override to imply the real function,no need to process UI change,because it is processed in Base class.
     */
    public void open() {
        this.isOpen = true;
        updateState(true);
    }

    /**
     * should override to imply the real function,no need to process UI change,because it is processed in Base class.
     */
    public void close() {
        this.isOpen = false;
        updateState(false);
    }

    /**
     * only update ui,usually no need to override
     */
    public void updateState(boolean state) {
        this.isOpen = state;
        if (state) {
            setImageDrawable(openDrawable);
        } else {
            setImageDrawable(closeDrawable);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * will be auto invoked when user click.usually no need to override
     */
    public void toggle() {
        boolean open = isOpen();
        if (open) {
            close();
        } else {
            open();
        }
    }
}
