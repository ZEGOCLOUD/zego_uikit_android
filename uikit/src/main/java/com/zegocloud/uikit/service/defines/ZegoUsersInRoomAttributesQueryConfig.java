package com.zegocloud.uikit.service.defines;

public class ZegoUsersInRoomAttributesQueryConfig {

    /**
     * The query anchor for the room user attribute.
     * （Non-required, the first default is empty, which means start the query from the beginning.）
     */
    private String nextFlag;
    /**
     * The number of paging queries.
     * （Required field.）
     */
    private int count;

    public String getNextFlag() {
        return nextFlag;
    }

    public void setNextFlag(String nextFlag) {
        this.nextFlag = nextFlag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
