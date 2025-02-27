package com.zegocloud.uikit.components.common;

public enum ZegoPresetResolution {
    PRESET_180P(0),
    PRESET_270P(1),
    PRESET_360P(2),
    PRESET_540P(3),
    PRESET_720P(4),
    PRESET_1080P(5);

    private int value;

    private ZegoPresetResolution(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static ZegoPresetResolution getPresetResolution(int value) {
        try {
            if (PRESET_180P.value == value) {
                return PRESET_180P;
            } else if (PRESET_270P.value == value) {
                return PRESET_270P;
            } else if (PRESET_360P.value == value) {
                return PRESET_360P;
            } else if (PRESET_540P.value == value) {
                return PRESET_540P;
            } else if (PRESET_720P.value == value) {
                return PRESET_720P;
            } else {
                return PRESET_1080P.value == value ? PRESET_1080P : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}
