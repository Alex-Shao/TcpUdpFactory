package com.moonearly.model;

/**
 * Created by Alex on 2017/11/10.
 */

public class UdpMsg {
    private String ip;
    private String name;

    public UdpMsg(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
