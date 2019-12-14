package com.man.dto.response;

import java.io.Serializable;

public class TokenResponse implements Serializable {
    private static final long serialVersionUID = 4440664413761381618L;
    private boolean flag = true;
    private String msg = "";
    private String token = "";

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
