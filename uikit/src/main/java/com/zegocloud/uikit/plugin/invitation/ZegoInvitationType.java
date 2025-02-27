package com.zegocloud.uikit.plugin.invitation;

public enum ZegoInvitationType {
    VOICE_CALL(0), VIDEO_CALL(1);

    private int value;

    ZegoInvitationType(int var3) {
        this.value = var3;
    }

    public int getValue() {
        return this.value;
    }

    public static ZegoInvitationType getZegoInvitationType(int var0) {
        try {
            if (VOICE_CALL.value == var0) {
                return VOICE_CALL;
            } else if (VIDEO_CALL.value == var0) {
                return VIDEO_CALL;
            } else {
                return VOICE_CALL;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}
