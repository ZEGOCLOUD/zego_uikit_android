package com.zegocloud.uikit.service.defines;

public enum ZegoAudioOutputDevice {
    SPEAKER(0),
    HEADPHONE(1),
    BLUETOOTH(2),
    EARPIECE(3),
    EXTERNAL_USB(4);

    private int value;

    private ZegoAudioOutputDevice(int var3) {
        this.value = var3;
    }

    public int value() {
        return this.value;
    }

    public static ZegoAudioOutputDevice getAudioOutputDevice(int var0) {
        try {
            if (SPEAKER.value == var0) {
                return SPEAKER;
            } else if (HEADPHONE.value == var0) {
                return HEADPHONE;
            } else if (BLUETOOTH.value == var0) {
                return BLUETOOTH;
            } else if (EARPIECE.value == var0) {
                return EARPIECE;
            } else {
                return EXTERNAL_USB.value == var0 ? EXTERNAL_USB : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}