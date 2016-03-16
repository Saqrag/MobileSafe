package com.bug.mobilesafe.bean;

/**
 * Created by saqra on 2016/2/19.
 */
public class BlackListBean {
    private String name;
    private String number;
    private String mode;

    public BlackListBean(String name, String number, String mode) {
        this.name = name;
        this.number = number;
        this.mode = mode;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
