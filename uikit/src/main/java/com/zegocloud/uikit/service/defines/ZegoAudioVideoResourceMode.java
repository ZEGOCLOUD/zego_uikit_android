package com.zegocloud.uikit.service.defines;

public enum ZegoAudioVideoResourceMode {
    DEFAULT(0), CDN_ONLY(1), L3_ONLY(2), RTC_ONLY(3), CDN_PLUS(4);

    private int value;

    ZegoAudioVideoResourceMode(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static ZegoAudioVideoResourceMode get(String value) {
        if (String.valueOf(ZegoAudioVideoResourceMode.CDN_ONLY.value).equals(value)) {
            return ZegoAudioVideoResourceMode.CDN_ONLY;
        } else if (String.valueOf(ZegoAudioVideoResourceMode.L3_ONLY.value).equals(value)) {
            return ZegoAudioVideoResourceMode.L3_ONLY;
        } else if (String.valueOf(ZegoAudioVideoResourceMode.RTC_ONLY.value).equals(value)) {
            return ZegoAudioVideoResourceMode.RTC_ONLY;
        } else if (String.valueOf(ZegoAudioVideoResourceMode.CDN_PLUS.value).equals(value)) {
            return ZegoAudioVideoResourceMode.CDN_PLUS;
        } else {
            return ZegoAudioVideoResourceMode.DEFAULT;
        }
    }

}
