package com.zegocloud.uikit.components.audiovideocontainer;

import java.io.Serializable;

public class ZegoLayout implements Serializable {

    public ZegoLayoutMode mode;
    public ZegoLayoutConfig config;

    public ZegoLayout() {
        this(ZegoLayoutMode.GALLERY, new ZegoLayoutGalleryConfig());
    }

    public ZegoLayout(ZegoLayoutMode mode, ZegoLayoutConfig config) {
        this.mode = mode;
        this.config = config;
    }
}
