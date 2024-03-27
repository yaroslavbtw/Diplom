package com.example.diplomaapp;

public class Devices {
    private String type;
    private String channel;

    public Devices(String type, String channel) {
        this.type = type;
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
