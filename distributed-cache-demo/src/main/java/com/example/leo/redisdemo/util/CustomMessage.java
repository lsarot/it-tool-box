package com.example.leo.redisdemo.util;

import java.io.Serializable;

public class CustomMessage implements Serializable {
    private String message;

    public CustomMessage() {
    }

    public CustomMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
