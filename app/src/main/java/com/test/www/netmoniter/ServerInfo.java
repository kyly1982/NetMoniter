package com.test.www.netmoniter;

/**
 * Created by kyly on 2016/6/16.
 */
public class ServerInfo {
    private String name;
    private String Value;

    public ServerInfo(String name, String value) {
        this.name = name;
        Value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
