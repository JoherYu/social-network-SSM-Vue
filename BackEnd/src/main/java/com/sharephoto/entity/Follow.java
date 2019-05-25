package com.sharephoto.entity;

import java.util.Date;

public class Follow extends FollowKey {
    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}