package com.zegocloud.uikit.components.audiovideocontainer;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class ZegoLayoutPictureInPictureConfig extends ZegoLayoutConfig {

    public boolean isSmallViewDraggable = false;
    public int smallViewBackgroundColor = Color.parseColor("#333437");
    public int largeViewBackgroundColor = Color.parseColor("#4A4B4D");
    public transient Drawable smallViewBackgroundImage;
    public transient Drawable largeViewBackgroundImage;
    public ZegoViewPosition smallViewDefaultPosition = ZegoViewPosition.TOP_RIGHT;
    public boolean switchLargeOrSmallViewByClick = true;
    public Size smallViewSize = new Size(90, 135);
    public int spacingBetweenSmallViews = 8;
    public boolean removeViewWhenAudioVideoUnavailable = false;
}
