package com.example.leo.redisdemo.util;

import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

@REntity
public class LedgerLiveObject {
    @RId
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}