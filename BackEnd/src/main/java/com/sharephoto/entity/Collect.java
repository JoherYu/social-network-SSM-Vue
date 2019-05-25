package com.sharephoto.entity;

import java.util.Date;

public class Collect extends CollectKey {
    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}