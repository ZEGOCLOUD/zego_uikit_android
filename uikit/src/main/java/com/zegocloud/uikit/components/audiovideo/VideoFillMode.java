package com.zegocloud.uikit.components.audiovideo;

public enum VideoFillMode {
    ASPECT_FIT(0),
    ASPECT_FILL(1),
    SCALE_TO_FILL(2);

    private int value;

    private VideoFillMode(int var3) {
        this.value = var3;
    }

    public int value() {
        return this.value;
    }

    public static VideoFillMode getZegoViewMode(int var0) {
        try {
            if (ASPECT_FIT.value == var0) {
                return ASPECT_FIT;
            } else if (ASPECT_FILL.value == var0) {
                return ASPECT_FILL;
            } else {
                return SCALE_TO_FILL.value == var0 ? SCALE_TO_FILL : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}

