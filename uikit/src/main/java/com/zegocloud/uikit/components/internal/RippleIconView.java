package com.zegocloud.uikit.components.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.ColorInt;
import com.zegocloud.uikit.R;

public class RippleIconView extends View {

    private static final int DEFAULT_TEXT_SIZE = 24;
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#222222");
    private static final int DEFAULT_RADIUS = 22;
    private static final int DEFAULT_RIPPLE_SIZE = 3;
    private static final int DEFAULT_RIPPLE_COLOR = Color.parseColor("#DBDDE3");

    private Paint paint;
    private String text;
    private int radius;
    private int textSize;
    private int rippleWidth;
    private int rippleCount = 3;
    private int circleBackgroundColor;
    private int rippleColor;
    private int textColor;
    // drawRipple or not
    private boolean drawRipple;
    // currentRipple width,dynamic when animate
    private int currentRippleWidth;
    private Runnable changeRippleRunnable;
    private Runnable hideRippleRunnable;
    private int strokeColor;
    private int strokeWidth;

    public RippleIconView(Context context) {
        super(context);
        this.radius = dpToPx(DEFAULT_RADIUS);
        this.textSize = spToPx(DEFAULT_TEXT_SIZE);
        this.rippleWidth = dpToPx(DEFAULT_RIPPLE_SIZE);
        this.textColor = DEFAULT_TEXT_COLOR;
        this.rippleColor = DEFAULT_RIPPLE_COLOR;
        this.drawRipple = true;
        initView();
    }

