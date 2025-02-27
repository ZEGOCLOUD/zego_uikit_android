package com.zegocloud.uikit.service.defines;

public enum ZegoScenario {

    /**
     * @deprecated
     */
    @Deprecated GENERAL(0),
    /**
     * @deprecated
     */
    @Deprecated COMMUNICATION(1),
    /**
     * @deprecated
     */
    @Deprecated LIVE(2), DEFAULT(3), STANDARD_VIDEO_CALL(4), HIGH_QUALITY_VIDEO_CALL(5), STANDARD_CHATROOM(
        6), HIGH_QUALITY_CHATROOM(7), BROADCAST(8), KARAOKE(9);

    private int value;

    private ZegoScenario(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static ZegoScenario getZegoScenario(int value) {
        try {
            if (GENERAL.value == value) {
                return GENERAL;
            } else if (COMMUNICATION.value == value) {
                return COMMUNICATION;
            } else if (LIVE.value == value) {
                return LIVE;
            } else if (DEFAULT.value == value) {
                return DEFAULT;
            } else if (STANDARD_VIDEO_CALL.value == value) {
                return STANDARD_VIDEO_CALL;
            } else if (HIGH_QUALITY_VIDEO_CALL.value == value) {
                return HIGH_QUALITY_VIDEO_CALL;
            } else if (STANDARD_CHATROOM.value == value) {
                return STANDARD_CHATROOM;
            } else if (HIGH_QUALITY_CHATROOM.value == value) {
                return HIGH_QUALITY_CHATROOM;
            } else if (BROADCAST.value == value) {
                return BROADCAST;
            } else {
                return KARAOKE.value == value ? KARAOKE : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}
