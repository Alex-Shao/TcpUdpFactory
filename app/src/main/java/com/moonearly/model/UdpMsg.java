package com.moonearly.model;

import java.io.Serializable;

/**
 * Created by Alex on 2017/11/10.
 */

public class UdpMsg implements Serializable{
    private String ip;
    private String name;
    private int type;

    public UdpMsg(String ip, String name, int type) {
        this.ip = ip;
        this.name = name;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