    public RippleIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleIconView);
        this.radius = a.getDimensionPixelSize(R.styleable.RippleIconView_circleRadius, dpToPx(DEFAULT_RADIUS));
        this.textSize = a.getDimensionPixelSize(R.styleable.RippleIconView_textSize, spToPx(DEFAULT_TEXT_SIZE));
        this.textColor = a.getColor(R.styleable.RippleIconView_textColor, DEFAULT_TEXT_COLOR);
        this.text = a.getString(R.styleable.RippleIconView_text);
        this.drawRipple = a.getBoolean(R.styleable.RippleIconView_drawRipple, true);
        this.rippleWidth = a.getDimensionPixelSize(R.styleable.RippleIconView_rippleWidth, dpToPx(DEFAULT_RIPPLE_SIZE));
        this.rippleColor = DEFAULT_RIPPLE_COLOR;
        this.strokeColor = a.getColor(R.styleable.RippleIconView_strokeColor, 0);
        this.strokeWidth = a.getDimensionPixelSize(R.styleable.RippleIconView_strokeWidth, 0);
        a.recycle();
        initView();
    }

    protected void initView() {
        circleBackgroundColor = generateColor(text);
        currentRippleWidth = 0;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleBackgroundColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        changeRippleRunnable = new Runnable() {
            @Override
            public void run() {
                int max = rippleWidth;
                int min = (int) (rippleWidth * 0.95f);
                if (max == min) {
                    min = max - 1;
                }
                if (currentRippleWidth == max) {
                    currentRippleWidth = min;
                } else {
                    currentRippleWidth = max;
                }
                setCurrentRippleWidth(currentRippleWidth);
            }
        };
        hideRippleRunnable = new Runnable() {
            @Override
            public void run() {
                setCurrentRippleWidth(0);
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth;
        int desiredHeight;
        if (drawRipple) {
            desiredWidth = 2 * (radius + rippleWidth * rippleCount);
            desiredHeight = 2 * (radius + rippleWidth * rippleCount);
        } else {
            desiredWidth = 2 * (radius);
            desiredHeight = 2 * (radius);
        }
        int width = resolveAdjustedSize(desiredWidth, widthMeasureSpec);
        int height = resolveAdjustedSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                   than max size imposed on ourselves.
                */
                result = desiredSize;
                break;
            case MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be larger than
                // the max size imposed on ourselves.
                result = Math.min(desiredSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private Rect r = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String iconKey = getIconKey(text);
        if (!TextUtils.isEmpty(iconKey)) {
            float viewCenterX = getWidth() / 2f;
            float viewCenterY = getHeight() / 2f;

            // draw ripple
            if (drawRipple) {
                paint.setColor(rippleColor);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(currentRippleWidth);
                for (int i = rippleCount; i > 0; i--) {
                    if (i == 1) {
                        paint.setAlpha(50);
                    } else if (i == 2) {
                        paint.setAlpha(25);
                    } else if (i == 3) {
                        paint.setAlpha(15);
                    }
                    canvas.drawCircle(viewCenterX, viewCenterY, radius + currentRippleWidth * (i - 0.5f), paint);
                }
            }
            // draw circle bg
            paint.setStyle(Style.FILL);
            paint.setColor(circleBackgroundColor);
            canvas.drawCircle(viewCenterX, viewCenterY, radius, paint);
            if (strokeWidth != 0) {
                paint.setStyle(Style.STROKE);
                paint.setColor(strokeColor);
                paint.setStrokeWidth(strokeWidth);
                canvas.drawCircle(viewCenterX, viewCenterY, radius - strokeWidth / 2f, paint);
            }

            // draw text in center
            paint.setStyle(Style.FILL);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            canvas.getClipBounds(r);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.getTextBounds(iconKey, 0, iconKey.length(), r);
            float x = viewCenterX - r.width() / 2f - r.left;
            float y = viewCenterY + r.height() / 2f - r.bottom;
            canvas.drawText(iconKey, x, y, paint);
        }
    }

    public void setCurrentRippleWidth(int currentRippleWidth) {
        this.currentRippleWidth = currentRippleWidth;
        invalidate();
    }

    public void startAnimation() {
        if (getHandler() != null) {
            getHandler().removeCallbacks(changeRippleRunnable);
            getHandler().postDelayed(changeRippleRunnable, 100);
        }
    }

    public void stopAnimation() {
        if (currentRippleWidth != 0) {
            if (getHandler() != null) {
                getHandler().postDelayed(hideRippleRunnable, 100);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }

    private int dpToPx(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
            getContext().getResources().getDisplayMetrics());
    }

    private int spToPx(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
            getContext().getResources().getDisplayMetrics());
    }

    public void setTextOnly(String name) {
        setText(name, false);
    }

    public void setText(String name, boolean updateBg) {
        this.text = name;
        if (updateBg) {
            circleBackgroundColor = generateColor(name);
        }
        invalidate();
    }

    public String getText() {
        return text;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public void setRippleWidth(int rippleWidth) {
        this.rippleWidth = rippleWidth;
        requestLayoutForce();
    }

    private void requestLayoutForce() {
        if (isInLayout()) {
            post(this::requestLayout);
        } else {
            requestLayout();
        }
    }

    public void setTextColor(@ColorInt int color) {
        textColor = color;
        invalidate();
    }

    protected String getIconKey(String name) {
        String key = "";
        if (!TextUtils.isEmpty(name)) {
            key = String.valueOf(name.charAt(0));
        }
        return key;
    }

    private @ColorInt
    int generateColor(String name) {
        //        return getColorFromString(getIconKey(name));
        return Color.parseColor("#DBDDE3");
    }

    public void setRadius(int radius) {
        this.radius = radius;
        requestLayoutForce();
    }

    public int getRadius() {
        return radius;
    }

    protected int getColorFromString(String text) {
        if (TextUtils.isEmpty(text)) {
            return Color.parseColor("#DBDDE3");
        } else {
            int index = (text.hashCode() % 7 + 7) % 7;
            switch (index) {
                case 0:
                    return 0xffb66fc0;
                case 1:
                    return 0xff9da9b3;
                case 2:
                    return 0xff88a5ed;
                case 3:
                    return 0xffeabe4b;
                case 4:
                    return 0xff699fd3;
                case 5:
                    return 0xff2b9f68;
                case 6:
                    return 0xffec6b61;
                default:
                    return 0xffec6b61;
            }
        }
    }
}
